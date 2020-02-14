package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.ProductCategory;
import com.shidao.model.WmsInventory;
import com.shidao.service.WmsInventoryService;
import com.shidao.util.JsonUtil;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping("/wmsInventory")
public class WmsInventoryController extends BaseController{

	@Autowired
	private WmsInventoryService inventoryService;
	/**
	 * 过期处理
	 * @param inventory
	 * @return
	 */
	@RequestMapping(value = "/expire", method = RequestMethod.POST)
	public JSONObject expire(WmsInventory inventory,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			inventoryService.expire(inventory, sm.getWarehouseId(),sm.getEmployeeId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 得到该药品在库存表里的所有批号
	 * @param inventory
	 * @return
	 */
	@RequestMapping(value = "/list",method=RequestMethod.GET)
	public JSONObject getMedicine(WmsInventory inventory, HttpServletRequest request) {
		try {
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession());
			Integer employeeId = sManager.getEmployeeId();
			Integer warehouseId = sManager.getWarehouseId();
			if (employeeId == null) {
				throw new ShidaoException("未登录，请登录！");
			}
			inventory.setWarehouseId(warehouseId);
			return JsonUtil.succeedJson(inventoryService.list(inventory));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 获取，在某个仓库里， 指定类型的药品供应商，和余量
	 * @author yzl 2018年9月4日
	 * @param medicineId
	 * @param category
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/list/vendorOfMedicine",method=RequestMethod.GET)
	public JSONObject getVendorByMedicineCode(@RequestParam(name="medicineId") Integer medicineId,
			@RequestParam(name="category") ProductCategory category,
			HttpServletRequest request){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(inventoryService.listVendorByMedicineOfWarehouse(medicineId, sm.getWarehouseId(),category));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="listProductByInventory",method=RequestMethod.GET)
	public JSONObject listProductByInventory(WmsInventory condition, HttpServletRequest request){
		try {
			EmployeeSessionManager sManager  = new EmployeeSessionManager(request.getSession());
			if(sManager.getEmployeeId() == null){
				throw new ShidaoException("请先登录");
			}
			condition.setWarehouseId(sManager.getWarehouseId());
			
			return JsonUtil.succeedJson(inventoryService.listProductByInventory(condition));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	
}
