package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.WmsRelationClubWarehouse;
import com.shidao.model.WmsRelationClubWarehouseVendor;
import com.shidao.model.WmsWarehouse;
import com.shidao.service.WmsRelationClubWarehouseService;
import com.shidao.service.WmsWarehouseService;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value = "/wmsWarehouse")
public class WmsWarehouseController extends BaseController{

	@Autowired
	private WmsWarehouseService warehouseService;
	
	@Autowired
	private WmsRelationClubWarehouseService relationClubWarehouseService;
	
	/**
	 * 增加仓库和仓库、门店关系表
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(WmsWarehouse warehouse,HttpServletRequest request) {
		try {
			warehouseService.insert(warehouse, request);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(WmsWarehouse warehouse) {
		try {
			warehouseService.updateByPrimaryKeySelective(warehouse);
			return JsonUtil.succeedJson(warehouse.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	/**
	 * 仓库删除、仓库和门店关系表删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(WmsWarehouse warehouse,HttpServletRequest request) {
		try {
			warehouseService.delete(warehouse, request);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 删除仓库对应的供应商
	 * @param vendorId
	 * @param warehouseId
	 * @return
	 */
	@RequestMapping(value = "/deleteWarehouseVendor", method = RequestMethod.POST)
	public JSONObject deleteWarehouseVendor(Integer vendorId,Integer warehouseId) {
		try {
			if (warehouseId == null) {
				throw new ShidaoException("请选择仓库！");
			}
			if (warehouseId != null && vendorId == null) {
				throw new ShidaoException("请选择供应商！");
			}
			warehouseService.deleteWarehouseVendor(vendorId, warehouseId,null);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 插入仓库对应的门供应商
	 * @param relationClubWarehouseVendor
	 * @return
	 */
	@RequestMapping(value = "/insertWarehouseVendor", method = RequestMethod.POST)
	public JSONObject insertWarehouseVendor(WmsRelationClubWarehouseVendor relationClubWarehouseVendor) {
		try {
			warehouseService.insertWarehouseVendor(relationClubWarehouseVendor);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月24日
	 * 功能:得到仓库的基本信息
	 * @param warehouseId
	 * @return
	 */
	@GetMapping(value = "/getInfo")
	public JSONObject getInfo(Integer warehouseId) {
		try {
			return JsonUtil.succeedJson(warehouseService.selectByPrimaryKey(warehouseId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月24日
	 * 功能:仓库加门店或者供应商
	 * @param warehouseId
	 * @param clubId
	 * @param vendorId
	 * @param type
	 * @return
	 */
	@PostMapping("/relation/add/{type}")
	public JSONObject relationAdd(Integer warehouseId,Integer itemId, @PathVariable String type) {
		try {
			switch (type) {
			case "vendor":
				WmsRelationClubWarehouseVendor condition = new WmsRelationClubWarehouseVendor();
				condition.setVendorId(itemId);
				condition.setWarehouseId(warehouseId);
				warehouseService.insertWarehouseVendor(condition);
				return JsonUtil.succeedJson(condition.getId());
			case "club":
				WmsRelationClubWarehouse relationClubWarehouse = new WmsRelationClubWarehouse();
				relationClubWarehouse.setClubId(itemId);
				relationClubWarehouse.setWarehouseId(warehouseId);
				relationClubWarehouseService.insertSelective(relationClubWarehouse);
				return JsonUtil.succeedJson(relationClubWarehouse.getId());
			default:
				throw new ShidaoException("不知此的类型："+type);
			}
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月24日
	 * 功能:删除
	 * @param relationId
	 * @param type
	 * @return
	 */
	@PostMapping("/relation/remove/{type}")
	public JSONObject relationRemove(Integer relationId,@PathVariable String type) {
		try {
			switch (type) {
			case "vendor":
				warehouseService.deleteWarehouseVendor(null,null,relationId);
				break;
			case "club":
				relationClubWarehouseService.deleteByPrimaryKey(relationId);
				break;
			default:
				break;
			}
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
