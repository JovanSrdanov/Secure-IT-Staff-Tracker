package pkibackend.pkibackend.Utilities;

import java.security.*;

public class Keys {
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

            //Na vise mesta sam nasao da je najbolje da prepustis odabir algoritma sistemu
            SecureRandom random = SecureRandom.getInstanceStrong();
            // preporuka je 3072 za digitalne potpise, a 2048 za enkripciju, ove brojke su preporucene do 2030. godine
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
