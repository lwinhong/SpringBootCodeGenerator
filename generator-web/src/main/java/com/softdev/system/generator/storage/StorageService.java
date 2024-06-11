package com.softdev.system.generator.storage;

import java.io.InputStream;

public interface StorageService {
    /**
     * 图片上传
     *
     * @param inputStream 文件流
     * @param filePath    文件保存的完整路径
     */
    void upload(InputStream inputStream, String filePath);

    /**
     * 下载目录文件
     *
     * @param filePath 文件路径
     * @return 文件字节数据
     */
    byte[] download(String filePath);
}
