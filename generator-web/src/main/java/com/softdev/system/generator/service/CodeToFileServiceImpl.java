package com.softdev.system.generator.service;

import com.alibaba.fastjson2.JSONObject;
import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.ParamInfo;
import com.softdev.system.generator.entity.ReturnT;
import com.softdev.system.generator.entity.TooneTemplateProcessResult;
import com.softdev.system.generator.myUpload.MyUploadFileUtil;
import com.softdev.system.generator.myUpload.UploadedInfo;
import com.softdev.system.generator.util.FileMd5Util;
import com.softdev.system.generator.util.FileUtil;

import com.softdev.system.generator.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
@CrossOrigin("*")
public class CodeToFileServiceImpl implements CodeToFileService {

    private final GeneratorService generatorService;
    private final MyUploadFileUtil fileUtil;

    @Autowired
    public CodeToFileServiceImpl(MyUploadFileUtil fileUtil, GeneratorService generatorService) {
        this.fileUtil = fileUtil;
        this.generatorService = generatorService;
    }

    @Override
    public ReturnT generateBySql(ParamInfo paramInfo, HttpServletRequest request) throws Exception {
        var list = new ArrayList<>(1);

        var fileContent = paramInfo.getTableSql();
        var opt = paramInfo.getOptions();
        var generateInfo = generateBySqlCore(request, opt, fileContent, list);
        var generateList = new ArrayList<UploadedInfo>(1);
        try {
            generateList.add(generateInfo);
            fileUtil.saveFileInfoToDb(generateList);
        } catch (Exception e) {
            generateList.forEach(fileUtil::deleteUploadedFile);
            throw new RuntimeException("保存文件信息到数据库失败", e);
        }
        return ReturnT.ok().put("msg", list);
    }

    private UploadedInfo generateBySqlCore(HttpServletRequest request, Map<String, Object> opt,
                                           String fileContent, ArrayList<Object> list) throws Exception {
        opt.put("templateConfig", "template4SchemeDev.json");
        var generateInfo = generateBySql(fileContent, opt);
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
        return generateInfo;
    }

    @Override
    public ReturnT generateByUploadFile(String options, List<UploadedInfo> uploadedInfoList,
                                        HttpServletRequest request) throws Exception {
        var list = new ArrayList<>();
        var generateList = new ArrayList<UploadedInfo>(uploadedInfoList.size());
        for (UploadedInfo uploadedInfo : uploadedInfoList) {
            var fileContent = FileUtil.getContent(uploadedInfo.getRealFilePath());
            var opt = JSONObject.parseObject(options);
            var generateInfo = generateBySqlCore(request, opt, fileContent, list);
            generateInfo.setRelateFileId(uploadedInfo.getFileId());
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
    public UploadedInfo generateBySql(String content, Map<String, Object> options) throws Exception {
        var param = new ParamInfo();
        param.setTableSql(content);
        param.setOptions(options);
        var result = generatorService.generateCodeCore(param);
        if (result != null && !result.isEmpty()) {
            return zipToLocal(result, param);
        }
        return null;
    }


    /**
     * 将生成之后的文件保存到本地，并压缩
     *
     * @param result
     * @param paramInfo
     * @return
     * @throws Exception
     */
    private UploadedInfo zipToLocal(Map<String, Object> result, ParamInfo paramInfo) throws Exception {
        var uploadDir = fileUtil.buildDir();
        var id = FileUtil.getNewFileId();
        File tmpDir = new File(uploadDir.getFolder().getAbsolutePath() + File.separator + id);
        var zipRoot = tmpDir.getAbsolutePath();//zip文件根目录
        var classInfo = (ClassInfo) paramInfo.getOptions().get("classInfo");
//        var files = new ArrayList<File>();
//        files.add(tmpDir);
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            //var fileName = entry.getKey();
            var fileValue = entry.getValue();
            if (fileValue instanceof TooneTemplateProcessResult) {
                var template = (TooneTemplateProcessResult) fileValue;
                //文件路径
                var codeFilePath = template.getPath()
                        .replace("#CLASSNAME#", classInfo.getClassName())
                        .replace("#LSCLASSNAME#", StringUtils.lowerCaseFirst(classInfo.getClassName()));
                var filePath = Paths.get(tmpDir.getPath(), codeFilePath).toString();

                //将内容写到本地文件
                FileUtil.writeFile(filePath, String.valueOf(template.getContent()));
                //files.add(new File(filePath));
            } else {
                //var filePath = zipRoot + "/" + fileName;
                //FileUtil.writeFile(filePath, String.valueOf(fileValue));
                //files.add(new File(filePath));
            }
        }
        var zipPath = zipRoot + ".zip";
        if (!FileUtil.zip(zipRoot, zipPath)) {
            throw new Exception("压缩失败:" + zipPath);
        }
        try {
            FileUtil.deleteDirectory(tmpDir.toPath());
        } catch (Exception e) {
            log.info("删错异常（可忽略）：{},{}", zipRoot, e.getMessage());
        }
        File zipFile = new File(zipPath);
        return new UploadedInfo()
                //.setFileOldName(id + ".zip")
                .setFileDir(uploadDir.getRelativeFolderWithoutUploadRoot())
                .setFileNewName(id + ".zip")
                .setFileId(id).setFileSize(zipFile.length() + "")
                .setRealFilePath(zipPath)
                .setDirInfo(uploadDir).setFileMD5(FileMd5Util.getMD54BigFile(zipFile));
    }
}
