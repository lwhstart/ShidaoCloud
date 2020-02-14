package com.shidao.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsProduct;
import com.shidao.model.SdbsVendor;
import com.shidao.model.WmsPurchaseOrderDetail;
import com.shidao.service.SdbsProductService;
import com.shidao.service.SdbsVendorService;
import com.shidao.service.WmsPurchaseOrderDetailService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping("/wmsPurchaseOrderDetail")
public class WmsPurchaseOrderDetailController {

	@Autowired
	private WmsPurchaseOrderDetailService purchaseOrderDetailService;
	
	@Autowired
	private SdbsProductService productService;
	
	@Autowired
	private SdbsVendorService sdbsVendorService;
	
	@RequestMapping(value="insert",method=RequestMethod.POST)
	public JSONObject insert(WmsPurchaseOrderDetail purchaseOrderDetail){
		try {
			purchaseOrderDetailService.insert(purchaseOrderDetail);
			return JsonUtil.succeedJson(purchaseOrderDetail.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping(value="update",method=RequestMethod.POST)
	public JSONObject update(WmsPurchaseOrderDetail purchaseOrderDetail){
		try {
			WmsPurchaseOrderDetail key = purchaseOrderDetailService.selectByPrimaryKey(purchaseOrderDetail.getId());
			purchaseOrderDetailService.update(purchaseOrderDetail);
			return JsonUtil.succeedJson(purchaseOrderDetailService.getSumTotal(key.getPurchaseOrderId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping(value="delete",method=RequestMethod.POST)
	public JSONObject delete(Integer id){
		try {
			WmsPurchaseOrderDetail key = purchaseOrderDetailService.selectByPrimaryKey(id);
			purchaseOrderDetailService.delete(id);
			BigDecimal sumTotal = purchaseOrderDetailService.getSumTotal(key.getPurchaseOrderId());
			return JsonUtil.succeedJson(sumTotal == null?BigDecimal.ZERO:sumTotal);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 根据编码，名字，拼音首字母查询药品
	 */
	@RequestMapping(value="medicine",method=RequestMethod.GET)
	public JSONObject medicine(SdbsProduct product,Integer vendorId,HttpServletRequest request){
		try{
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			SdbsVendor vendor = new SdbsVendor();
			vendor.setWarehouseId(sm.getWarehouseId());
			vendor.setDeleted(0);
			product.setVendorsParams(sdbsVendorService.list(vendor));
			product.setVendorId(vendorId);
			List<SdbsProduct> products = productService.list(product);
			return JsonUtil.succeedJson(products);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
