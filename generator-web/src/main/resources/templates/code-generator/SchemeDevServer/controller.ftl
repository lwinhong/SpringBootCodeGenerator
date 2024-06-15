package com.toone.masterdata.controller;
<#if isAutoImport?exists && isAutoImport==true>
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.toone.core.base.controller.BaseController;
import com.toone.core.utils.StringUtil;
import com.toone.masterdata.model.po.MdProjectContacts;
import com.toone.masterdata.model.vo.MdProjectVO;
import com.toone.core.response.Return;
import com.toone.masterdata.model.po.MdProject;
import com.toone.masterdata.model.param.MdProjectParam;
import com.toone.masterdata.service.impl.MdProjectServiceImpl;

import java.util.List;
</#if>

/**
* @description ${classInfo.classComment}
* @author ${authorName}
* @date ${.now?string('yyyy-MM-dd')}
*/
@RestController
@RequestMapping("/${classInfo.className}")
@CrossOrigin("*")
public class ${classInfo.className}Controller extends BaseController<MdProject, MdProjectParam> {

    @Autowired
    private ${classInfo.className}ServiceImpl ${classInfo.className?uncap_first}Service;

    /**
    * 查询 id 查询
    * @author ${authorName}
    * @date ${.now?string('yyyy/MM/dd')}
    * @param id
    * @return
    */
    @GetMapping("findById")
    public Return<?> findById(String id) {
        ${classInfo.className}VO ${classInfo.className?uncap_first}VO = ${classInfo.className?uncap_first}Service.findById(id);
        return Return.success(${classInfo.className?uncap_first}VO);
    }

    /**
    * 刪除
    * @author ${authorName}
    * @date ${.now?string('yyyy/MM/dd')}
    * @param id
    **/
    @GetMapping("/delete")
    public Return<?> delete(String id) {
        ${classInfo.className?uncap_first}Service.delete(id);
        return Return.success();
    }
    /**
    * 新增
    * @author ${authorName}
    * @date ${.now?string('yyyy/MM/dd')}
    **/
    @RequestMapping("/insert")
    public Return<?> insert(${classInfo.className} ${classInfo.className?uncap_first}){
        ${classInfo.className?uncap_first}Service.insert(${classInfo.className?uncap_first});
        return Return.success();
    }

    /**
    * 更新
    * @author ${authorName}
    * @date ${.now?string('yyyy/MM/dd')}
    **/
    @RequestMapping("/update")
    public Return<?> update(${classInfo.className} ${classInfo.className?uncap_first}){
        ${classInfo.className?uncap_first}Service.update(${classInfo.className?uncap_first});
        return Return.success();
    }

    /**
    * 导出Excel文件
    * @author ${authorName}
    * @date ${.now?string('yyyy/MM/dd')}
    * @param param
    */
    @PostMapping("exportFile")
    public Return<?> exportFile(String param) {
        JSONObject paramJson = JSONObject.parseObject(param);
        ${classInfo.className?uncap_first}Service.exportFile(paramJson.getString("id"));
        return Return.success();
    }

    /**
    * feign接口，根据id获取信息
    * @author ${authorName}
    * @date ${.now?string('yyyy/MM/dd')}
    * @param id
    */
    @GetMapping("/feign-find${classInfo.className}ById")
    public ${classInfo.className} find${classInfo.className}ById(@RequestParam("id") String id) {
        if (StringUtil.isEmpty(id)) {
            return null;
        }
        ${classInfo.className} ${classInfo.className?uncap_first} = ${classInfo.className?uncap_first}Service.get${classInfo.className}ById(id);
        return ${classInfo.className?uncap_first};
    }

}