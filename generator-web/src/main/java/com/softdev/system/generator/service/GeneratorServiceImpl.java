package com.softdev.system.generator.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.ParamInfo;
import com.softdev.system.generator.entity.ReturnT;
import com.softdev.system.generator.entity.TemplateConfig;
import com.softdev.system.generator.util.FreemarkerUtil;
import com.softdev.system.generator.util.MapUtil;
import com.softdev.system.generator.util.TableParseUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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
        templateCpnfig = null;
        if (templateCpnfig != null) {
        } else {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template.json");
            templateCpnfig = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining(System.lineSeparator()));
            inputStream.close();
        }
        //log.info(JSON.toJSONString(templateCpnfig));
        return templateCpnfig;
    }

    /**
     * 根据配置的Template模板进行遍历解析，得到生成好的String
     *
     * @author zhengkai.blog.csdn.net
     */
    @Override
    public Map<String, String> getResultByParams(Map<String, Object> params) throws IOException, TemplateException {
        Map<String, String> result = new HashMap<>(32);
        result.put("tableName", MapUtil.getString(params, "tableName"));
        JSONArray parentTemplates = JSONArray.parseArray(getTemplateConfig());
        for (int i = 0; i < parentTemplates.size(); i++) {
            JSONObject parentTemplateObj = parentTemplates.getJSONObject(i);
            for (int x = 0; x < parentTemplateObj.getJSONArray("templates").size(); x++) {
                JSONObject childTemplate = parentTemplateObj.getJSONArray("templates").getJSONObject(x);
                result.put(childTemplate.getString("name"),
                        FreemarkerUtil.processString(parentTemplateObj.getString("group") + "/"
                                + childTemplate.getString("name") + ".ftl", params));
            }
        }
        return result;
    }

    @Override
    public ReturnT generateCode(ParamInfo paramInfo) throws Exception {
        if (StringUtils.isEmpty(paramInfo.getTableSql())) {
            return ReturnT.error("表结构信息为空");
        }

        Map<String, String> result = generateCodeCore(paramInfo);
        return ReturnT.ok().put("outputJson", result);
    }

    public Map<String, String> generateCodeCore(ParamInfo paramInfo) throws Exception {
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
        Map<String, String> result = getResultByParams(paramInfo.getOptions());
//        log.info("result {}",result);
        log.info("table:{} - time:{} ", MapUtil.getString(result, "tableName"), new Date());
        return result;
    }
}
