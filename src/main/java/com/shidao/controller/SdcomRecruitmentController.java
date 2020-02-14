package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdcomRecruitment;
import com.shidao.service.SdcomRecruitmentService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/sdcomRecruitment")
public class SdcomRecruitmentController extends BaseController{

	@Autowired
	SdcomRecruitmentService sdcomRecruitmentService;

	
	
	@RequestMapping(value = "/update",method = RequestMethod.POST)
	public JSONObject update(SdcomRecruitment condition) {
		try {
			sdcomRecruitmentService.updateByPrimaryKeySelective(condition);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/insert",method = RequestMethod.POST)
	public JSONObject insert(SdcomRecruitment sdcomRecruitment) {
		try {
			sdcomRecruitmentService.insertSelective(sdcomRecruitment);
			return JsonUtil.succeedJson(sdcomRecruitment.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
