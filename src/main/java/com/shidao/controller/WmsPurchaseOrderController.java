package com.shidao.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.WmsPurchaseOrder;
import com.shidao.model.WmsPurchaseOrderDetail;
import com.shidao.model.WmsWarehouseVoucher.StatusType;
import com.shidao.service.WmsPurchaseOrderDetailService;
import com.shidao.service.WmsPurchaseOrderService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;
import com.shidao.util.StringUtil;

/**
 * @author liupengyuan
 *
 */
@RestController
@RequestMapping("/wmsPurchaseOrder")
public class WmsPurchaseOrderController extends BaseController{

	@Autowired
	private WmsPurchaseOrderService purchaseOrderService;
	
	@Autowired
	private WmsPurchaseOrderDetailService purchaseOrderDetailService;
	
	//手动添加采购单
	@RequestMapping(value="insert",method=RequestMethod.POST)
	public JSONObject insert(WmsPurchaseOrder purchaseOrder,HttpServletRequest request){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (purchaseOrder.getPurchaserId() == null) {
				purchaseOrder.setPurchaserId(sm.getEmployeeId());
			}
			purchaseOrder.setPurchaseCode(StringUtil.orderNumber());
			purchaseOrder.setStatus(StatusType.Created.name());
			purchaseOrder.setWarehouseId(sm.getWarehouseId());
			purchaseOrderService.insertSelective(purchaseOrder);
			return JsonUtil.succeedJson(purchaseOrder.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 自动采购
	 * 根据库存表和预警设置判断库存量是否足够
	 * 采购药品从进价最低的那一家供应商采购
	 */
	@RequestMapping(value="autoInsert",method=RequestMethod.POST)
	public JSONObject autoInsert(HttpServletRequest request){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			purchaseOrderService.autoInsert(sm.getEmployeeId(),sm.getWarehouseId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 请求审核
	 * @param purchaseOrder
	 * @return
	 */
	@RequestMapping(value="requestApproval",method=RequestMethod.POST)
	public JSONObject requestApproval(WmsPurchaseOrder purchaseOrder,HttpServletRequest request){
		try {
			purchaseOrder.setApprovedDate(new Date());
			purchaseOrder.setApproverId((Integer)request.getSession().getAttribute("employeeId"));
			purchaseOrder.setStatus(StatusType.RequestApproval.name());
			WmsPurchaseOrderDetail purchaseOrderDetail = new WmsPurchaseOrderDetail();
			purchaseOrderDetail.setPurchaseOrderId(purchaseOrder.getId());
			List<WmsPurchaseOrderDetail> list = purchaseOrderDetailService.list(purchaseOrderDetail);
			if (list == null || list.isEmpty()) {
				throw new ShidaoException("没有数据，无法提交！");
			}
			purchaseOrderService.updateByPrimaryKeySelective(purchaseOrder);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 更新
	 * @param purchaseOrder
	 * @return
	 */
	@RequestMapping(value="update",method=RequestMethod.POST)
	public JSONObject update(WmsPurchaseOrder purchaseOrder,HttpServletRequest request){
		try {
			if (purchaseOrder.getStatus() != null) {
				purchaseOrder.setApprovedDate(new Date());
				purchaseOrder.setApproverId((Integer)request.getSession().getAttribute("employeeId"));
			}
			purchaseOrderService.updateByPrimaryKeySelective(purchaseOrder);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 删除采购单和采购单详情
	 * @param purchaseOrder
	 * @return
	 */
	@RequestMapping(value="delete",method=RequestMethod.POST)
	public JSONObject delete(Integer id){
		try {
			purchaseOrderService.delete(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 所有的状态用map集合存储
	 */
	@RequestMapping(value="statusType",method=RequestMethod.GET)
	public JSONObject statusType(){
		try{
			Map<String, String> map = new HashMap<>();
			for (StatusType statusType : StatusType.values()) {
				map.put(statusType.getText(), statusType.name());
			}
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 通过状态和拒绝状态用map集合存储
	 */
	@RequestMapping(value="isApproval",method=RequestMethod.GET)
	public JSONObject isApproval(){
		try{
			Map<String, String> map = new HashMap<>();
			map.put(StatusType.Approved.name(), StatusType.Approved.getText());
			map.put(StatusType.Rejected.name(), StatusType.Rejected.getText());
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
}
