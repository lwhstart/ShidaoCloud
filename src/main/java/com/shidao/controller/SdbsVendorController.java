package com.shidao.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.Position;
import com.shidao.model.SdbsVendor;
import com.shidao.service.SdbsVendorService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value = "/sdbsVendor")
public class SdbsVendorController extends BaseController {

	@Autowired
	private SdbsVendorService sdbsVendorService;

	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(SdbsVendor condition, Integer pageNum, Integer pageSize ) {
		try {
			Map<String, Object> result = sdbsVendorService.listByCondition(condition, pageNum, pageSize);
			return JsonUtil.succeedJson(result);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(HttpServletRequest request,SdbsVendor sdbsVendor){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Position position = Position.parse(sm.getPosition());
			switch (position) {
			case CenterAdmin:
				sdbsVendorService.insert(sdbsVendor);
				break;
			case PharmacyManager:
				sdbsVendorService.insert(sdbsVendor,sm.getWarehouseId());
				break;
			default:
				throw new ShidaoException("没有权限创建供应商");
			}
			return JsonUtil.succeedJson(sdbsVendor.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	public JSONObject deleteById(@PathVariable(value="id") Integer id,HttpServletRequest request){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Position position = Position.parse(sm.getPosition());
			switch (position) {
			case CenterAdmin:
				sdbsVendorService.delete(id);
				break;
			case PharmacyManager:
				sdbsVendorService.delete(id,sm.getWarehouseId());
				break;
			default:
				break;
			}
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject vendorPost(Model model,SdbsVendor sdbsVendor){
		try {
			sdbsVendorService.updateByPrimaryKeySelective(sdbsVendor);		
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 根据供应商ID获取折扣率
	 * @param vendorId
	 * @return
	 */
	@RequestMapping(value = "/getDiscount", method = RequestMethod.GET)
	public JSONObject getDiscount(Integer vendorId){
		try {
			return JsonUtil.succeedJson(sdbsVendorService.selectByPrimaryKey(vendorId).getDiscount());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
