package com.softdev.system.generator.service;

import com.alibaba.fastjson2.JSONObject;
import com.softdev.system.generator.entity.ParamInfo;
import com.softdev.system.generator.entity.ReturnT;
import com.softdev.system.generator.myUpload.MyUploadFileUtil;
import com.softdev.system.generator.myUpload.UploadedInfo;
import com.softdev.system.generator.util.FileUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@CrossOrigin("*")
public class CodeToFileServiceImpl extends AbsCodeToFileService {

    private final GeneratorService generatorService;
    private final MyUploadFileUtil fileUtil;

    @Autowired
    public CodeToFileServiceImpl(MyUploadFileUtil fileUtil, GeneratorService generatorService) {
        super();
        this.fileUtil = fileUtil;
        this.generatorService = generatorService;
    }

    @Override
    public ReturnT generateByUploadFile(String options, List<UploadedInfo> uploadedInfoList,
                                        HttpServletRequest request) throws Exception {
        var list = new ArrayList<>();
        for (UploadedInfo uploadedInfo : uploadedInfoList) {
            var fileContent = FileUtil.getContent(uploadedInfo.getRealFilePath());
            var zipFile = generateBySql(fileContent, options);
            if (StringUtils.isEmpty(zipFile)) {
                throw new Exception("生成失败");
            }
            zipFile = zipFile.replace("\\", "/");
            var downloadUrl = fileUtil.buildUrl(request, zipFile);
            list.add(Map.of("fileUrl", downloadUrl,
                    "path", zipFile,
                    "uploadFile", uploadedInfo));
        }
        return ReturnT.ok().put("msg", list);
    }

    @Override
    public String generateBySql(String content, String options) throws Exception {
        var param = new ParamInfo();
        param.setTableSql(content);
        param.setOptions(JSONObject.parse(options));
        var result = generatorService.generateCodeCore(param);
        if (result != null && !result.isEmpty()) {
            return zipToLocal(result);
        }

        return "";
    }

    private String zipToLocal(Map<String, String> result) throws Exception {
        var uploadDir = fileUtil.buildDir();
        var tmpDirName = UUID.randomUUID();
        File tmpDir = new File(uploadDir.getFolder().getAbsolutePath() + File.separator + tmpDirName);
        var files = new ArrayList<File>();
        for (Map.Entry<String, String> entry : result.entrySet()) {
            var fileName = entry.getKey();
            var fileContent = entry.getValue();
            var filePath = tmpDir.getAbsolutePath() + "/" + fileName;
            FileUtil.writeFile(filePath, fileContent);
            files.add(new File(filePath));
        }
        var zipPath = tmpDir.getAbsolutePath() + ".zip";
        zipFiles(files.toArray(new File[0]), new File(zipPath));
        try {
            tmpDir.delete();
        } catch (Exception e) {
            log.info("删异常：{},{}", tmpDir.getAbsolutePath(), e.getMessage());
        }

        return Paths.get(uploadDir.getRelativeFolder(), tmpDirName + ".zip").toString();
    }

    /**
     * 功能:压缩多个文件成一个zip文件
     *
     * @param srcfile：源文件列表
     * @param zipfile：压缩后的文件
     */
    public void zipFiles(File[] srcfile, File zipfile) throws Exception {
        byte[] buf = new byte[1024];
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile))) {
            //ZipOutputStream类：完成文件或文件夹的压缩

            for (File file : srcfile) {
                try (FileInputStream in = new FileInputStream(file)) {
                    // 给列表中的文件单独命名
                    out.putNextEntry(new ZipEntry(file.getName()));
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.closeEntry();
                }
            }
            out.close();
            System.out.println("压缩完成.");
        } catch (Exception e) {
            log.error("压缩失败", e);
            throw e;
        }
    }
}
