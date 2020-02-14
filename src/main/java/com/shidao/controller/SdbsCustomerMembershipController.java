package com.shidao.controller;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.MembershipOperation;
import com.shidao.model.SdbsCustomer;
import com.shidao.model.SdbsCustomerMembership;
import com.shidao.service.SdbsCustomerMembershipService;
import com.shidao.util.CustomerSessionManager;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;
import com.shidao.vo.MembershipOperationVO;

@RestController
@RequestMapping(value = "/customerMembership")
public class SdbsCustomerMembershipController extends BaseController {

	@Autowired
	private SdbsCustomerMembershipService custMembershipService;
	
	@RequestMapping(value = "")
	public JSONObject getByCustomer(HttpServletRequest request) {
		try {
			Integer currentCusomterId = SdbsCustomer.getCurrentId(request);
			SdbsCustomerMembership conditon = new SdbsCustomerMembership();
			conditon.setCustomerId(currentCusomterId);
			return JsonUtil.succeedJson(custMembershipService.list(conditon));
		} catch (ShidaoException e) {
			return JsonUtil.errjson(e);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id,HttpServletRequest request) {
		try {
			Integer employeeId = (Integer)request.getSession().getAttribute("employeeId");
			if (employeeId == null)
				throw new ShidaoException("没有登录");
			if (id == null)
				throw new ShidaoException("没有编号");
			custMembershipService.deleteById(id,employeeId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public JSONObject cancelServiceCard(SdbsCustomerMembership sdbsCustomerMembership) {
		try {
			custMembershipService.updateByPrimaryKeySelective(sdbsCustomerMembership);
			if (sdbsCustomerMembership.getId() != null) {
				return JsonUtil.succeedJson();
			}
			throw new ShidaoException("删除失败！请重试");
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/myFreeCancleCount", method = RequestMethod.GET)
	public JSONObject getMyFreeCancleCount(HttpServletRequest request) {
		try {
			Integer currentCustomerID = SdbsCustomer.getCurrentId(request);

			custMembershipService.getByCustomerId(currentCustomerID);
			return JsonUtil.succeedJson(/*custMembershipService.getByCustomerId(currentCustomerID).getFreeCancelCount()*/);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 客户退卡
	 * 1. 设置卡位
	 * @author YZL 2018-6-8 
	 * @param request
	 * @param cardId
	 * @return
	 */
	@PostMapping("/refundcard")
	public JSONObject refundCard(HttpServletRequest request, Integer cardId,String refundCardInfo,BigDecimal refundTotal) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			custMembershipService.refundCard(cardId, esManager.getEmployeeId(),refundCardInfo,refundTotal);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 补卡(套餐卡或会员卡)
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{operation}",method = RequestMethod.POST)
	public JSONObject operation(SdbsCustomerMembership customerMembership,
			@PathVariable(value = "operation") MembershipOperation operation,
			HttpServletRequest request,MembershipOperationVO vo) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			vo.setClubId(sm.getClubId());
			vo.setOperatorId(sm.getEmployeeId());
			customerMembership.setLastModifier(sm.getEmployeeId());
			custMembershipService.operation(customerMembership, operation, vo);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/isCustomerMembership")
	public JSONObject getIsCustomerMembership(HttpServletRequest request) {
		try {
			CustomerSessionManager customerSessionManager = new CustomerSessionManager(request.getSession());
			return JsonUtil.succeedJson(custMembershipService.getIsCustomerMembership(customerSessionManager.getCustomerId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/usedHistory/{membershipCardId}")
	public JSONObject usedHistory(HttpServletRequest request,@PathVariable(value="membershipCardId") Integer membershipCardId) {
		try {
			return JsonUtil.succeedJson(custMembershipService.listUsedHistory(membershipCardId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
