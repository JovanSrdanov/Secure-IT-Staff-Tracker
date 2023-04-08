package pkibackend.pkibackend.Utilities;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";

    public static String generatePassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int passwordLength = 10; // Length of the generated password
        String password = generatePassword(passwordLength);
        System.out.println("Generated Password: " + password);
    }
}
