<#if isAutoImport?exists && isAutoImport==true>
package ${classInfo.packageName}.service.impl;

import com.toone.core.base.service.impl.BaseServiceImpl;
import com.toone.core.dao.POCondition;
import com.toone.core.utils.*;
import ${classInfo.packageName}.model.po.${classInfo.className};
import ${classInfo.packageName}.model.param.${classInfo.className}Param;
import ${classInfo.packageName}.model.vo.${classInfo.className}VO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

</#if>
/**
 * @description ${classInfo.classComment}
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd')}
 */
@Service
public class ${classInfo.className}ServiceImpl extends BaseServiceImpl<${classInfo.className}, ${classInfo.className}Param> {

	public void beforeList(${classInfo.className}Param param, POCondition condition) {

	}

	/**
	* 保存数据
	*
	* @param param
	* @return
	*/
	@Override
	@Transactional
	public String save(${classInfo.className}Param param) {
		${classInfo.className} po = new ${classInfo.className}();
		BeanUtils.copyProperties(po, param);
		//保存逻辑

		return "";
	}

	@Override
	public Object insert(${classInfo.className} ${classInfo.className?uncap_first}) {

		// valid
		if (${classInfo.className?uncap_first} == null) {
			return ${returnUtilFailure}("必要参数缺失");
        }

		${classInfo.className?uncap_first}Mapper.insert(${classInfo.className?uncap_first});
        return ${returnUtilSuccess}();
	}

	/**
	* 删除数据
	*
	* @param po
	*/
	@Override
	@Transactional
	public void delete(int id) {
		//根据条件删除
		//POCondition condition = new POCondition();
		//添加条件
		//condition.addEQ("Id", id);
		//dao.deletePoByCondition(${classInfo.className}.class, condition);
		dao.deletePo(${classInfo.className}.class, id);
	}

	/**
	* 删除数据
	*
	* @param po
	*/
	@Override
	@Transactional
	public void del(${classInfo.className} po) {
		if (po != null && StringUtil.isNotEmpty(po.getId())) {
			//根据条件删除
			//POCondition condition = new POCondition();
			//condition.addEQ("id", po.getId());
			//dao.deletePoByCondition(${classInfo.className}.class, condition);
			dao.deletePo(${classInfo.className}.class, po.getId());
		}
	}

	@Override
	public Object update(${classInfo.className} ${classInfo.className?uncap_first}) {
		int ret = ${classInfo.className?uncap_first}Mapper.update(${classInfo.className?uncap_first});
		return ret>0?${returnUtilSuccess}():${returnUtilFailure}();
	}


	/**
	* 根据ID获取数据
	*
	* @param id
	* @return
	* @throws Exception
	*/
	public ${classInfo.className}VO findById(String id) {
		${classInfo.className}VO ${classInfo.className?uncap_first}VO = new ${classInfo.className}VO();
		if (StringUtil.isEmpty(id)) {
			return null;
		}
		${classInfo.className} ${classInfo.className?uncap_first} = this.find(id);
		if (${classInfo.className?uncap_first} == null) {
			return null;
		}
		BeanCopyUtils.copyProperties(${classInfo.className?uncap_first}VO, ${classInfo.className?uncap_first});
		return ${classInfo.className?uncap_first}VO;
	}

	/**
	* 根据id获取项目信息
	*
	* @param id
	* @return
	*/
	public ${classInfo.className} get${classInfo.className}ById(String id) {
		${classInfo.className} ${classInfo.className?uncap_first} = dao.findPo(${classInfo.className}.class, id);
		return ${classInfo.className?uncap_first};
	}


	/**
	* 导出Excel文件
	*
	* @param id
	*/
	public void exportFile(String id) {
		try {
			POCondition condition = new POCondition();
			condition.addEQ("id", projectId);
			List<${classInfo.className}> contactsList = dao.findPoList(${classInfo.className}.class, condition);
			Workbook workbook = ${classInfo.className}Exportor.exportFile(contactsList);
			String title = "标题" + DateUtil.getCurDate() + ".xls";
			ImportExportUtil.export(workbook, title);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
