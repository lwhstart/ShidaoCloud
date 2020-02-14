package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.MzDisease;
import com.shidao.service.MzDiseaseService;
import com.shidao.service.TcmHealthManagementService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/mzDisease")
public class MzDiseaseController extends BaseController {

	@Autowired
	private MzDiseaseService mzDiseaseService;

	@Autowired
	private TcmHealthManagementService healthManagementService;

	/**
	 * 获取部位
	 * 
	 * @return
	 */
	@RequestMapping(value = "/parts/{gender}", method = RequestMethod.GET)
	public JSONObject parts(@PathVariable String gender) {
		try {
			MzDisease condition = new MzDisease();
			condition.setCategory("TCM");
			condition.setDistinct(true);
			condition.setSelectFields("part");
			condition.setGender(gender);
			return JsonUtil.succeedJson( mzDiseaseService.list(condition));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/{part}/names/{gender}", method = RequestMethod.GET)
	public JSONObject names(@PathVariable String part, @PathVariable String gender) {
		try {
			MzDisease condition = new MzDisease();
			condition.setPart(part);
			condition.setGender(gender);
			condition.setSelectFields("id,name");
			return JsonUtil.succeedJson(mzDiseaseService.list(condition));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping("/{id}/healthManagement")
	public JSONObject getHealthManagement(@PathVariable Integer id) {
		try {
			return JsonUtil.succeedJson(healthManagementService.getByDiseaseid(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public JSONObject list(MzDisease condition){
		try {			
			return JsonUtil.succeedJson(mzDiseaseService.list(condition));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
