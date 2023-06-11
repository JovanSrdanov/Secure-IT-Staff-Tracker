package jass.security.service.implementations;

import jass.security.dto.CvAesDto;
import jass.security.exception.NotFoundException;
import jass.security.model.Cv;
import jass.security.model.SoftwareEngineer;
import jass.security.repository.ICvRepository;
import jass.security.repository.ISwEngineerRepository;
import jass.security.service.interfaces.ICvService;
import jass.security.utils.Base64Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.Certificate;
import java.util.UUID;

@Service
@Primary
public class CvService implements ICvService {
    private final int blockSize;
    //In bytes
    private final int keySize;
    private final String algorithm;
    private final ISwEngineerRepository swEngineerRepository;
    private final ICvRepository cvRepository;

    @Value("${cvKeyStoreName}")
    private   String keyStoreName;
    @Value("${cvKeyStorePassword}")
    private String keyStorePassword;
    @Value("${cvCertificateAlias}")
    private String certAlias;
    @Value("${cvPrivateKeyPassword}")
    private String cvPrivateKeyPassword;
public CvService(ICvRepository cvRepository, ISwEngineerRepository swEngineerRepository, ICvRepository cvRepository1){
    this.keyStorePassword = keyStorePassword;
    this.keySize = 16;
        this.swEngineerRepository = swEngineerRepository;
        this.cvRepository = cvRepository1;
        this.blockSize = 16;
        this.algorithm = "AES/CBC/PKCS5Padding";
    }


    private SecretKey stringToSecretKey(String secretKeyStr) {
        byte[] secretKeyByte;
        try {
            secretKeyByte= Base64Utility.decode(secretKeyStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new SecretKeySpec(secretKeyByte, 0, secretKeyByte.length, "AES");
    }

    private SecretKey bytesToSecretKey(byte[] bytes){
        return new SecretKeySpec(bytes, 0, bytes.length, "AES");
    }

    private String getCvDir() {
        String runtimeDir = new File(getClass().getResource("").getPath()).getPath();
        String rootDir = runtimeDir.substring(0, runtimeDir.indexOf("target" + File.separator + "classes"));
        return rootDir + "cv" + File.separator;
    }

    @Override
    public void save(MultipartFile file, UUID engineerId) throws IOException, NotFoundException {
        CvAesDto dto = encryptAes(file.getBytes());
        String filePath = getCvDir() + "cv_" + engineerId.toString();

        try(FileOutputStream fos = new FileOutputStream(filePath)){
           fos.write(dto.getEncryptedCv());
           fos.flush();
        }
        catch (IOException e){
           throw new RuntimeException(e.getMessage());
        }
        byte[] encryptedSecretKey = encryptRsa(dto.getSecretKey());
        Cv cv = new Cv(UUID.randomUUID(), encryptedSecretKey, dto.getAesInitVector());
        cvRepository.save(cv);

        //Linking cv to engineer
        var engineerResult =  swEngineerRepository.findById(engineerId);
        if(engineerResult.isEmpty()){
            throw new NotFoundException("Engineer not found");
        }
        SoftwareEngineer engineer = engineerResult.get();
        engineer.setCv(cv);
        swEngineerRepository.save(engineer);
    }

    @Override
    public byte[] read(UUID engineerId) throws IOException, NotFoundException {
        var engineerResult =  swEngineerRepository.findById(engineerId);
        if(engineerResult.isEmpty()){
            throw new NotFoundException("Engineer not found");
        }

        Cv cv = engineerResult.get().getCv();
        if (cv == null){
            throw new NotFoundException("There is currently no available cv");
        }

        byte[] encryptedSecretKey = cv.getSecretKey();
        byte[] secretKey = decryptRsa(encryptedSecretKey);

        byte[] aesInitVector = cv.getAesInitVector();

        String filePath = getCvDir() + "cv_" + engineerId.toString();
        byte[] encryptedCv =  Files.readAllBytes(Path.of(filePath));

        return decryptAes(new CvAesDto(encryptedCv, secretKey, aesInitVector));
    }

    private SecretKey generateSecretKey(){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize * 8);
            return  keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CvAesDto encryptAes(byte[] cv) {
        try {
            SecretKey secretKey = generateSecretKey();
            IvParameterSpec iv = generateIv();
            Cipher cipher = null;
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encryptedCv = cipher.doFinal(cv);
            return new CvAesDto(encryptedCv,secretKey.getEncoded(),iv.getIV());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] decryptAes(CvAesDto dto) {
        try{
        IvParameterSpec iv = new IvParameterSpec(dto.getAesInitVector());
        Cipher cipher = null;
        cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, bytesToSecretKey(dto.getSecretKey()), iv);
        return cipher.doFinal(dto.getEncryptedCv());
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public IvParameterSpec generateIv() {
        byte[] iv = new byte[blockSize];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private byte[] encryptRsa(byte[] data){
        try {

            PublicKey publicKey = loadPublicKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);

        } catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }

    }

    private byte[] decryptRsa(byte[] data){
        try {

            PrivateKey privateKey =  loadPrivateKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);

        } catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private KeyStore loadKeystore() throws Exception{
        KeyStore keyStore = KeyStore.getInstance("PKCS12", "SunJSSE");

        String runtimeDir = new File(getClass().getResource("").getPath()).getPath();
        String rootDir = runtimeDir.substring(0, runtimeDir.indexOf("target" + File.separator + "classes"));
        String keystorePath = rootDir + "src" + File.separator + "main" + File.separator + "resources" + File.separator +
                "cv" + File.separator + keyStoreName;
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(keystorePath));
        keyStore.load(in, keyStorePassword.toCharArray());
        return keyStore;
    }

    private PublicKey loadPublicKey() {
        try {
            KeyStore keyStore = loadKeystore();

            if (keyStore.isKeyEntry(certAlias)) {
                Certificate cert = keyStore.getCertificate(certAlias);
                return cert.getPublicKey();
            } else{
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private PrivateKey loadPrivateKey() {
        try {
            KeyStore keyStore = loadKeystore();

            if (keyStore.isKeyEntry(certAlias)) {
                return (PrivateKey) keyStore.getKey(certAlias, cvPrivateKeyPassword.toCharArray());
            } else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
