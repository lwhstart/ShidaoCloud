package com.shidao.controller;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.WmsDeliveryVoucherDetail;
import com.shidao.service.WmsDeliveryVoucherDetailService;
import com.shidao.service.WmsInventoryService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value = "/deliveryVoucherDetail")
public class WmsDeliveryVoucherDetailController extends BaseController {

	@Autowired
	private WmsDeliveryVoucherDetailService deliveryVoucherDetailService;
	
	@Autowired
	private WmsInventoryService inventoryService;

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(WmsDeliveryVoucherDetail deliveryVoucherDetail,HttpServletRequest request) {
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			Integer warehouseId = employeeSessionManager.getWarehouseId();
			
			if (deliveryVoucherDetail.getProductId() == null) {
				throw new ShidaoException("出库不能没有产品id");
			}
			
			Float price = inventoryService.getJqPrice(deliveryVoucherDetail.getProductId(), warehouseId);
			deliveryVoucherDetail.setPrice(BigDecimal.valueOf(price));
			deliveryVoucherDetailService.insertSelective(deliveryVoucherDetail);
			JSONObject result = JsonUtil.succeedJson(deliveryVoucherDetail.getId());			
			result.put("price", price);
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(WmsDeliveryVoucherDetail deliveryVoucherDetail,HttpServletRequest request) {
		try {
			WmsDeliveryVoucherDetail key = new WmsDeliveryVoucherDetail();
			if (deliveryVoucherDetail.getAmount() != null) {
				key = deliveryVoucherDetailService.selectByPrimaryKey(deliveryVoucherDetail.getId());
				deliveryVoucherDetail.setTotal(deliveryVoucherDetail.getAmount().multiply(key.getPrice()));
			}

			deliveryVoucherDetailService.updateByPrimaryKeySelective(deliveryVoucherDetail);
			// 更新后重新算总价
			if (deliveryVoucherDetail.getId() != null && deliveryVoucherDetail.getTotal() != null) {
				deliveryVoucherDetailService.updateTotalPrice(key.getDeliveryVoucherId());
			}
			return JsonUtil.succeedJson(deliveryVoucherDetailService.getSumTotalByDeliveryVoucherId(key.getDeliveryVoucherId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/updateVendor", method = RequestMethod.POST)
	public JSONObject updateDelivery(HttpServletRequest request,
			@RequestParam("id") Integer id, @RequestParam("productId") Integer productId) {
		try {
			new EmployeeSessionManager(request.getSession());
			deliveryVoucherDetailService.updateVendor(id, productId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id) {
		try {

			WmsDeliveryVoucherDetail key = deliveryVoucherDetailService.selectByPrimaryKey(id);
			deliveryVoucherDetailService.deleteByPrimaryKey(id);
			// 删除后重新算总价
			deliveryVoucherDetailService.updateTotalPrice(key.getDeliveryVoucherId());
			return JsonUtil.succeedJson(deliveryVoucherDetailService.getSumTotalByDeliveryVoucherId(key.getDeliveryVoucherId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
