package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.CrmCustomerNotice;
import com.shidao.service.CrmCustomerNoticeService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/crmCustomerNotice")
public class CrmCustomerNoticeController extends BaseController{

	@Autowired
	private CrmCustomerNoticeService customerNoticeService;
	
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(CrmCustomerNotice customerNotice) {
		try {
			customerNoticeService.insertSelective(customerNotice);
			return JsonUtil.succeedJson(customerNotice.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(CrmCustomerNotice customerNotice) {
		try {
			customerNoticeService.updateByPrimaryKeySelective(customerNotice);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id) {
		try {
			customerNoticeService.deleteByPrimaryKey(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	/**
	 * 发布
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/issued", method = RequestMethod.POST)
	public JSONObject issued(Integer id) {
		try {
			CrmCustomerNotice customerNotice = new CrmCustomerNotice();
			customerNotice.setId(id);
			customerNotice.setIsIssue(1);
			customerNoticeService.updateByPrimaryKeySelective(customerNotice);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	/**
	 * 撤回
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/recall", method = RequestMethod.POST)
	public JSONObject recall(Integer id) {
		try {
			CrmCustomerNotice customerNotice = new CrmCustomerNotice();
			customerNotice.setId(id);
			customerNotice.setIsIssue(0);
			customerNoticeService.updateByPrimaryKeySelective(customerNotice);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
