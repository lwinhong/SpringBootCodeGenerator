package com.toone.system.generator.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.security.MessageDigest;

@Slf4j
public class FileMd5Util {

    public static String getMD5(String path) {
        return getMD5(new File(path));
    }

    public static String getMD5(File path) {
        try (var fis = new FileInputStream(path)) {
            return getMD5(fis);
        } catch (Exception e) {
            log.error("IO异常", e);
        }
        return null;
    }

    public static String getMD5(InputStream stream) {
        try {
            return DigestUtils.md5Hex(stream);
        } catch (FileNotFoundException e) {
            log.error("没有文件", e);
        } catch (IOException e) {
            log.error("IO异常", e);
        }
        return null;
    }

    /**
     * 获取一个文件的md5值(可处理大文件)
     * @return md5 value
     */
    public static String getMD54BigFile(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (fileInputStream != null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 求一个字符串的md5值
     * @param target 字符串
     * @return md5 value
     */
    public static String MD5(String target) {
        return DigestUtils.md5Hex(target);
    }
}
