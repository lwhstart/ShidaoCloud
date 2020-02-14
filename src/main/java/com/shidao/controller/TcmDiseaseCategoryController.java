package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.TcmDiseaseCategory;
import com.shidao.service.TcmDiseaseCategoryService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/tcmDiseaseCategory")
public class TcmDiseaseCategoryController extends BaseController{
	
	@Autowired
	private TcmDiseaseCategoryService diseaseCategoryService;
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月10日
	 * @param condition
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(TcmDiseaseCategory condition) {
		try {
			diseaseCategoryService.insertSelective(condition);
			return JsonUtil.succeedJson(condition.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月10日
	 * @param condition
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(TcmDiseaseCategory condition) {
		try {
			diseaseCategoryService.updateByPrimaryKeySelective(condition);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月11日
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id) {
		try {
			diseaseCategoryService.delete(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
