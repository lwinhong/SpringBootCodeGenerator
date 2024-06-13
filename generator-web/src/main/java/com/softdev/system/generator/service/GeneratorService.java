package com.softdev.system.generator.service;

import com.softdev.system.generator.entity.ParamInfo;
import com.softdev.system.generator.entity.ReturnT;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * GeneratorService
 *
 * @author zhengkai.blog.csdn.net
 */
public interface GeneratorService {

    String getTemplateConfig() throws IOException;

    Map<String, String> getResultByParams(Map<String, Object> params) throws IOException, TemplateException;

    ReturnT generateCode(ParamInfo paramInfo) throws Exception;

    Map<String, String> generateCodeCore(ParamInfo paramInfo) throws Exception;
}
