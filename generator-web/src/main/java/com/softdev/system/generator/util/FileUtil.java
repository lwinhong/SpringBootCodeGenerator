package com.softdev.system.generator.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

@Slf4j
public class FileUtil {
    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String getFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
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
}
