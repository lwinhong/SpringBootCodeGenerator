package com.softdev.system.generator.service;

import com.alibaba.fastjson2.JSONObject;
import com.softdev.system.generator.entity.ParamInfo;
import com.softdev.system.generator.entity.ReturnT;
import com.softdev.system.generator.myUpload.UploadedInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface CodeToFileService {
    ReturnT generateByUploadFile(String options, List<UploadedInfo> uploadedInfoList,
                                 HttpServletRequest request) throws Exception;

    UploadedInfo generateBySql(String content,  Map<String, Object> options) throws Exception;
    ReturnT generateBySql(ParamInfo paramInfo,
                          HttpServletRequest request) throws Exception;
}
