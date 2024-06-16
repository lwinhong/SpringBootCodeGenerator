package com.softdev.system.generator.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;


@Slf4j
public class FileUtil {
    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String getFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getNewFileId() {
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
     * 支持单文件或多层文件夹的压缩
     *
     * @param srcPath
     * @param targetPath
     */
    public static Boolean zipFile(String srcPath, String targetPath) {
        int length;
        File file = new File(srcPath);
        List<File> filesToArchive;
        if (file.isDirectory()) {
            filesToArchive = getAllFile(new File(srcPath));
            length = srcPath.length();
        } else {
            filesToArchive = Collections.singletonList(file);
            length = file.getParent().length() + 1;
        }
        try (ArchiveOutputStream<ZipArchiveEntry> o = new ZipArchiveOutputStream(new File(targetPath))) {
            for (File f : filesToArchive) {
                ZipArchiveEntry entry = o.createArchiveEntry(f, f.getPath().substring(length));
                o.putArchiveEntry(entry);
                if (f.isFile()) {
                    try (InputStream i = Files.newInputStream(f.toPath())) {
                        IOUtils.copy(i, o);
                    }
                }
                o.closeArchiveEntry();
            }
        } catch (IOException e) {
            log.error("zipFile fails", e);
            return false;
        }
        return true;
    }

    /**
     * 递归获取文件夹下所有文件
     * @param dirFile
     * @return
     */
    public static List<File> getAllFile(File dirFile) {
        File[] childrenFiles = dirFile.listFiles();
        if (Objects.isNull(childrenFiles) || childrenFiles.length == 0) {
            return Collections.emptyList();
        }
        List<File> files = new ArrayList<>();
        for (File childFile : childrenFiles) {
            if (childFile.isFile()) {
                files.add(childFile);
            } else {
                files.add(childFile);
                List<File> cFiles = getAllFile(childFile);
                if (cFiles.isEmpty()) {
                    continue;
                }
                files.addAll(cFiles);
            }
        }
        return files;
    }

    /**
     * 删除文件夹
     *
     * @param path 文件夹路径
     * @throws IOException
     */
    public static void deleteDirectory(Path path) throws IOException {
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(FileUtil::deleteFile);
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    public static void deleteFile(Path filePath) {
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            log.error("无法删除的路径 %s%n%s", filePath, e);
        }
    }
}
