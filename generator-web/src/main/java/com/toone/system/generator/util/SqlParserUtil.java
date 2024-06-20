package com.toone.system.generator.util;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.toone.system.generator.entity.ClassInfo;
import com.toone.system.generator.entity.FieldInfo;
import com.toone.system.generator.entity.Json2JavaInfo;
import com.toone.system.generator.entity.ParamInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class SqlParserUtil {
    /**
     * json转ClassInfo
     *
     * @param paramInfo
     * @return
     */
    public static List<ClassInfo> processJsonToClassInfo2(ParamInfo paramInfo) {
        List<ClassInfo> classInfoList = new ArrayList<>();

//        JSONArray jsonArray = JSONArray.parseArray(paramInfo.getTableSql().trim());
//        String nameCaseType = MapUtil.getString(paramInfo.getOptions(), "nameCaseType");
//
//        for (int i = 0; i < jsonArray.size(); i++) {
//            var codeJavaInfo = new ClassInfo();
//            classInfoList.add(codeJavaInfo);
//
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            var table = jsonObject.getJSONObject("table");
//            var tableName = table.getString("name");
//            // class Name
//            String className = StringUtils.upperCaseFirst(StringUtils.underlineToCamelCase(tableName));
//            if (className.contains("_")) {
//                className = className.replaceAll("_", "");
//            }
//
//            var classComment = table.getString("comment");
//            if (classComment != null)
//                classComment = classComment.replaceAll(";", "");
//
//            codeJavaInfo.setTableName(tableName);
//            codeJavaInfo.setClassName(className);
//            codeJavaInfo.setClassComment(classComment);
//            codeJavaInfo.setOriginTableName(tableName);
//
//            var fieldList = new ArrayList<FieldInfo>();
//            codeJavaInfo.setFieldList(fieldList);
//
//            var fields = jsonObject.getJSONArray("fields");
//            if (fields != null) {
//                for (int j = 0, fieldsSize = fields.size(); j < fieldsSize; j++) {
//                    var field = (JSONObject) fields.get(j);
//                    String fieldName;
//                    if (ParamInfo.NAME_CASE_TYPE.CAMEL_CASE.equals(nameCaseType)) {
//                        // 2024-1-27 L&J 适配任意(maybe)原始风格转小写驼峰
//                        fieldName = StringUtils.toLowerCamel(key);
//                    } else if (ParamInfo.NAME_CASE_TYPE.UNDER_SCORE_CASE.equals(nameCaseType)) {
//                        fieldName = StringUtils.toUnderline(key, false);
//                    } else if (ParamInfo.NAME_CASE_TYPE.UPPER_UNDER_SCORE_CASE.equals(nameCaseType)) {
//                        fieldName = StringUtils.toUnderline(key.toUpperCase(), true);
//                    } else {
//                        fieldName = key;
//                    }
//
//                    String mysqlType = "";
//                    if (field.containsKey("dataType")) {
//                        mysqlType = field.getString("dataType").toLowerCase();
//                    }
//                    String swaggerClass = "string";
//                    if (mysqlJavaTypeUtil.getMysqlSwaggerTypeMap().containsKey(mysqlType)) {
//                        swaggerClass = mysqlJavaTypeUtil.getMysqlSwaggerTypeMap().get(mysqlType);
//                    }
//                    // field class
//                    // int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
//                    String fieldClass = "String";
//                    if (mysqlJavaTypeUtil.getMysqlJavaTypeMap().containsKey(mysqlType)) {
//                        fieldClass = mysqlJavaTypeUtil.getMysqlJavaTypeMap().get(mysqlType);
//                    }
//                    String fieldComment = field.getString("comment");
//                    if (fieldComment == null) {
//                        fieldComment = key;
//                    }
//
//                    var fieldInfo = new FieldInfo();
//                    fieldInfo.setColumnName(key);
//                    fieldInfo.setFieldName(fieldName);
//                    fieldInfo.setFieldClass(fieldClass);
//                    fieldInfo.setSwaggerClass(swaggerClass);
//                    fieldInfo.setFieldComment(fieldComment);
//                    fieldList.add(fieldInfo);
//                }
//            }
//
//            break;
//        }
        return classInfoList;
    }

    public static List<ClassInfo> processJsonToClassInfo(ParamInfo paramInfo) {
        List<ClassInfo> classInfoList = new ArrayList<>();

        var jsonArray = JSONArray.parseArray(paramInfo.getTableSql().trim(), Json2JavaInfo.class);
        String nameCaseType = MapUtil.getString(paramInfo.getOptions(), "nameCaseType");

        for (int i = 0; i < jsonArray.size(); i++) {
            var codeJavaInfo = new ClassInfo();
            classInfoList.add(codeJavaInfo);

            var jsonObject = jsonArray.get(i);
            var table = jsonObject.getTable();
            var tableName = table.getName();
            // class Name
            String className = StringUtils.upperCaseFirst(StringUtils.underlineToCamelCase(tableName));
            if (className.contains("_")) {
                className = className.replaceAll("_", "");
            }

            var classComment = table.getComment();
            if (classComment != null)
                classComment = classComment.replaceAll(";", "");

            codeJavaInfo.setTableName(tableName);
            codeJavaInfo.setClassName(className);
            codeJavaInfo.setClassComment(classComment);
            codeJavaInfo.setOriginTableName(tableName);

            var fieldList = new ArrayList<FieldInfo>();
            codeJavaInfo.setFieldList(fieldList);

            var fields = jsonObject.getFields();
            if (fields != null) {
                for (int j = 0, fieldsSize = fields.size(); j < fieldsSize; j++) {
                    var field = fields.get(j);
                    String key = field.getName();
                    String fieldName;
                    if (ParamInfo.NAME_CASE_TYPE.CAMEL_CASE.equals(nameCaseType)) {
                        // 2024-1-27 L&J 适配任意(maybe)原始风格转小写驼峰
                        fieldName = StringUtils.toLowerCamel(key);
                    } else if (ParamInfo.NAME_CASE_TYPE.UNDER_SCORE_CASE.equals(nameCaseType)) {
                        fieldName = StringUtils.toUnderline(key, false);
                    } else if (ParamInfo.NAME_CASE_TYPE.UPPER_UNDER_SCORE_CASE.equals(nameCaseType)) {
                        fieldName = StringUtils.toUnderline(key.toUpperCase(), true);
                    } else {
                        fieldName = key;
                    }

                    String mysqlType = "";
                    if (field.getDataType() != null) {
                        mysqlType = field.getDataType().toLowerCase();
                        if (mysqlType.contains("(")) {
                            mysqlType = mysqlType.substring(0, mysqlType.indexOf("(")).trim();
                        }
                    }
                    String swaggerClass = "string";
                    if (mysqlJavaTypeUtil.getMysqlSwaggerTypeMap().containsKey(mysqlType)) {
                        swaggerClass = mysqlJavaTypeUtil.getMysqlSwaggerTypeMap().get(mysqlType);
                    }
                    // field class
                    // int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                    String fieldClass = "String";
                    if (mysqlJavaTypeUtil.getMysqlJavaTypeMap().containsKey(mysqlType)) {
                        fieldClass = mysqlJavaTypeUtil.getMysqlJavaTypeMap().get(mysqlType);
                    }
                    String fieldComment = field.getComment();
                    if (fieldComment == null) {
                        fieldComment = key;
                    }

                    var fieldInfo = new FieldInfo();
                    fieldInfo.setColumnName(key);
                    fieldInfo.setFieldName(fieldName);
                    fieldInfo.setFieldClass(fieldClass);
                    fieldInfo.setSwaggerClass(swaggerClass);
                    fieldInfo.setFieldComment(fieldComment);
                    fieldList.add(fieldInfo);
                }
            }
        }
        return classInfoList;
    }

    /**
     * sql转ClassInfo
     *
     * @param paramInfo
     * @return
     */
    public static ClassInfo processSqlToClassInfo(ParamInfo paramInfo) throws JSQLParserException {
        var sql = paramInfo.getTableSql();
        var options = paramInfo.getOptions();
        DbType dbtype = DbType.of(MapUtil.getString(paramInfo.getOptions(), "dbType"));
        if (dbtype == null) {
            //sql.toLowerCase().contains("mysql")
            dbtype = DbType.mysql;
            log.warn("没有传数据库类型，默认是有mysql");
            //throw new CodeGenerateException("不支持的数据库类型");
        }
        var stmtList = SQLUtils.parseStatements(sql, dbtype);

        if (stmtList.stream().anyMatch(stmt -> stmt instanceof SQLInsertStatement)) {
            return processSqlToClassInfo4Insert(options, stmtList);
        } /*else if (stmtList.stream().anyMatch(stmt -> stmt instanceof SQLCreateTableStatement)) {
            return processSqlToClassInfo4CreateTable(options, stmtList);
        }*/

        return processSqlToClassInfo4CreateTable(options, stmtList);
    }

    /**
     * createTable sql转ClassInfo
     *
     * @param options
     * @param stmtList
     * @return
     * @throws JSQLParserException
     */
    public static ClassInfo processSqlToClassInfo4CreateTable(Map<String, Object> options, List<SQLStatement> stmtList) throws JSQLParserException {

        String nameCaseType = MapUtil.getString(options, "nameCaseType");
        //表名
        String tableName = null;
        // class Comment
        String classComment = null;
        // field List
        List<FieldInfo> fieldList = new ArrayList<>();

        for (SQLStatement stmt : stmtList) {
            if (!(stmt instanceof SQLCreateTableStatement)) {
                continue;
            }

            var table = (SQLCreateTableStatement) stmt;

            tableName = table.getTableName().replace("`", "");
            classComment = table.getComment() != null ? table.getComment().toString() : "";
            //如果备注跟;混在一起，需要替换掉
            classComment = classComment.replaceAll(";", "");

            for (SQLTableElement element : table.getTableElementList()) {
                if (!(element instanceof SQLColumnDefinition)) {
                    continue;
                }
                var column = (SQLColumnDefinition) element;

                String columnName = column.getColumnName().replace("`", "");
                // field Name
                // 2019-09-08 yj 添加是否下划线转换为驼峰的判断
                // 2023-8-27 L&J 支持原始列名任意命名风格, 不依赖用户是否输入下划线
                String fieldName;
                if (ParamInfo.NAME_CASE_TYPE.CAMEL_CASE.equals(nameCaseType)) {
                    // 2024-1-27 L&J 适配任意(maybe)原始风格转小写驼峰
                    fieldName = StringUtils.toLowerCamel(columnName);
                } else if (ParamInfo.NAME_CASE_TYPE.UNDER_SCORE_CASE.equals(nameCaseType)) {
                    fieldName = StringUtils.toUnderline(columnName, false);
                } else if (ParamInfo.NAME_CASE_TYPE.UPPER_UNDER_SCORE_CASE.equals(nameCaseType)) {
                    fieldName = StringUtils.toUnderline(columnName.toUpperCase(), true);
                } else {
                    fieldName = columnName;
                }

                String mysqlType = column.getDataType().getName().toLowerCase();
                //swagger class
                String swaggerClass = "string";
                if (mysqlJavaTypeUtil.getMysqlSwaggerTypeMap().containsKey(mysqlType)) {
                    swaggerClass = mysqlJavaTypeUtil.getMysqlSwaggerTypeMap().get(mysqlType);
                }
                // field class
                // int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                String fieldClass = "String";
                if (mysqlJavaTypeUtil.getMysqlJavaTypeMap().containsKey(mysqlType)) {
                    fieldClass = mysqlJavaTypeUtil.getMysqlJavaTypeMap().get(mysqlType);
                }
                String fieldComment = column.getComment() != null ? column.getComment().toString() : columnName;

                FieldInfo fieldInfo = new FieldInfo()
                        .setColumnName(columnName)
                        .setFieldName(fieldName)
                        .setSwaggerClass(swaggerClass)
                        .setFieldClass(fieldClass)
                        .setFieldComment(fieldComment);
                fieldList.add(fieldInfo);
            }
            break;
        }
        if (fieldList.size() == 0) {
            throw new CodeGenerateException("表结构分析失败，请检查语句是否正确");
        }
        String originTableName = tableName;
        //ignore prefix
        if (tableName != null && StringUtils.isNotNull(MapUtil.getString(options, "ignorePrefix"))) {
            tableName = tableName.replaceAll(MapUtil.getString(options, "ignorePrefix"), "");
        }
        // class Name
        String className = StringUtils.upperCaseFirst(StringUtils.underlineToCamelCase(tableName));
        if (className.contains("_")) {
            className = className.replaceAll("_", "");
        }

        classComment = classComment.replaceAll(";", "");

        ClassInfo codeJavaInfo = new ClassInfo();
        codeJavaInfo.setTableName(tableName);
        codeJavaInfo.setClassName(className);
        codeJavaInfo.setClassComment(classComment);
        codeJavaInfo.setFieldList(fieldList);
        codeJavaInfo.setOriginTableName(originTableName);

        return codeJavaInfo;
    }

    /**
     * insert sql转ClassInfo
     *
     * @param options
     * @param stmtList
     * @return
     */
    public static ClassInfo processSqlToClassInfo4Insert(Map<String, Object> options, List<SQLStatement> stmtList) {

        String nameCaseType = MapUtil.getString(options, "nameCaseType");
        String tableName = null;
        String classComment = "";
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
        for (SQLStatement sqlStatement : stmtList) {
            if (!(sqlStatement instanceof SQLInsertStatement)) {
                continue;
            }
            var sqlInsertStatement = (SQLInsertStatement) sqlStatement;
            tableName = sqlInsertStatement.getTableName().toString();
            ((SQLInsertStatement) sqlStatement).getColumns().forEach((column) -> {
                if (column instanceof SQLIdentifierExpr) {
                    String columnName = column.toString();
                    String fieldName;
                    if (ParamInfo.NAME_CASE_TYPE.CAMEL_CASE.equals(nameCaseType)) {
                        // 2024-1-27 L&J 适配任意(maybe)原始风格转小写驼峰
                        fieldName = StringUtils.toLowerCamel(columnName);
                    } else if (ParamInfo.NAME_CASE_TYPE.UNDER_SCORE_CASE.equals(nameCaseType)) {
                        fieldName = StringUtils.toUnderline(columnName, false);
                    } else if (ParamInfo.NAME_CASE_TYPE.UPPER_UNDER_SCORE_CASE.equals(nameCaseType)) {
                        fieldName = StringUtils.toUnderline(columnName.toUpperCase(), true);
                    } else {
                        fieldName = columnName;
                    }
                    String fieldClass = "String";
                    String fieldComment = "";
                    String swaggerClass = mysqlJavaTypeUtil.getMysqlSwaggerTypeMap().get("varchar");


                    FieldInfo fieldInfo = new FieldInfo();
                    //
                    fieldInfo.setColumnName(columnName);
                    fieldInfo.setFieldName(fieldName);
                    fieldInfo.setFieldClass(fieldClass);
                    fieldInfo.setSwaggerClass(swaggerClass);
                    fieldInfo.setFieldComment(fieldComment);

                    fieldList.add(fieldInfo);
                }
            });
        }

        String originTableName = tableName;
        //ignore prefix
        if (tableName != null && StringUtils.isNotNull(MapUtil.getString(options, "ignorePrefix"))) {
            tableName = tableName.replaceAll(MapUtil.getString(options, "ignorePrefix"), "");
        }
        // class Name
        String className = StringUtils.upperCaseFirst(StringUtils.underlineToCamelCase(tableName));
        if (className.contains("_")) {
            className = className.replaceAll("_", "");
        }

        ClassInfo codeJavaInfo = new ClassInfo();
        codeJavaInfo.setTableName(tableName);
        codeJavaInfo.setClassName(className);
        codeJavaInfo.setClassComment(classComment);
        codeJavaInfo.setFieldList(fieldList);
        codeJavaInfo.setOriginTableName(originTableName);

        return codeJavaInfo;
    }
}
