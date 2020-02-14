package com.shidao.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.WmsCheckOrder;
import com.shidao.model.WmsCheckOrder.CheckStatus;
import com.shidao.model.WmsCheckOrderDetail;
import com.shidao.service.WmsCheckOrderDetailService;
import com.shidao.service.WmsCheckOrderService;
import com.shidao.service.WmsRelationClubWarehouseService;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.StringUtil;

@RestController
@RequestMapping(value = "/wmsCheckOrder")
public class WmsCheckOrderController extends BaseController{
	
	@Autowired
	private WmsCheckOrderService wmsCheckOrderService;
	
	@Autowired
	private WmsCheckOrderDetailService checkOrderDetailService;
	
	@Autowired
	private WmsRelationClubWarehouseService relationClubWarehouseService;

	@RequestMapping(value ="/insert", method = RequestMethod.POST)
	public JSONObject insert(WmsCheckOrder checkOrder,HttpServletRequest request){
		try {
			// 该门店仓库
			Integer warehouseId = relationClubWarehouseService.getWarehouseIdByClubId((Integer)request.getSession().getAttribute("clubId"));
			checkOrder.setBatchNumber(StringUtil.orderNumber());
			checkOrder.setOperatorId((Integer)request.getSession().getAttribute("employeeId"));
			checkOrder.setStatus(CheckStatus.Checking);
			checkOrder.setWarehouseId(warehouseId);
			wmsCheckOrderService.insert(checkOrder);
			return JsonUtil.succeedJson(checkOrder.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
	@RequestMapping(value ="/update", method = RequestMethod.POST)
	public JSONObject update(WmsCheckOrder checkOrder,HttpServletRequest request){
		try {
			if (checkOrder.getStatus().equals(CheckStatus.RequestApproval)) {
				checkOrder.setApproverId((Integer)request.getSession().getAttribute("employeeId"));
				checkOrder.setApprovalDate(new Date());
				WmsCheckOrderDetail checkOrderDetail = new WmsCheckOrderDetail();
				checkOrderDetail.setCheckOrderId(checkOrder.getId());
				List<WmsCheckOrderDetail> list = checkOrderDetailService.list(checkOrderDetail);
				if (list != null && !list.isEmpty()) {
					checkOrderDetail.setHaveDate("1");
					List<WmsCheckOrderDetail> list2 = checkOrderDetailService.list(checkOrderDetail);
					if (list2 != null && !list2.isEmpty()) {
						throw new ShidaoException(list2.get(0).getMedicine().getName()+"--没有过期日期还是过期在昨天？");
					}
				} else {
					throw new ShidaoException("没有数据，你提交不了！");
				}
			}
			wmsCheckOrderService.updateByPrimaryKeySelective(checkOrder);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 审核是否通过，若通过则添加到入库表里
	 */
	@RequestMapping(value ="/approve", method = RequestMethod.POST)
	public JSONObject approval(Integer id,HttpServletRequest request){
		try {
			EmployeeSessionManager sManager= new EmployeeSessionManager(request.getSession());
			wmsCheckOrderService.approve(id, sManager.getEmployeeId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value ="/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id){
		try {
			wmsCheckOrderService.delete(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="/autoInsert",method=RequestMethod.POST)
	public JSONObject autoInsert(HttpServletRequest request){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			wmsCheckOrderService.autoInsert(sm.getEmployeeId(),sm.getWarehouseId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
}
