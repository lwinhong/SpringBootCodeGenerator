package com.toone.system.generator.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;

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
     * 压缩文件夹下的所有文件
     * @param dir          要压缩的文件夹
     * @param outputStream 输出压缩后的文件流
     * @throws IOException      IO异常
     * @throws ArchiveException 压缩异常
     */
    public static void zip(File dir, OutputStream outputStream) throws IOException, ArchiveException {
        ZipArchiveOutputStream zipOutput = null;
        try {
            zipOutput = new ArchiveStreamFactory()
                    .createArchiveOutputStream(ArchiveStreamFactory.ZIP, outputStream);
            zipOutput.setEncoding("utf-8");
            zipOutput.setUseZip64(Zip64Mode.AsNeeded);
            Collection<File> files = FileUtils.listFilesAndDirs(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

            for (File file : files) {
                InputStream in = null;
                try {
                    if (file.getPath().equals(dir.getPath())) {
                        continue;
                    }
                    String relativePath = StringUtils.replace(file.getPath(), dir.getPath() + File.separator, "");
                    ZipArchiveEntry entry = new ZipArchiveEntry(file, relativePath);
                    zipOutput.putArchiveEntry(entry);
                    if (file.isDirectory()) {
                        zipOutput.closeArchiveEntry();
                        continue;
                    }

                    in = new FileInputStream(file);
                    IOUtils.copy(in, zipOutput);
                    zipOutput.closeArchiveEntry();
                } finally {
                    if (in != null) {
                        IOUtils.closeQuietly(in);
                    }
                }
            }
            zipOutput.finish();
        } finally {
            IOUtils.closeQuietly(zipOutput);
        }
    }

    public static Boolean zip(String sourceFolderPath, String zipFilePath) {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath)) {
            zip(new File(sourceFolderPath), fos);
        } catch (Exception e) {
            log.error("zipFile fails", e);
            return false;
        }
        return true;
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
