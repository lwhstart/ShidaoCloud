package com.shidao.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.WmsPurchaseOrder;
import com.shidao.model.WmsWarehouseVoucher;
import com.shidao.model.WmsWarehouseVoucher.StatusType;
import com.shidao.model.WmsWarehouseVoucher.WarehouseVoucherCategory;
import com.shidao.model.WmsWarehouseVoucherDetail;
import com.shidao.service.WmsPurchaseOrderService;
import com.shidao.service.WmsWarehouseVoucherDetailService;
import com.shidao.service.WmsWarehouseVoucherService;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.StringUtil;

@RestController
@RequestMapping("/wmsWarehouseVoucher")
public class WmsWarehouseVoucherController extends BaseController{

	@Autowired
	private WmsWarehouseVoucherService warehouseVoucherService;
	
	@Autowired
	private WmsPurchaseOrderService purchaseOrderService;
	
	@Autowired
	private WmsWarehouseVoucherDetailService warehouseVoucherDetailService;
	
	/**
	 * 审核，若通过则加入库存表里，否则不加
	 */
	@RequestMapping(value = "/approve", method = RequestMethod.POST)
	public JSONObject approval(Integer id,HttpServletRequest request) {
		try {
			EmployeeSessionManager sManager= new EmployeeSessionManager(request.getSession());
			warehouseVoucherService.approve(id,sManager.getEmployeeId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	/**
	 * 请求审核
	 */
	@RequestMapping(value = "/requestApproval", method = RequestMethod.POST)
	public JSONObject requestApproval(WmsWarehouseVoucher wmsWarehouseVoucher) {
		try {
			wmsWarehouseVoucher.setStatus(StatusType.RequestApproval.name());
			WmsWarehouseVoucherDetail warehouseVoucherDetail = new WmsWarehouseVoucherDetail();
			warehouseVoucherDetail.setOrderId(wmsWarehouseVoucher.getId());
			List<WmsWarehouseVoucherDetail> list = warehouseVoucherDetailService.list(warehouseVoucherDetail);
			if (list != null && !list.isEmpty()) {
				warehouseVoucherDetail.setHaveDate("1");
				List<WmsWarehouseVoucherDetail> list2 = warehouseVoucherDetailService.list(warehouseVoucherDetail);
				if (list2 != null && !list2.isEmpty()) {
					throw new ShidaoException(list2.get(0).getProduct().getName()+"--没有生产日期和过期日期！"); 
				}
			} else {
				throw new ShidaoException("你都没东西，肯定不让你提交！");
			}
			warehouseVoucherService.updateByPrimaryKeySelective(wmsWarehouseVoucher);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping(value="update",method=RequestMethod.POST)
	public JSONObject update(WmsWarehouseVoucher wmsWarehouseVoucher,HttpServletRequest request){
		try {
			wmsWarehouseVoucher.setApprovalDate(new Date());
			wmsWarehouseVoucher.setApproverId((Integer)request.getSession().getAttribute("employeeId"));
			warehouseVoucherService.updateByPrimaryKeySelective(wmsWarehouseVoucher);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping(value="delete",method=RequestMethod.POST)
	public JSONObject delete(Integer id){
		try {
			warehouseVoucherService.delete(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="insert",method=RequestMethod.POST)
	public JSONObject insert(WmsWarehouseVoucher wmsWarehouseVoucher,HttpServletRequest request){
		try {
			// 该门店仓库
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			wmsWarehouseVoucher.setBatchNumber(StringUtil.orderNumber());
			wmsWarehouseVoucher.setCreatorId(esManager.getEmployeeId());
			wmsWarehouseVoucher.setStatus(StatusType.Created.name());
			wmsWarehouseVoucher.setWarehouseId(esManager.getWarehouseId());
			wmsWarehouseVoucher.setCategory(WarehouseVoucherCategory.Manually);
			warehouseVoucherService.insertSelective(wmsWarehouseVoucher);
			return JsonUtil.succeedJson(wmsWarehouseVoucher.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
	/**
	 * 从采购单插入到入库单和入库单详情
	 * @param wmsWarehouseVoucher
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/createWarehouseVoucher", method = RequestMethod.POST)
	public JSONObject insert(WmsWarehouseVoucher wmsWarehouseVoucher,Integer purchaseOrderId,HttpServletRequest request) {
		try {
			WmsPurchaseOrder purchaseOrder = purchaseOrderService.selectByPrimaryKey(purchaseOrderId);
			if (!purchaseOrder.getStatus().equals(StatusType.Approved.name())) {
				throw new ShidaoException("审核不通过，不允许入库");
			}
			wmsWarehouseVoucher.setVendorId(purchaseOrder.getVendorId());
			wmsWarehouseVoucher.setCategory(WarehouseVoucherCategory.Purchase);
			wmsWarehouseVoucher.setWarehouseId(purchaseOrder.getWarehouseId());
			wmsWarehouseVoucher.setBatchNumber(purchaseOrder.getPurchaseCode());
			wmsWarehouseVoucher.setCreatorId((Integer)request.getSession().getAttribute("employeeId"));
			wmsWarehouseVoucher.setStatus(StatusType.Created.name());
			wmsWarehouseVoucher.setTotal(purchaseOrder.getTotal());
			Integer id = warehouseVoucherService.insert(wmsWarehouseVoucher,purchaseOrderId);
			return JsonUtil.succeedJson(id);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
}
