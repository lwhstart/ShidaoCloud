package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.ProductCategory;
import com.shidao.model.WmsInventoryWarningSetting;
import com.shidao.service.WmsInventoryWarningSettingService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping("/wmsInventoryWarningSetting")
public class WmsInventoryWarningSettingController {

	@Autowired
	private WmsInventoryWarningSettingService warningSettingService;
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月25日<br>
	 * 功能:一键采购
	 * @param request
	 * @param category
	 * @return
	 */
	@PostMapping("/makePurch4Warning")
	public JSONObject makePurch4Warning(HttpServletRequest request,ProductCategory category){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(warningSettingService.makePurch4Warning(sm.getWarehouseId(), category, sm.getEmployeeId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月25日<br>
	 * @param request
	 * @param id
	 * @return
	 */
	@PostMapping("/delete")
	public JSONObject delete(HttpServletRequest request,Integer id){
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			warningSettingService.deleteByPrimaryKey(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping("/update")
	public JSONObject update(HttpServletRequest request,WmsInventoryWarningSetting condition){
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			warningSettingService.updateByPrimaryKeySelective(condition);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping("/insert")
	public JSONObject insert(HttpServletRequest request,WmsInventoryWarningSetting condition){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (condition.getCategory()==null) {
				condition.setCategory(ProductCategory.Keliji);
			}
			condition.setWarehouseId(sm.getWarehouseId());
			warningSettingService.insertSelective(condition);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月21日<br>
	 * 功能:重置库存报警
	 * @param category
	 * @return
	 */
	@PostMapping("/resetWarningSetting")
	public JSONObject resetWarningSetting(HttpServletRequest request,ProductCategory category) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			warningSettingService.resetWarningSetting(sm.getWarehouseId(), category);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
