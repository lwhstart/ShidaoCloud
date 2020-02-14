package com.shidao.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsDelivery;
import com.shidao.model.SdbsDelivery.DeliveryStatus;
import com.shidao.service.SdbsDeliveryService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;
import com.shidao.util.StringUtil;

@RestController
@RequestMapping(value = "/sdbsDelivery")
public class SdbsDeliveryController extends BaseController {

	@Autowired
	private SdbsDeliveryService deliveryService;

	// 选择需要快递复选框
	@RequestMapping(value = "/needDelivery", method = RequestMethod.POST)
	public JSONObject needDelivery(Boolean needed, Integer salesOrderId,String salesOrderIds,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Integer deliveryId = null;
			if (StringUtil.isNullOrEmpty(salesOrderIds))
				deliveryId = deliveryService.needDelivery(needed, salesOrderId,sm.getEmployeeId(),sm.getClubId(),sm.getClubType());
			else
				deliveryId=deliveryService.needDelivery(needed, salesOrderIds,sm.getEmployeeId(),sm.getClubId(),sm.getClubType());
			if (deliveryId == null)
				return JsonUtil.succeedJson();
			return JsonUtil.succeedJson(deliveryId);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	// 填写地址，点确定
	@RequestMapping(value = "/setDeliveryAddress", method = RequestMethod.POST)
	public JSONObject setDeliveryAddress(SdbsDelivery delivery, HttpServletRequest request) {
		try {
			delivery.setStatus(DeliveryStatus.DeliveryRequested);
			deliveryService.updateByPrimaryKeySelective(delivery);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月16日
	 * 功能:填写地址,勾选订单,点确定
	 * @param delivery
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/setDeliveryAddressAndOrderIds", method = RequestMethod.POST)
	public JSONObject setDeliveryAddressAndOrderIds(SdbsDelivery delivery,String salesOrderIds, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (StringUtil.isNullOrEmpty(salesOrderIds))
				throw new ShidaoException("请选择产品");
			Integer deliveryId=deliveryService.needDelivery(true, salesOrderIds,sm.getEmployeeId(),sm.getClubId(),sm.getClubType());
			delivery.setId(deliveryId);
			delivery.setStatus(DeliveryStatus.DeliveryRequested);
			deliveryService.updateByPrimaryKeySelective(delivery);
			return JsonUtil.succeedJson(deliveryId);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	// 填写订单信息，点确定
	@RequestMapping(value = "/setDeliveryInfo", method = RequestMethod.POST)
	public JSONObject setDeliveryInfo(SdbsDelivery delivery, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			delivery.setDeliveryId(sm.getEmployeeId());
			delivery.setStatus(DeliveryStatus.Delivered);
			delivery.setDeliveryDate(new Date());
			deliveryService.updateByPrimaryKeySelective(delivery);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");
			return JsonUtil.succeedJson(dateFormat.format(delivery.getDeliveryDate()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	// 修改快递单
	@RequestMapping(value = "/update/delivery", method = RequestMethod.POST)
	public JSONObject updateDelivery(SdbsDelivery delivery, HttpServletRequest request) {
		try {
			if (delivery.getDeliveryInfo() != null) {
				EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
				delivery.setDeliveryId(sm.getEmployeeId());
			}
			deliveryService.updateByPrimaryKeySelective(delivery);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public JSONObject list(Integer salesOrderId, HttpServletRequest request) {
		try {
			deliveryService.selectDeliveryBySalesOrderId(salesOrderId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public JSONObject cancel(Integer id) {
		try {
			deliveryService.cancel(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public JSONObject send(Integer id) {
		try {
			return JsonUtil.succeedJson(deliveryService.selectByPrimaryKey(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年6月14日
	 * 功能:新增快递并添加快递费
	 * @param salesOrderIds
	 * @param deliveryFee
	 * @param address
	 * @return
	 */
	@PostMapping("/insertDelivery")
	public JSONObject insertDelivery(String salesOrderIds,Float deliveryFee,String address,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (salesOrderIds.endsWith(",")) {
				salesOrderIds = salesOrderIds.substring(0, salesOrderIds.length()-1);
			}
			//新增快递费
			String salesOrderIdsA = deliveryService.insertDeliveryFee(salesOrderIds,deliveryFee);
			//订单设置快递使用状态 
			Integer deliveryId = deliveryService.needDelivery(true, salesOrderIdsA,sm.getEmployeeId(),sm.getClubId(),sm.getClubType());
			//更新为DeliveryRequested,并加入地址
			SdbsDelivery delivery = new SdbsDelivery();
			delivery.setId(deliveryId);
			delivery.setStatus(DeliveryStatus.DeliveryRequested);
			delivery.setAddress(address);
			deliveryService.updateByPrimaryKeySelective(delivery);
			return JsonUtil.succeedJson(deliveryId);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	
}
