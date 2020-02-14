package com.shidao.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.WmsCheckOrderDetail;
import com.shidao.model.WmsInventory;
import com.shidao.service.WmsCheckOrderDetailService;
import com.shidao.service.WmsInventoryService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/wmsCheckOrderDetail")
public class WmsCheckOrderDetailController extends BaseController{

	@Autowired
	private WmsCheckOrderDetailService checkOrderDetailService;
	
	@Autowired
	private WmsInventoryService inventoryService;
	
	/**
	 * 插入盘货单详情
	 * @param checkOrderDetail
	 * @return
	 */
	@RequestMapping(value ="/insert", method = RequestMethod.POST)
	public JSONObject insert(WmsCheckOrderDetail checkOrderDetail,HttpServletRequest request){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			WmsInventory inventory = new WmsInventory();
			inventory.setProductId(checkOrderDetail.getProductId());
			inventory.setWarehouseId(sm.getWarehouseId());
			List<WmsInventory> remainderAmount = inventoryService.getRemainderAmount(inventory);
			if (remainderAmount != null && !remainderAmount.isEmpty()) {
				checkOrderDetail.setFormerAmount(BigDecimal.valueOf(remainderAmount.get(0).getCheckAmount()));
			}
			BigDecimal formerAmount = checkOrderDetail.getFormerAmount();
			BigDecimal actualAmount = checkOrderDetail.getActualAmout();
			if (formerAmount != null && actualAmount != null) {
				checkOrderDetail.setChange(actualAmount.subtract(formerAmount));
			}
			checkOrderDetailService.insertSelective(checkOrderDetail);
			return JsonUtil.succeedJson(checkOrderDetail.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
	@RequestMapping(value ="/update", method = RequestMethod.POST)
	public JSONObject update(WmsCheckOrderDetail checkOrderDetail){
		try {
			if (checkOrderDetail.getActualAmout() != null) {
				WmsCheckOrderDetail key = checkOrderDetailService.selectByPrimaryKey(checkOrderDetail.getId());
				checkOrderDetail.setChange(checkOrderDetail.getActualAmout().subtract(key.getFormerAmount()));
			}
			checkOrderDetailService.updateByPrimaryKeySelective(checkOrderDetail);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value ="/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id){
		try {
			WmsCheckOrderDetail checkOrderDetail = new WmsCheckOrderDetail();
			checkOrderDetail.setId(id);
			checkOrderDetailService.deleteByPrimaryKey(checkOrderDetail);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
