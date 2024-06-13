package com.softdev.system.generator.service;

import com.softdev.system.generator.entity.ReturnT;
import com.softdev.system.generator.myUpload.UploadedInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CodeToFileService {
    ReturnT generateByUploadFile(String options, List<UploadedInfo> uploadedInfoList,
                                 HttpServletRequest request) throws Exception;

    UploadedInfo generateBySql(String content, String options) throws Exception;
}
