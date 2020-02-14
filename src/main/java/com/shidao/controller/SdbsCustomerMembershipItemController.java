package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsCustomerMembershipItem;
import com.shidao.service.SdbsCustomerMembershipItemService;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value="/customerMembershipItem")
public class SdbsCustomerMembershipItemController {

	@Autowired
	SdbsCustomerMembershipItemService customerMembershipItemService;
	
	/**根据顾客id和项目ID查询相对应的项目卡
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getServiceCard",method=RequestMethod.GET)
	public JSONObject getByCustomer(SdbsCustomerMembershipItem customerMembershipItem){
		try {
			return JsonUtil.succeedJson(customerMembershipItemService.list(customerMembershipItem));
		} catch (ShidaoException e) {
			return JsonUtil.errjson(e);
		}catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 根据ID删除
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public JSONObject deleteById(Integer id,HttpServletRequest request){
		try {
			Integer employeeId = (Integer)request.getSession().getAttribute("employeeId");
			if (employeeId == null)
			throw new ShidaoException("没有登录");
			customerMembershipItemService.deleteById(id,employeeId);
			return JsonUtil.succeedJson();
		}catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 根据ID更新
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public JSONObject updateCustomerCardItem(SdbsCustomerMembershipItem customerMembershipItem,HttpServletRequest request){
		try {
			Integer employeeId = (Integer)request.getSession().getAttribute("employeeId");
			if (employeeId == null)
			throw new ShidaoException("没有登录");
			customerMembershipItemService.update(customerMembershipItem,employeeId);
			return JsonUtil.succeedJson();
		}catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 更新套餐卡里剩余的次数
	 * @param customerMembershipItem
	 * @return
	 */
	@RequestMapping(value="/updateUnitLeft",method=RequestMethod.POST)
	public JSONObject updateUnitLeft(SdbsCustomerMembershipItem customerMembershipItem){
		try {
			customerMembershipItemService.updateBycustomerMemberShipId(customerMembershipItem);
			return JsonUtil.succeedJson();
		}catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
