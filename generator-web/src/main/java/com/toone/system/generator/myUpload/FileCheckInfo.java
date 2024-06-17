package com.toone.system.generator.myUpload;

import com.toone.system.generator.entity.DbFiles;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileCheckInfo {
    private String md5;
    private Boolean exist;
    private DbFiles dbFiles;
    private UploadedInfo uploadedInfo;
}
