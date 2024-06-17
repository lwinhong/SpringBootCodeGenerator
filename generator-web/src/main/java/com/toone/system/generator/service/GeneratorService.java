package com.toone.system.generator.service;

import com.toone.system.generator.entity.ParamInfo;
import com.toone.system.generator.entity.ReturnT;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;

/**
 * GeneratorService
 *
 * @author zhengkai.blog.csdn.net
 */
public interface GeneratorService {

    String getTemplateConfig() throws IOException;

    String getTemplateConfig(String fileName) throws IOException;


    Map<String, Object> getResultByParams(Map<String, Object> params) throws IOException, TemplateException;

    ReturnT generateCode(ParamInfo paramInfo) throws Exception;

    Map<String, Object> generateCodeCore(ParamInfo paramInfo) throws Exception;


}
