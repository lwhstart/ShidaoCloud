package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.ProductCategory;
import com.shidao.model.DatePeriodParameter;
import com.shidao.model.WmsPurchaseRequestOrder;
import com.shidao.service.WmsPurchaseRequestOrderService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/wmsPurchaseRequestOrder")
public class WmsPurchaseRequestOrderController extends BaseController{
	
	@Autowired
	private WmsPurchaseRequestOrderService requestOrderService;
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月18日<br>
	 * 功能:一键创建采购申请单和采购申请单详情
	 * @param request
	 * @param category
	 * @param period
	 * @return
	 */
	@PostMapping( "/createdPurchaseRequestOrder")
	public JSONObject createdPurchaseRequestOrder(HttpServletRequest request, ProductCategory category,DatePeriodParameter period) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(requestOrderService.createdPurchaseRequestOrder(sm.getEmployeeId(), sm.getWarehouseId(), category, sm.getClubId(), period));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月18日<br>
	 * 功能:提交申请
	 * @param request
	 * @param condition
	 * @return
	 */
	@PostMapping( "/approval")
	public JSONObject approval(HttpServletRequest request, WmsPurchaseRequestOrder condition) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setLastModifier(sm.getEmployeeId());
			requestOrderService.approval(condition);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月25日<br>
	 * 功能:删除订单和订单详情
	 * @param request
	 * @param id
	 * @return
	 */
	@PostMapping( "/delete")
	public JSONObject delete(HttpServletRequest request, Integer id) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			requestOrderService.delete(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
