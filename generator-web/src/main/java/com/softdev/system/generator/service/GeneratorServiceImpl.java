package com.softdev.system.generator.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.softdev.system.generator.entity.*;
import com.softdev.system.generator.util.FreemarkerUtil;
import com.softdev.system.generator.util.MapUtil;
import com.softdev.system.generator.util.TableParseUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GeneratorService
 *
 * @author zhengkai.blog.csdn.net
 */
@Slf4j
@Service
public class GeneratorServiceImpl implements GeneratorService {

    String templateCpnfig = null;

    /**
     * 从项目中的JSON文件读取String
     *
     * @author zhengkai.blog.csdn.net
     */
    @Override
    public String getTemplateConfig() throws IOException {
        templateCpnfig = getTemplateConfig("");
        return templateCpnfig;
    }

    @Override
    public String getTemplateConfig(String fileName) {
        if (StringUtils.isEmpty(fileName))
            fileName = "template.json";

        templateCpnfig = null;
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    templateCpnfig = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                }
            }
        } catch (Exception e) {
            log.error("获取模板信息异常：{},{}", fileName, e.getMessage(), e);
        }
        return templateCpnfig;
    }

    /**
     * 根据配置的Template模板进行遍历解析，得到生成好的String
     *
     * @author zhengkai.blog.csdn.net
     */
    @Override
    public Map<String, Object> getResultByParams(Map<String, Object> params) throws IOException, TemplateException {
        Map<String, Object> result = new HashMap<>(32);
        result.put("tableName", MapUtil.getString(params, "tableName"));

        var templateConfig = MapUtil.getString(params, "templateConfig");
        if (StringUtils.isNotEmpty(templateConfig)) {
            getResultByParams4Json(params, result, templateConfig);
        } else {
            getResultByParams(params, result);
        }
        return result;
    }

    private void getResultByParams4Json(Map<String, Object> params, Map<String, Object> result, String templateConfig) throws IOException, TemplateException {

        var templateConfigJson = getTemplateConfig(templateConfig);
        var schemeDev = JSON.parseArray(templateConfigJson, TooneSchemeDevTemplate.class);

        for (TooneSchemeDevTemplate config : schemeDev) {
            var templates = config.getTemplates();
            for (TooneTemplateProcessResult template : templates) {
                var templateName = config.getGroup() + "/" + template.getName() + ".ftl";
                template.setContent(FreemarkerUtil.processString(templateName, params));
                result.put(template.getName(), template);
            }
        }
    }

    private void getResultByParams(Map<String, Object> params, Map<String, Object> result) throws IOException, TemplateException {
        JSONArray parentTemplates = JSONArray.parseArray(getTemplateConfig(""));
        for (int i = 0; i < parentTemplates.size(); i++) {
            JSONObject parentTemplateObj = parentTemplates.getJSONObject(i);
            var templates = parentTemplateObj.getJSONArray("templates");
            for (int x = 0; x < templates.size(); x++) {
                JSONObject childTemplate = templates.getJSONObject(x);
                var name = childTemplate.getString("name");
                var templateName = parentTemplateObj.getString("group") + "/" + name + ".ftl";
                result.put(name,
                        FreemarkerUtil.processString(templateName, params));
            }
        }
    }

    @Override
    public ReturnT generateCode(ParamInfo paramInfo) throws Exception {
        if (StringUtils.isEmpty(paramInfo.getTableSql())) {
            return ReturnT.error("表结构信息为空");
        }

        var result = generateCodeCore(paramInfo);
        return ReturnT.ok().put("outputJson", result);
    }

    public Map<String, Object> generateCodeCore(ParamInfo paramInfo) throws Exception {
        if (StringUtils.isEmpty(paramInfo.getTableSql())) {
            throw new Exception("表结构信息为空");
        }

        //1.Parse Table Structure 表结构解析
        ClassInfo classInfo = null;
        String dataType = MapUtil.getString(paramInfo.getOptions(), "dataType");
        if ("sql".equals(dataType) || dataType == null) {
            classInfo = TableParseUtil.processTableIntoClassInfo(paramInfo);
        } else if ("json".equals(dataType)) {
            //JSON模式：parse field from json string
            classInfo = TableParseUtil.processJsonToClassInfo(paramInfo);
            //INSERT SQL模式：parse field from insert sql
        } else if ("insert-sql".equals(dataType)) {
            classInfo = TableParseUtil.processInsertSqlToClassInfo(paramInfo);
            //正则表达式模式（非完善版本）：parse sql by regex
        } else if ("sql-regex".equals(dataType)) {
            classInfo = TableParseUtil.processTableToClassInfoByRegex(paramInfo);
            //默认模式：default parse sql by java
        }

        //2.Set the params 设置表格参数

        paramInfo.getOptions().put("classInfo", classInfo);
        paramInfo.getOptions().put("tableName", classInfo == null ? System.currentTimeMillis() : classInfo.getTableName());

        //log the generated table and filed size记录解析了什么表，有多少个字段
        //log.info("generated table :{} , size :{}",classInfo.getTableName(),(classInfo.getFieldList() == null ? "" : classInfo.getFieldList().size()));

        //3.generate the code by freemarker templates with parameters . Freemarker根据参数和模板生成代码
        var result = getResultByParams(paramInfo.getOptions());
//        log.info("result {}",result);
        log.info("table:{} - time:{} ", MapUtil.getString(result, "tableName"), new Date());
        return result;
    }
}
