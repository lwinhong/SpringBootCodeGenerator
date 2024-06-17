package com.toone.system.generator.myUpload;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;

@Data
@Accessors(chain = true)
public class UploadDirInfo {
    private File folder;
    private String relativeFolder;
    private String relativeFolderWithoutUploadRoot;
}
