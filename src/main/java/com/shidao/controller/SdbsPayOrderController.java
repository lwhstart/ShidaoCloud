package com.shidao.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsPayOrder;
import com.shidao.service.SdbsPayOrderService;
import com.shidao.util.JsonUtil;
import com.shidao.util.ListResult;

@RestController
@RequestMapping(value="/sdbsPayOrder")
public class SdbsPayOrderController {

	@Autowired
	private SdbsPayOrderService payOrderService;
	
	
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(SdbsPayOrder payOrder) {
		try {
			payOrder.setPayDate(new Date());
			payOrderService.insertSelective(payOrder);
			return JsonUtil.succeedJson(payOrder.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(SdbsPayOrder payOrder,Integer pageNum) {
		try {
			JSONObject map = JsonUtil.succeedJson();
			ListResult<SdbsPayOrder> list = payOrderService.list(payOrder, pageNum, 5);
			if (list.isHasNextPage())
				map.put("nextPageNum", pageNum+1);
			map.put("payOrders", list.getList());
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
		
}
