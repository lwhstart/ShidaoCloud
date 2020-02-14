/**
 * 
 */
package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.TcmSymptom;
import com.shidao.service.TcmSymptomService;
import com.shidao.util.JsonUtil;
import com.shidao.util.ListResult;

/**
 * @author zhangzhuping
 *
 */
@RestController
@RequestMapping(value="/tcmSymptom")
public class TcmSymptomController extends BaseController {
	
	@Autowired
	private TcmSymptomService tcmSymptomService;

	/**
	 * 获取部位
	 * @return
	 */
	@RequestMapping(value="/parts/{gender}", method = RequestMethod.GET)
	public JSONObject parts(@PathVariable String gender){
		try {
			TcmSymptom condition = new TcmSymptom();
			condition.setSelectFields("part");
			condition.setGender(gender);
			condition.setDistinct(true);
			ListResult<TcmSymptom> tcmSymptoms = tcmSymptomService.list(condition, 1, Integer.MAX_VALUE);
			return JsonUtil.succeedJson(tcmSymptoms.getList());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="/{part}/symptoms/{gender}", method =RequestMethod.GET)
	public JSONObject symptom(@PathVariable String part, @PathVariable String gender){
		try {
			TcmSymptom condition = new TcmSymptom();
			condition.setPart(part);
			condition.setGender(gender);
			condition.setSelectFields("id,symptom");
			ListResult<TcmSymptom> tcmSymptoms = tcmSymptomService.list(condition, 1, Integer.MAX_VALUE);
			return JsonUtil.succeedJson(tcmSymptoms.getList());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
}
