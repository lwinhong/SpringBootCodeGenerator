package com.softdev.system.generator.service;

import com.alibaba.fastjson2.JSONObject;
import com.softdev.system.generator.entity.ParamInfo;
import com.softdev.system.generator.entity.ReturnT;
import com.softdev.system.generator.myUpload.MyUploadFileUtil;
import com.softdev.system.generator.myUpload.UploadedInfo;
import com.softdev.system.generator.util.FileUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.File;
import java.util.*;

@Service
@Slf4j
@CrossOrigin("*")
public class CodeToFileServiceImpl extends AbsCodeToFileService {

    private final GeneratorService generatorService;
    private final MyUploadFileUtil fileUtil;

    @Autowired
    public CodeToFileServiceImpl(MyUploadFileUtil fileUtil, GeneratorService generatorService) {
        this.fileUtil = fileUtil;
        this.generatorService = generatorService;
    }

    @Override
    public ReturnT generateByUploadFile(String options, List<UploadedInfo> uploadedInfoList,
                                        HttpServletRequest request) throws Exception {
        var list = new ArrayList<>();
        var generateList = new ArrayList<UploadedInfo>();
        for (UploadedInfo uploadedInfo : uploadedInfoList) {
            var fileContent = FileUtil.getContent(uploadedInfo.getRealFilePath());
            var generateInfo = generateBySql(fileContent, options);
            if (generateInfo == null) {
                throw new Exception("生成失败");
            }

            var dir = generateInfo.getFileDir().replace("\\", "/")
                    + generateInfo.getFileNewName();
            var downloadUrl = fileUtil.buildUrl(request, dir);
            list.add(Map.of(
                    "fileUrl", downloadUrl,
                    "fileId", generateInfo.getFileId()
                    /*"uploadFile", uploadedInfo*/));
            generateList.add(generateInfo);
        }
        try {
            fileUtil.saveFileInfoToDb(generateList);
        } catch (Exception e) {
            generateList.forEach(file -> fileUtil.deleteUploadedFile(file));
            throw new RuntimeException("保存文件信息到数据库失败", e);
        }
        return ReturnT.ok().put("msg", list);
    }

    @Override
    public UploadedInfo generateBySql(String content, String options) throws Exception {
        var param = new ParamInfo();
        param.setTableSql(content);
        param.setOptions(JSONObject.parse(options));
        var result = generatorService.generateCodeCore(param);
        if (result != null && !result.isEmpty()) {
            return zipToLocal(result);
        }
        return null;
    }

    private UploadedInfo zipToLocal(Map<String, String> result) throws Exception {
        var uploadDir = fileUtil.buildDir();
        var id = FileUtil.getNewFileId();
        File tmpDir = new File(uploadDir.getFolder().getAbsolutePath() + File.separator + id);
        var files = new ArrayList<File>();
        for (Map.Entry<String, String> entry : result.entrySet()) {
            var fileName = entry.getKey();
            var fileContent = entry.getValue();
            var filePath = tmpDir.getAbsolutePath() + "/" + fileName;
            FileUtil.writeFile(filePath, fileContent);
            files.add(new File(filePath));
        }
        var zipPath = tmpDir.getAbsolutePath() + ".zip";
        FileUtil.zipFiles(files.toArray(new File[0]), new File(zipPath));
        try {
            tmpDir.delete();
        } catch (Exception e) {
            log.info("删错异常（可忽略）：{},{}", tmpDir.getAbsolutePath(), e.getMessage());
        }
        File zipFile = new File(zipPath);
        return new UploadedInfo()
                .setFileOldName(id + ".zip")
                .setFileDir(uploadDir.getRelativeFolderWithoutUploadRoot())
                .setFileNewName(id + ".zip")
                .setFileId(id).setFileSize(zipFile.length() + "")
                .setRealFilePath(zipPath)
                .setDirInfo(uploadDir);
    }
}
