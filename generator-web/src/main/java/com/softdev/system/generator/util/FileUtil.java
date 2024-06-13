package com.softdev.system.generator.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileUtil {
    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String getFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getNewFileId(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getContent(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            System.err.println("读取文件内容失败:{}" + filePath + e.getMessage());
            log.error("读取文件内容失败:{}", filePath, e);
        }
        return sb.toString();
    }

    public static void writeFile(String filePath, String content) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());//确保文件夹生成
            Files.write(path, Collections.singletonList(content));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 功能:压缩多个文件成一个zip文件
     *
     * @param srcfile：源文件列表
     * @param zipfile：压缩后的文件
     */
    public static void zipFiles(File[] srcfile, File zipfile) throws Exception {
        byte[] buf = new byte[1024];
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile))) {
            //ZipOutputStream类：完成文件或文件夹的压缩

            for (File file : srcfile) {
                try (FileInputStream in = new FileInputStream(file)) {
                    // 给列表中的文件单独命名
                    out.putNextEntry(new ZipEntry(file.getName()));
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.closeEntry();
                }
            }
            out.close();
            System.out.println("压缩完成.");
        } catch (Exception e) {
            log.error("压缩失败", e);
            throw e;
        }
    }
}
