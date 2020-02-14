package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdcomRelationDiseaseCategoryDoctor;
import com.shidao.service.SdcomRelationDiseaseCategoryDoctorService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/sdcomRelationDepartmentDoctor")
public class SdcomRelationDepartmentDoctorController {

	@Autowired
	private SdcomRelationDiseaseCategoryDoctorService relationDiseaseCategoryDoctorService;
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月8日
	 * @param condition
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(SdcomRelationDiseaseCategoryDoctor condition) {
		try {
			relationDiseaseCategoryDoctorService.insertSelective(condition);
			return JsonUtil.succeedJson(condition.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
