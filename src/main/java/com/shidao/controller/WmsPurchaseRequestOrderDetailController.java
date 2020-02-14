package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.WmsPurchaseRequestOrderDetail;
import com.shidao.service.WmsPurchaseRequestOrderDetailService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/wmsPurchaseRequestOrderDetail")
public class WmsPurchaseRequestOrderDetailController extends BaseController{
	
	@Autowired
	private WmsPurchaseRequestOrderDetailService requestOrderDetailService;
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月21日<br>
	 * 功能:更改供应商获得所有的供应商
	 * @param request
	 * @param medicineId
	 * @param vendorId
	 * @return
	 */
	@GetMapping( "/getMedicineVendorInfo")
	public JSONObject getMedicineVendorInfo(HttpServletRequest request, Integer id) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			return JsonUtil.succeedJson(requestOrderDetailService.getMedicineVendorInfoById(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月21日<br>
	 * 功能:更新实际采购数量和备注
	 * @param request
	 * @param condition
	 * @return
	 */
	@PostMapping( "/updateQuantityAndCommtens")
	public JSONObject updateQuantityAndCommtens(HttpServletRequest request, WmsPurchaseRequestOrderDetail condition) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(requestOrderDetailService.updateQuantityAndCommtens(condition,sm.getEmployeeId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月24日<br>
	 * @param request
	 * @param condition
	 * @return
	 */
	@PostMapping( "/update")
	public JSONObject update(HttpServletRequest request, WmsPurchaseRequestOrderDetail condition) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			requestOrderDetailService.updateByPrimaryKeySelective(condition,sm.getEmployeeId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping( "/delete")
	public JSONObject delete(HttpServletRequest request, Integer id) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(requestOrderDetailService.deleteByPrimaryKey(id,sm.getEmployeeId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月21日<br>
	 * 功能:更改供应商，动态修改参数
	 * @param request
	 * @param condition
	 * @return
	 */
	@PostMapping( "/updateMedicineVendor")
	public JSONObject updateMedicineVendor(HttpServletRequest request, WmsPurchaseRequestOrderDetail condition) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			requestOrderDetailService.updateMedicineVendorId(condition.getId(), condition.getActualVendorId(),sm.getEmployeeId());
			return JsonUtil.succeedJson(requestOrderDetailService.selectByPrimaryKey(condition.getId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月21日<br>
	 * 功能:新增新的药
	 * @param request
	 * @param condition
	 * @return
	 */
	@PostMapping( "/insertNewMenicine")
	public JSONObject insertNewMenicine(HttpServletRequest request, WmsPurchaseRequestOrderDetail condition) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(requestOrderDetailService.insertNewMenicine(condition.getOrderId(), condition.getMedicineId(),sm.getClubId(),sm.getEmployeeId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
