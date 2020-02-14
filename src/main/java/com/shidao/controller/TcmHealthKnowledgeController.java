package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.service.TcmHealthKnowledgeService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value="/Knowledge")
public class TcmHealthKnowledgeController extends BaseController{
	
	@Autowired
	TcmHealthKnowledgeService tcmHealthKnowledgeService;
	
	@RequestMapping(value = "/healthKnowledge/list/{diseaseName}", method = RequestMethod.GET)
	public JSONObject healthKnowledgeByValidList(@PathVariable String diseaseName) {
		try {
			return JsonUtil.succeedJson(tcmHealthKnowledgeService.listByCondition( diseaseName));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value="/healthKnowledge/detail/{id}", method = RequestMethod.GET)
	public JSONObject healthKnowledgeDetail(@PathVariable Integer id)
	{
		try {
			return JsonUtil.succeedJson(tcmHealthKnowledgeService.selectByPrimaryKey(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
