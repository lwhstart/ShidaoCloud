package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdepConsultation;
import com.shidao.service.SdepConsultationService;
import com.shidao.util.CustomerSessionManager;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value = "/sdepConsultation")
public class SdepConsultationController extends BaseController{

	@Autowired
	private SdepConsultationService consultationService;
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(SdepConsultation consultation) {
		try {
			consultationService.update(consultation);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(SdepConsultation consultation,HttpServletRequest request) {
		try {
			Boolean isLoginOfCustomer = false;
			CustomerSessionManager csManager = new CustomerSessionManager(request.getSession(),false);
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession(),false);
			if(csManager.isLoggedIn()) {
				isLoginOfCustomer = true;
				consultation.setCustomerId(csManager.getCustomerId());
			}else if (esManager.isLoggedIn()) {
				consultation.setEmployeeId(esManager.getEmployeeId());
			}
			else {

				throw new ShidaoException("请先登录");
			}

			return JsonUtil.succeedJson(consultationService.insert(consultation,isLoginOfCustomer));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id) {
		try {
			SdepConsultation consultation = new SdepConsultation();
			consultation.setId(id);
			consultation.setDeleted(1);
			consultationService.updateByPrimaryKeySelective(consultation);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/doctor/read/{id}", method = RequestMethod.POST)
	public JSONObject doctorRead(@PathVariable("id")Integer id,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			consultationService.readQuestion(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping(value = "/customer/read/{id}", method = RequestMethod.POST)
	public JSONObject customerRead(@PathVariable("id")Integer id,HttpServletRequest request) {
		try {
			@SuppressWarnings("unused")
			CustomerSessionManager cm = new CustomerSessionManager(request.getSession());
			{
				consultationService.readAnswer(id);
				return JsonUtil.succeedJson();
			}
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/list")
	public JSONObject list(Integer pageNum,Integer pageSize,HttpServletRequest request) {
		try {
			CustomerSessionManager cm = new CustomerSessionManager(request.getSession());
			return JsonUtil.succeedJson(consultationService.listConsultAboutCustomer(cm.getCustomerId(), pageNum, pageSize));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/detail/{id}")
	public JSONObject consultDetail(@PathVariable("id")Integer id,Integer maxId,HttpServletRequest request) {
		try {
			CustomerSessionManager csm =  new CustomerSessionManager(request.getSession());
			return JsonUtil.succeedJson(consultationService.getOneConsultationDetail(id,csm.getCustomerId(),maxId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
