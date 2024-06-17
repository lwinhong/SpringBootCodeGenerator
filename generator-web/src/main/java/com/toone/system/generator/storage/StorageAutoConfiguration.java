package com.toone.system.generator.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConfigurationPropertiesScan("com.toone.system.generator.storage") // 自己项目的storage文件夹路径
public class StorageAutoConfiguration {
    @Bean
    // 如果存在这个配置file-storage.storageStrategy.rootPath
    @ConditionalOnProperty(prefix = "file-storage.storageStrategy",name = "rootPath")
    // 确保单例
    @ConditionalOnMissingBean
    public StorageService storageService(StorageProperties storageProperties){
        // storageProperties是Spring中自动参数注入 ↑
        return new LocalStorageServiceImpl(storageProperties);
    }
}

