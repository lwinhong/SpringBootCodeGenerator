package com.toone.system.generator.myUpload;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UploadedInfo {
    /**
     * 文件id
     */
    private String fileId;
    /**
     * 原文件名
     */
    private String fileOldName;
    /**
     * 新文件名
     */
    private String fileNewName;
    /**
     * 文件路径
     */
    private String fileUrl;
    /**
     * 文件目录
     */
    private String fileDir;
    private String fileMD5;
    private String fileSize;
    /**
     * 文件真实路径
     */
    @JSONField(serialize = false)
    private String realFilePath;

    /**
     * 提供者
     */
    @JSONField(serialize = false)
    private String provider;

    @JSONField(serialize = false)
    private UploadDirInfo dirInfo;

    @JSONField(serialize = false)
    private FileCheckInfo checkInfo;

    @JSONField(serialize = false)
    private String relateFileId;
}
