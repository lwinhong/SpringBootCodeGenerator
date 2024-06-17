package com.toone.system.generator.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class LocalStorageServiceImpl extends AbstractStorageService{
    // 配置中存储路径的Path对象
    private Path rootLocation;
    public LocalStorageServiceImpl(StorageProperties storageProperties) {
        super(storageProperties);
    }

    /**
     * 父类构造函数执行中自动初始化的启动代码
     */
    @Override
    public void init(){
        try{
            // 父类中获取properties文件保存根路径,提前创建目录
            String path = this.storageConfig.getStorageStrategy().getRootPath();
            this.rootLocation = Paths.get(path);
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void upload(InputStream inputStream, String savePath) {
        try {
            // 得到要保存文件的父目录 = files + 路径策略
            // 1. 创建父目录（多线程）  2.创建/替换文件
            Path filePath = this.rootLocation.resolve(savePath);
            Path dirPath = filePath.getParent();
            if(!Files.exists(dirPath) || !Files.isDirectory(dirPath)){
                // 目录还不存在需要创建 ,两个线程有可能同时执行到这里，获取锁后还应二次判断
                synchronized (dirPath.toAbsolutePath().toString().intern()){
                    if(!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                        try {
                            Files.createDirectories(dirPath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            Files.copy(inputStream,filePath, StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public byte[] download(String filePath){
        // TODO
        return null;
    }
}

