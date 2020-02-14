package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.TcmJingfangDisease;
import com.shidao.service.TcmJingfangDiseaseServise;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value="/jingfangDisease")
public class TcmJingfangDiseaseController extends BaseController{

	@Autowired
	private TcmJingfangDiseaseServise jingfangDiseaseServise;
	
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public JSONObject list(TcmJingfangDisease tcmJingfangDisease){
		try {
			return JsonUtil.succeedJson(jingfangDiseaseServise.list(tcmJingfangDisease));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
