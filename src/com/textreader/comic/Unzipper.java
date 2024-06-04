package com.textreader.comic;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Unzipper {
    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.createNewFile();
        }
        if (!destDir.isDirectory()) {
            destDir = destDir.getParentFile();
        }
        ZipFile zipFile = new ZipFile(zipFilePath);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                extractFile(zipFile, entry, filePath);
            } else {
                File dir = new File(filePath);
                dir.mkdirs();
            }
        }

        zipFile.close();
    }

    private static void extractFile(ZipFile zipFile, ZipEntry entry, String filePath) throws IOException {
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;

        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            bos = new BufferedOutputStream(fos);
            bis = new BufferedInputStream(zipFile.getInputStream(entry));

            byte[] bytesIn = new byte[1024];
            int read = 0;
            while ((read = bis.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        } finally {
            if (bos != null) {
                bos.close();
            }
            if (bis != null) {
                bis.close();
            }
        }
    }
}