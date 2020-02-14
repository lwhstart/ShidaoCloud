package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.MzAcupointStickingPrescription;
import com.shidao.service.MzAcupointStickingPrescriptionsService;
import com.shidao.util.JsonUtil;

/**
 * 贴敷方相关功能
 * @author yuanzhilin
 *
 */
@RestController
@RequestMapping("/TFF")
public class MzAcupointStickingPrescriptionsController extends BaseController {

	@Autowired
	private MzAcupointStickingPrescriptionsService tffService;
	
	@RequestMapping("/diseases")
	public JSONObject list(MzAcupointStickingPrescription prescription){
		try {
			
			return JsonUtil.succeedJson(tffService.getDiseases());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
}
