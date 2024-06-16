package com.softdev.system.generator.controller;

import com.softdev.system.generator.entity.ParamInfo;
import com.softdev.system.generator.entity.ReturnT;
import com.softdev.system.generator.service.CodeToFileService;
import com.softdev.system.generator.service.GeneratorService;
import com.softdev.system.generator.myUpload.MyUploadFileUtil;
import com.softdev.system.generator.util.ValueUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;

/**
 * 代码生成控制器
 *
 * @author zhengkai.blog.csdn.net
 */
@Controller
@Slf4j
public class GeneratorController {
    @Autowired
    private ValueUtil valueUtil;

    @Autowired
    private GeneratorService generatorService;

    @GetMapping("/")
    public ModelAndView defaultPage() {

        return new ModelAndView("index").addObject("value", valueUtil);
    }

    @GetMapping("/index")
    public ModelAndView indexPage() {

        return new ModelAndView("index").addObject("value", valueUtil);
    }

    @GetMapping("/main")
    public ModelAndView mainPage() {

        return new ModelAndView("main").addObject("value", valueUtil);
    }

    @RequestMapping("/template/all")
    @ResponseBody
    public ReturnT getAllTemplates() throws Exception {
        String templates = generatorService.getTemplateConfig();
        return ReturnT.ok().put("templates", templates);
    }

    @PostMapping("/code/generate")
    @ResponseBody
    public ReturnT generateCode(@RequestBody ParamInfo paramInfo) throws Exception {
        try {
            return generatorService.generateCode(paramInfo);
        } catch (Exception e) {
            return ReturnT.error("生成失败： " + e.getMessage());
        }
    }

    /**************************以下是后面根据业务需求添加的文件上传功能**********************/

    private MyUploadFileUtil fileUtil;
    private CodeToFileService codeToFileService;

    @Autowired
    public void setMyUploadFileUtil(MyUploadFileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    @Autowired
    private void setCodeToFileService(CodeToFileService codeToFileService) {
        this.codeToFileService = codeToFileService;
    }

    @PostMapping("/code/generate4SQL")
    @ResponseBody
    public ReturnT generateCode4Sql(@RequestBody ParamInfo paramInfo,
                                    HttpServletRequest request) throws Exception {
        try {
            return codeToFileService.generateBySql(paramInfo, request);
        } catch (Exception e) {
            return ReturnT.error("生成失败： " + e.getMessage());
        }
    }

    @PostMapping("/code/generate4file")
    @ResponseBody
    public ReturnT generateCode4File(@RequestParam("file") MultipartFile[] files,
                                     @RequestParam("options") String options,
                                     HttpServletRequest request) throws Exception {
        try {
            //1.保存文件
            var result = fileUtil.uploadFiles(files, request);

            //2.读取文件，解析内容
            if (result == null || request.getContentLength() == 0) {
                return ReturnT.error("生成失败：没有上传文件");
            }

            //3.根据解析的内容，生成代码
            // 返回结果（按层级放好类文件，压缩成zip文件，然后下载url）
            return codeToFileService.generateByUploadFile(options, result, request);
        } catch (Exception e) {
            log.error("生成失败： " + e.getMessage(), e);
            return ReturnT.error("You failed to upload because " + e.getMessage());
        }
    }

    @GetMapping("/code/download")
    public void download(String fileId, HttpServletResponse response) throws IOException {
        try {
            fileUtil.downloadLocal(fileId, response);
        } catch (Exception e) {
            log.error("下载失败： " + e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/code/upload")
    @ResponseBody
    public ReturnT upload(@RequestParam("file") MultipartFile[] files, HttpServletRequest request) {
        try {
            //1.保存文件
            var result = fileUtil.uploadFiles(files, request);
            return ReturnT.ok(Map.of("fileResult", result));
        } catch (Exception e) {
            return ReturnT.error("You failed to upload because " + e.getMessage());
        }
    }
}
