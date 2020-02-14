package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.TcmHealthKnowledgeDistribution;
import com.shidao.service.TcmHealthKnowledgeDistributionService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping("/TcmHealthKnowledgeDistribution")
public class TcmHealthKnowledgeDistributionController extends BaseController {

	@Autowired
	private TcmHealthKnowledgeDistributionService hkdService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(TcmHealthKnowledgeDistribution condition, Integer pageNum, Integer pageSize) {
		try {
			return JsonUtil.succeedJson(hkdService.list(condition, pageNum, pageSize));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	public JSONObject upadge(@PathVariable Integer id, Integer deleted, Integer sticked) {
		try {
			TcmHealthKnowledgeDistribution condition = new TcmHealthKnowledgeDistribution();
			condition.setId(id);
			condition.setDeleted(deleted);
			hkdService.updateByPrimaryKeySelective(condition);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	public JSONObject upadge(@PathVariable Integer id) {
		try {
			TcmHealthKnowledgeDistribution condition = new TcmHealthKnowledgeDistribution();
			condition.setId(id);
			condition.setDeleted(1);
			hkdService.updateByPrimaryKeySelective(condition);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/stick/{id}", method = RequestMethod.POST)
	public JSONObject stickToggle(@PathVariable Integer id) {
		try {
			hkdService.stickToggle(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public JSONObject detail(@PathVariable Integer id) {
		try {
			return JsonUtil.succeedJson(hkdService.selectByPrimaryKey(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
		

}
