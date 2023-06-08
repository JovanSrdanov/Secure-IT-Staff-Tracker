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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

@Service
@Primary
public class CvService implements ICvService {
    private final SecretKey secretKey;
    private final int blockSize;
    private final String algorithm;
    private final ISwEngineerRepository swEngineerRepository;
    private final ICvRepository cvRepository;
public CvService(@Value("${cvAesKey}") String secretKeyStr, ICvRepository cvRepository, ISwEngineerRepository swEngineerRepository, ICvRepository cvRepository1){

    this.secretKey = stringToSecretKey(secretKeyStr);
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

    private String getCvDir() {
        String runtimeDir = new File(getClass().getResource("").getPath()).getPath();
        String rootDir = runtimeDir.substring(0, runtimeDir.indexOf("target" + File.separator + "classes"));
        return rootDir + "cv" + File.separator;
    }

    @Override
    public void save(MultipartFile file, UUID engineerId) throws IOException, NotFoundException {
        CvAesDto dto = encrypt(file.getBytes());
        String filePath = getCvDir() + "cv_" + engineerId.toString();

        try(FileOutputStream fos = new FileOutputStream(filePath)){
           fos.write(dto.getEncryptedCv());
           fos.flush();
        }
        catch (IOException e){
           throw new RuntimeException(e.getMessage());
        }

        Cv cv = new Cv(UUID.randomUUID(), dto.getAesInitVector());
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
        byte[] aesInitVector = engineerResult.get().getCv().getAesInitVector();

        String filePath = getCvDir() + "cv_" + engineerId.toString();
        byte[] encryptedCv =  Files.readAllBytes(Path.of(filePath));

        return decrypt(new CvAesDto(encryptedCv, aesInitVector));
    }

    @Override
    public CvAesDto encrypt(byte[] cv) {
        try {
            IvParameterSpec iv = generateIv();
            Cipher cipher = null;
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encryptedCv = cipher.doFinal(cv);
            return new CvAesDto(encryptedCv, iv.getIV());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] decrypt(CvAesDto dto) {
        try{
        IvParameterSpec iv = new IvParameterSpec(dto.getAesInitVector());
        Cipher cipher = null;
        cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
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

}
