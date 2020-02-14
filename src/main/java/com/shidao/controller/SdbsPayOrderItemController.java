package com.shidao.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsPayOrderItem;
import com.shidao.service.SdbsPayOrderItemService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value="/sdbsPayOrderItem")
public class SdbsPayOrderItemController {
	
	@Autowired
	SdbsPayOrderItemService payOrderItemService;
	
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(SdbsPayOrderItem payOrderItem) {
		try {
			payOrderItem.setPayDate(new Date());
			payOrderItemService.insertSelective(payOrderItem);
			return JsonUtil.succeedJson(payOrderItem.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="/update",method = RequestMethod.POST)
	public JSONObject update(SdbsPayOrderItem payOrderItem){
		try {
			payOrderItemService.updateByPrimaryKeySelective(payOrderItem);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

}
