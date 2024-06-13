package com.softdev.system.generator.myUpload;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class UploadedInfo {
    private String fileOldName;
    private String fileNewName;
    private String fileUrl;
    private String fileDir;

    @JSONField(serialize = false)
    private String realFilePath;

//    /**
//     * 生成文件路径
//     */
//    @JSONField(serialize = false)
//    private String generatedFilePath;
//    private String generatedFileUrl;
}
