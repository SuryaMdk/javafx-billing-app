package com.str.billing.util;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupUtil {

    public static void backupDatabase(String sourcePath, String backupDir) {
        try {
            // Create backup directory if it doesn't exist
            Files.createDirectories(Paths.get(backupDir));

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFileName = "billing_backup_" + timestamp + ".db";

            Path source = Paths.get(sourcePath);
            Path backup = Paths.get(backupDir, backupFileName);

            Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Database backup created at: " + backup.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
