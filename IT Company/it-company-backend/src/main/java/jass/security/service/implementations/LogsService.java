package jass.security.service.implementations;

import jass.security.service.interfaces.ILogsService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.time.format.DateTimeFormatter;

@Service
@Primary
public class LogsService implements ILogsService {
    // Goes through the logs folder, unzipps needed files and extracts all logs from them
    @Override
    public List<String> getAllLogs() throws IOException {
        List<String> logs = new ArrayList<>();
        String path = getLogsFolderFullPath();
        File logsFolder = new File(path);
        File[] logFiles = logsFolder.listFiles();

        if (logFiles != null) {
            for (File logFile : logFiles) {
                if (isPlainTextFile(logFile)) {
                    extractLogsFromPlainTextFiles(logs, logFile);
                    // if a file is a folder, it only contains zipped files
                } else if (logFile.isDirectory()) {
                    extractLogsFromZippedFiles(logs, logFile);
                }
            }
        }

        return logs;
    }

    private static String getLogsFolderFullPath() {
        return Paths.get(System.getProperty("user.dir"), "logs").normalize().toString();
    }

    private void extractLogsFromPlainTextFiles(List<String> logs, File logFile) throws IOException {
        // TODO Stefan: make it so logs cannot be changed
        // gets the time when the current log was last changed, should only be the date of it's creation
        LocalDateTime fileLastModifiedTime = getFileLastModifiedTime(logFile);
        // checks if the log is older than 12 hours
        if (isNewerThanHours(fileLastModifiedTime)) {
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line;
            while ((line = reader.readLine()) != null) {
                logs.add(line);
            }
        }
    }

    private void extractLogsFromZippedFiles(List<String> logs, File logFile) throws IOException {
        // all zipped files have the extension .log.gz
        File[] compressedLogs = logFile.listFiles((dir, name) -> name.endsWith(".log.gz"));
        if (compressedLogs != null) {
            for (File compressedLog : compressedLogs) {
                if (isNewerThanTwoDays(compressedLog)) {
                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    LocalDateTime fileLastModifiedTime = getFileLastModifiedTime(compressedLog);
                    if (isNewerThanHours(fileLastModifiedTime)) {
                        GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(compressedLog));
                        BufferedReader reader = new BufferedReader
                                (new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            logs.add(line);
                        }
                    }   
                }
            }
        }
    }

    private Boolean isNewerThanTwoDays(File compressedLog) {
        String fileName = compressedLog.getName();
        String[] parts = fileName.split("-");

        if (parts.length >= 4) {
            String dayString = parts[2];
            String monthString = parts[3];
            String yearString = parts[4];
            int day = Integer.parseInt(dayString);
            int month = convertMonthStringToNumber(monthString);
            int year = Integer.parseInt(yearString);
            LocalDateTime fileLastModifiedTime = LocalDateTime.of(year, month, day, 0, 0);
            LocalDateTime currentTime = LocalDateTime.now();
            return Duration.between(fileLastModifiedTime, currentTime).compareTo(Duration.ofDays(1)) <= 0;
        }

        return false;
    }

    private int convertMonthStringToNumber(String monthString) {
        String[] monthNames = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        for (int i = 0; i < monthNames.length; i++) {
            if (monthString.equalsIgnoreCase(monthNames[i])) {
                return i + 1;
            }
        }

        // Default to January if the month name is not recognized
        return 1;
    }

    private LocalDateTime getFileLastModifiedTime(File compressedLog) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(compressedLog.lastModified()), ZoneId.systemDefault());
    }

    private Boolean isNewerThanHours(LocalDateTime fileLastModifiedTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        return Duration.between(fileLastModifiedTime, currentTime).compareTo(Duration.ofHours(12)) <= 0;
    }

    private Boolean isPlainTextFile(File logFile) {
        // if a file is not zipped, it's extension is .log (only current log is not zipped, every time the application is started,
        // the file gets too large, or a new day/month begins, current .log file is zipped and a new one gets created
        return logFile.isFile() && logFile.getName().endsWith(".log");
    }
}
