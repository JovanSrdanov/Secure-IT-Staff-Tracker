package pkibackend.pkibackend.Utilities;

import java.io.*;
public class UniqueFIleCreator {
    public static File createUniqueFile(String baseFileName) {
        File file = new File(baseFileName);
        String parentDirectory = file.getParent();
        String fileName = file.getName();
        String fileExtension = "";

        // Extract file extension if present
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            fileExtension = fileName.substring(dotIndex);
            fileName = fileName.substring(0, dotIndex);
        }

        // Create a unique file
        int counter = 0;
        File uniqueFile;
        do {
            counter++;
            String uniqueFileName = fileName + (counter > 1 ? "(" + counter + ")" : "") + fileExtension;
            uniqueFile = new File(parentDirectory, uniqueFileName);
        } while (uniqueFile.exists());

        return uniqueFile;
    }
}