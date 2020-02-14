package com.shidao.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsCustomerServiceGroup;
import com.shidao.service.SdbsCustomerServiceGroupService;
import com.shidao.util.CustomerSessionManager;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value = "/sdbsCustomerServiceGroup")
public class SdbsCustomerServiceGroupController extends BaseController{
	
	@Autowired
	private SdbsCustomerServiceGroupService customerServiceGroupService;
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(SdbsCustomerServiceGroup customerServiceGroup) {
		try {
			customerServiceGroupService.updateByPrimaryKeySelective(customerServiceGroup);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月25日
	 * 增加功能:添加联系人
	 * @param customerServiceGroup
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(SdbsCustomerServiceGroup customerServiceGroup,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			customerServiceGroup.setClubId(sm.getClubId());
			List<SdbsCustomerServiceGroup> list = customerServiceGroupService.list(customerServiceGroup);
			if (list != null && !list.isEmpty())
				throw new ShidaoException("医生重复");
			customerServiceGroupService.insertSelective(customerServiceGroup);
			return JsonUtil.succeedJson(customerServiceGroup.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月25日
	 * 增加功能:删除联系人
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id) {
		try {
			customerServiceGroupService.deleteByPrimaryKey(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 用户获取自己的服务列表
	 * @param customerServiceGroup
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public JSONObject list(SdbsCustomerServiceGroup customerServiceGroup,HttpServletRequest request) {
		try {
			CustomerSessionManager sessionManager = new CustomerSessionManager(request.getSession());
			customerServiceGroup.setCustomerId(sessionManager.getCustomerId());
			return JsonUtil.succeedJson(customerServiceGroupService.list(customerServiceGroup));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 转发进入微信小程序服务组
	 * @param request
	 * @param forwardUuid
	 * @return
	 */
	@PostMapping(value="/insertByforwardUuid")
	public JSONObject insertByforwardUuid(HttpServletRequest request,@RequestParam(value="forwardUuid") String forwardUuid) {
		try {
			CustomerSessionManager sessionManager = new CustomerSessionManager(request.getSession());
			customerServiceGroupService.insertByforwardUuid(sessionManager.getCustomerId(), forwardUuid);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
