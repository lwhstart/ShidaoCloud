package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsServiceCardItem;
import com.shidao.service.SdbsServiceCardItemService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/serviceCardItem")
public class SdbsServiceCardItemController {

	@Autowired
	private SdbsServiceCardItemService serviceCardItemService;
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject updateSdbsServiceCardItem(SdbsServiceCardItem serviceCardItem) {
		try {
			serviceCardItemService.update(serviceCardItem);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insertSdbsServiceItemCard(SdbsServiceCardItem serviceCardItem) {
		try {
			serviceCardItemService.insertSelective(serviceCardItem);
			return JsonUtil.succeedJson(serviceCardItem.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject deleteSdbsServiceCardItem(Integer id) {
		try {
			serviceCardItemService.deleteByPrimaryKey(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年8月23日
	 * 功能:
	 * @param id
	 * @return
	 */
	@GetMapping("/list")
	public JSONObject list(SdbsServiceCardItem item) {
		try {
			return JsonUtil.succeedJson(serviceCardItemService.list(item));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
