package com.toone.system.generator.storage;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class StorageStrategyUtils {
    private final static AtomicLong atomicLong = new AtomicLong(System.currentTimeMillis());
    @Resource
    private StorageProperties storageConfig;

    @Value("${server.port}")
    private String port;
    private String ip = "127.0.0.1";

    @Value("${upload.requestPre}")
    private String requestPre;

    public String getNameStrategy(String originalName){
        String fileName = originalName.substring(0,originalName.lastIndexOf("."));
        String suffix = originalName.substring(originalName.lastIndexOf("."));
        String strategy = storageConfig.getStorageStrategy().getNameStrategy();
        if(strategy.equals(LocalStorageNameStrategyEnum.UUID.getStrategy())){
            return UUID.randomUUID().toString().replaceAll("-","") + suffix;
        }else if(strategy.equals(LocalStorageNameStrategyEnum.SEQ.getStrategy())){
            return atomicLong.incrementAndGet() + suffix;
        }else if(strategy.equals(LocalStorageNameStrategyEnum.ORIGINAL_NAME_AND_SEQ.getStrategy())){
            return fileName + "_" + atomicLong.incrementAndGet() + suffix;
        }else if(strategy.equals(LocalStorageNameStrategyEnum.SEQ_AND_ORIGINAL_NAME.getStrategy())){
            return atomicLong.incrementAndGet() + "_" + fileName + suffix;
        }
        // 默认
        return fileName;
    }
    public String getPathStrategy(String fileName, String dirId){
        String pathStrategy = storageConfig.getStorageStrategy().getPathStrategy();
        if(pathStrategy.equals(LocalStoragePathStrategyEnum.BY_DATE.getStrategy())){
            // 根据日期创建文件夹存放  yyMM/dd + fileName -> yyMM\\dd\\fileName
            String storageDateFormat = storageConfig.getStorageStrategy().getDateFormat();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(storageDateFormat);
            String dateFormatStr = LocalDate.now().format(formatter).replace("/",  File.separator).replace("\\",  File.separator);
            return dateFormatStr + File.separator +  (StringUtils.hasText(dirId)?dirId + File.separator:"") +fileName;
        }else if(pathStrategy.equals(LocalStoragePathStrategyEnum.BY_ID.getStrategy())){
            // 放在id文件夹中
            return dirId + File.separator + fileName;
        }else if(pathStrategy.equals(LocalStoragePathStrategyEnum.BY_NO.getStrategy())){
            // 全放到rootPath下
            return fileName;
        }
        // 默认文件夹名
        return "default" + File.separator + fileName;
    }

    /**
     * 根据保存路径返回前端访问它的url
     * @param fileSavePath 文件完整保存路径
     * @return
     */
    public String getRequestPath(String fileSavePath) {
        // 保存上传的文件，前端图片请求前缀,mvc资源的请求路径映射也是requestPre
        return "http://" + ip + ":" + port + requestPre + "/" + fileSavePath.replaceAll("\\\\","/");
    }
}


