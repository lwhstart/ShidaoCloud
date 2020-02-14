package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.service.SdbsProfessioService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value="/sdbsProfession")
public class SdbsProfessionController extends BaseController{

	@Autowired
	private SdbsProfessioService sdbsProfessioService;
	
	
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public JSONObject list(){
		try {
			return JsonUtil.succeedJson(sdbsProfessioService.list());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}	
}
