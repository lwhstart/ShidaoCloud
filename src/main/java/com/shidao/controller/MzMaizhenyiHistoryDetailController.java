package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.MzMaizhenyiHistoryDetail;
import com.shidao.service.MzMaizhenyiHistoryDetailService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping("/mzMaihenyiHistoryDetail")
public class MzMaizhenyiHistoryDetailController extends BaseController {

	@Autowired
	private MzMaizhenyiHistoryDetailService detailService;
	
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(MzMaizhenyiHistoryDetail detail ) {
		try {
			detailService.insertSelective(detail);
			return JsonUtil.succeedJson(detail.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(MzMaizhenyiHistoryDetail detail, String newYunmai ) {
		try {
			detailService.updateByPrimaryKeySelective(detail, newYunmai);
			return JsonUtil.succeedJson(detail.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public JSONObject detail(@PathVariable Integer id) {
		try {
			return JsonUtil.succeedJson(detailService.selectByPrimaryKey(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
}
