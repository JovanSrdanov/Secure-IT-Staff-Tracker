package jass.security.utils;

import java.util.Random;

public class RandomPasswordGenerator {

    public static String generatePassword(int length) {
        String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialCharacters = "!_";

        String allCharacters = uppercaseLetters + lowercaseLetters + numbers + specialCharacters;

        Random random = new Random();
        StringBuilder password = new StringBuilder();

        // Generate at least one character from each character set
        password.append(getRandomChar(uppercaseLetters, random));
        password.append(getRandomChar(lowercaseLetters, random));
        password.append(getRandomChar(numbers, random));
        password.append(getRandomChar(specialCharacters, random));

        // Generate remaining characters randomly
        for (int i = 4; i < length; i++) {
            int randomIndex = random.nextInt(allCharacters.length());
            password.append(allCharacters.charAt(randomIndex));
        }

        return password.toString();
    }

    public static char getRandomChar(String characters, Random random) {
        int randomIndex = random.nextInt(characters.length());
        return characters.charAt(randomIndex);
    }
}