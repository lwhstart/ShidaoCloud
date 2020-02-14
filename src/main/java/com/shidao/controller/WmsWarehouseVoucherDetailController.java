package com.shidao.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.WmsWarehouseVoucherDetail;
import com.shidao.service.WmsWarehouseVoucherDetailService;
import com.shidao.util.DateUtil;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping("/wmsWarehouseVoucherDetail")
public class WmsWarehouseVoucherDetailController extends BaseController{

	@Autowired
	private WmsWarehouseVoucherDetailService warehouseVoucherDetailService;
	
	/**
	 * 增加入库单详情，同时更新入库单总价
	 */
	@RequestMapping(value="insert",method=RequestMethod.POST)
	public JSONObject insert(WmsWarehouseVoucherDetail warehouseVoucherDetail){
		try {
			warehouseVoucherDetailService.insert(warehouseVoucherDetail);
			if (warehouseVoucherDetail.getId() != null) {
				WmsWarehouseVoucherDetail key = warehouseVoucherDetailService.selectByPrimaryKey(warehouseVoucherDetail.getId());
				if (key.getProductionDate() != null) {
					Date subMonth = DateUtil.dateSubMonth(key.getProductionDate(), key.getValidityMonths());
					WmsWarehouseVoucherDetail wmsWarehouseVoucherDetail = new WmsWarehouseVoucherDetail();
					wmsWarehouseVoucherDetail.setId(key.getId());
					wmsWarehouseVoucherDetail.setExpirationDate(subMonth);
					warehouseVoucherDetailService.updateByPrimaryKeySelective(wmsWarehouseVoucherDetail);
				}
			}
			return JsonUtil.succeedJson(warehouseVoucherDetail.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	/**
	 * 更新详情表并且更新订单表里的总价
	 * @param warehouseVoucherDetail
	 * @return
	 */
	@RequestMapping(value="update",method=RequestMethod.POST)
	public JSONObject update(WmsWarehouseVoucherDetail warehouseVoucherDetail){
		try {
			if (warehouseVoucherDetail.getAmount() == null) {
				throw new ShidaoException("请输入数量！");
			}
			
			if (warehouseVoucherDetail.getProductionDate() != null && warehouseVoucherDetail.getValidityMonths() != null && warehouseVoucherDetail.getExpirationDate() == null) {
				Date subMonth = DateUtil.dateSubMonth(warehouseVoucherDetail.getProductionDate(), warehouseVoucherDetail.getValidityMonths());
				warehouseVoucherDetail.setExpirationDate(subMonth);
			}
			if (warehouseVoucherDetail.getPrice() != null || warehouseVoucherDetail.getAmount() != null) {
				warehouseVoucherDetailService.update(warehouseVoucherDetail);
			} else {
				warehouseVoucherDetailService.updateByPrimaryKeySelective(warehouseVoucherDetail);
			}
			WmsWarehouseVoucherDetail key = warehouseVoucherDetailService.selectByPrimaryKey(warehouseVoucherDetail.getId());
			return JsonUtil.succeedJson(warehouseVoucherDetailService.getSumTotal(key.getOrderId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	/**
	 *删除入库单详情，同时更新入库单总价
	 */
	@RequestMapping(value="delete",method=RequestMethod.POST)
	public JSONObject delete(Integer id){
		try {
			WmsWarehouseVoucherDetail key = warehouseVoucherDetailService.selectByPrimaryKey(id);
			warehouseVoucherDetailService.delete(id);
			return JsonUtil.succeedJson(warehouseVoucherDetailService.getSumTotal(key.getOrderId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
