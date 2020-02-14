package com.shidao.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.RefundRequestStatus;
import com.shidao.model.SdbsRefundRequest;
import com.shidao.model.SdbsSalesOrder;
import com.shidao.service.SdbsRefundRequestsService;
import com.shidao.service.SdbsSalesOrderService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value = "/sdbsRefundRequest")
public class SdbsRefundRequestController extends BaseController{
	
	@Autowired
	private SdbsRefundRequestsService refundRequestsService;
	
	@Autowired
	private SdbsSalesOrderService salesOrderService;
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月17日
	 * 功能:退款请求
	 * @param salesOrderId
	 * @param request
	 * @return
	 */
	@PostMapping("/refund/request")
	public JSONObject refundRequest(SdbsRefundRequest condition,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			SdbsRefundRequest refundRequest = new SdbsRefundRequest();
			refundRequest.setSalesOrderId(condition.getSalesOrderId());
			refundRequest.setStatus(RefundRequestStatus.ApprovalRequesting);
			List<SdbsRefundRequest> list = refundRequestsService.list(refundRequest);
			if (list != null && !list.isEmpty())
				throw new ShidaoException("申请退款中，请耐心等待");
			SdbsSalesOrder salesOrder = salesOrderService.selectByPrimaryKey(condition.getSalesOrderId());
			condition.setRequestCustomerId(salesOrder.getCustomerId());
			condition.setRequestEmployeeId(sm.getEmployeeId());
			condition.setRequestName(sm.getCurrent().getName());
			condition.setRequestDate(new Date());
			condition.setStatus(RefundRequestStatus.ApprovalRequesting);
			refundRequestsService.insertSelective(condition);
			return JsonUtil.succeedJson(condition.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月17日
	 * 功能:退款审核
	 * @param condition
	 * @param request
	 * @return
	 */
	@PostMapping("/approval/request")
	public JSONObject approvalRequest(SdbsRefundRequest condition,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			refundRequestsService.approvalRequest(condition, sm.getEmployeeId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
