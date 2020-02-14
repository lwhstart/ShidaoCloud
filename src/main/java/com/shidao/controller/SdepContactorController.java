package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdepContactor;
import com.shidao.service.SdepContactorService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value="/sdepContactor")
public class SdepContactorController extends BaseController{

	@Autowired
	private SdepContactorService contactorService;
	
	@GetMapping(value="/listCustomerContactor")
	public JSONObject listCustomerContactor(SdepContactor contactor) {
		try {
			return JsonUtil.succeedJson(contactorService.listCustomerContactor(contactor));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 只为助理app使用
	 * @param request
	 * @param id
	 * @return
	 */
	@GetMapping(value="/app/list")
	public JSONObject selectSendAppByUserId(HttpServletRequest request,Integer contactorId) {
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(contactorService.selectSendAppByUserId(employeeSessionManager.getWebsocketId(),contactorId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
