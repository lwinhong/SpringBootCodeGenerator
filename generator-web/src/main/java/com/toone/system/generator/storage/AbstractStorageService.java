package com.toone.system.generator.storage;

public abstract class AbstractStorageService implements StorageService {
    // 配置属性类
    protected StorageProperties storageConfig;

    public AbstractStorageService(StorageProperties storageProperties) {
        this.storageConfig = storageProperties;
        this.init();  // 初始化
    }

    /**
     * 子实现类初始化方法
     */
    abstract void init();
}

