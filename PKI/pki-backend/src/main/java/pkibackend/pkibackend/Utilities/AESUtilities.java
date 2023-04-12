package pkibackend.pkibackend.Utilities;

import io.github.cdimascio.dotenv.Dotenv;
import pkibackend.pkibackend.dto.AESPasswordDto;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESUtilities {
    private SecretKey secretKey;
    private int blockSize;
    private String algorithm;

    public AESUtilities(){
        // Mora ovako da se cita a ne iz properties zato sto AESUtilities nije komponenta, mi je sami instanciramo
        // pa ne moze da se autowireuje @Value, a ako se ne radi preko @Value, ne moze da se resolvuje promenljiva
        // koja ima referencu na .env fajl
        Dotenv dotenv = Dotenv.configure().directory("src/main/resources/.env").load();
        String secretKeyStr = dotenv.get("KEYSTORE_PASSWORD_SECRET_KEY");
        secretKey = stringToSecretKey(secretKeyStr);

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

    public AESPasswordDto encrypt(String clearText){
        try {
            IvParameterSpec iv = generateIv();
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] password = cipher.doFinal(clearText.getBytes());
            return new AESPasswordDto(password, iv.getIV());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    public  String decrypt(AESPasswordDto aesPasswordDto){
        try {
            IvParameterSpec iv = new IvParameterSpec(aesPasswordDto.getIv());
            Cipher cipher = null;
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return new String(cipher.doFinal(aesPasswordDto.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    public IvParameterSpec generateIv() {
        byte[] iv = new byte[blockSize];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}
