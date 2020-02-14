package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.ExtCustomer;
import com.shidao.service.ExtCustomerService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping("/ext/customer")
public class ExtCustomerController extends BaseController {

	@Autowired
	ExtCustomerService extCustomerService;
	
	@GetMapping("/list")
	public JSONObject listCustomer(HttpServletRequest request, ExtCustomer condition) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			condition.setClubId(es.getClubId());
			return JsonUtil.succeedJson(extCustomerService.list(condition));
		}catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping("/insert")
	public JSONObject insertCustomer(ExtCustomer customer, HttpServletRequest request){
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			customer.setClubId(es.getClubId());
			extCustomerService.insertSelective(customer);
			return JsonUtil.succeedJson(customer);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping("/update")
	public JSONObject updateCustomer(ExtCustomer extCustomer) {
		try {
			extCustomerService.updateByUUIDSelective(extCustomer);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping("/delete")
	public JSONObject delete(ExtCustomer extCustomer) {
		try {
			extCustomerService.deleteBySelective(extCustomer);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
