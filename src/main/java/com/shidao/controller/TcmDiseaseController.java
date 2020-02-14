package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.TcmDisease;
import com.shidao.service.TcmDiseaseService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/tcmDisease")
public class TcmDiseaseController extends BaseController {

	@Autowired
	private TcmDiseaseService tcmDieaseService;
	
	@RequestMapping(value = "/bingzhongs", method = RequestMethod.GET)
	public JSONObject getBingzhongs() {
		try {
			return JsonUtil.succeedJson(tcmDieaseService.listBingzhong());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(TcmDisease condition) {
		try {
			condition.setSelectFields("id,name,name_Initial,CATEGORY");			
			return JsonUtil.succeedJson(tcmDieaseService.list(condition));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/list/ai", method = RequestMethod.GET)
	public JSONObject listAi(TcmDisease condition) {
		try {
			return JsonUtil.succeedJson(tcmDieaseService.listDiseaseAi(condition));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
