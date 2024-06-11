package com.softdev.system.generator.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties("file-storage")
public class StorageProperties {
    @NestedConfigurationProperty
    private final LocalStorageStrategy storageStrategy = new LocalStorageStrategy();
    @Data
    public static class LocalStorageStrategy {
        private String rootPath;
        private String pathStrategy = LocalStoragePathStrategyEnum.BY_DATE.getStrategy();
        private String nameStrategy = LocalStorageNameStrategyEnum.SEQ_AND_ORIGINAL_NAME.getStrategy();
        private String dateFormat = "YYYY-MM-dd hh:mm:ss";
    }
}

