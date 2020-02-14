package com.shidao.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.Position;
import com.shidao.model.WmsDeliveryVoucher;
import com.shidao.enums.DeliveryVoucherCategory;
import com.shidao.model.WmsDeliveryVoucher.OpeationStatus;
import com.shidao.model.WmsDeliveryVoucher.StatusType;
import com.shidao.model.WmsDeliveryVoucherDetail;
import com.shidao.service.WmsDeliveryVoucherDetailService;
import com.shidao.service.WmsDeliveryVoucherService;
import com.shidao.service.WmsInventoryService;
import com.shidao.service.WmsRelationClubWarehouseService;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.StringUtil;
import com.shidao.websocket.SendSystemMessage;

@RestController
@RequestMapping(value = "/deliveryVoucher")
public class WmsDeliveryVoucherController extends BaseController {

	@Autowired
	private WmsDeliveryVoucherService deliveryVoucherService;

	@Autowired
	private WmsDeliveryVoucherDetailService deliveryVoucherDetailService;

	@Autowired
	private WmsRelationClubWarehouseService relationClubWarehouseService;
	
	@Autowired
	private WmsInventoryService inventoryService;

	@RequestMapping(value = "/dispensing/{deliveryVoucherId}")
	public JSONObject getDispensing(@PathVariable Integer deliveryVoucherId,String customerName ,HttpServletRequest request) {
		try {
			EmployeeSessionManager esm = new EmployeeSessionManager(request.getSession());
			deliveryVoucherService.dispendingVoucher(deliveryVoucherId, esm.getEmployeeId() );
			Integer clubId = deliveryVoucherService.getClubIdById(deliveryVoucherId);
			if (clubId == null)
				clubId = esm.getClubId();
			if (!StringUtil.isNullOrEmpty(customerName)) {
				SendSystemMessage.send(Position.FinanceAssistant, clubId, String.format("客户【%s】已在药房配好药！", customerName));
			}
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(WmsDeliveryVoucher deliveryVoucher, HttpServletRequest request) {
		try {
			// 该门店仓库
			Integer warehouseId = relationClubWarehouseService
					.getWarehouseIdByClubId((Integer) request.getSession().getAttribute("clubId"));
			deliveryVoucher.setBatchNumber(StringUtil.orderNumber());
			deliveryVoucher.setCreatorId((Integer) request.getSession().getAttribute("employeeId"));
			deliveryVoucher.setStatus(StatusType.Created);
			deliveryVoucher.setCategory(DeliveryVoucherCategory.ManuallyCreated);
			deliveryVoucher.setWarehouseId(warehouseId);
			deliveryVoucherService.insertSelective(deliveryVoucher);
			return JsonUtil.succeedJson(deliveryVoucher.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 请求审核
	 * 
	 * @param deliveryVoucher
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(WmsDeliveryVoucher deliveryVoucher) {
		try {
			if (deliveryVoucher.getStatus().equals(StatusType.RequestApproval)) {
				WmsDeliveryVoucherDetail wmsDeliveryVoucherDetail = new WmsDeliveryVoucherDetail();
				wmsDeliveryVoucherDetail.setDeliveryVoucherId(deliveryVoucher.getId());
				List<WmsDeliveryVoucherDetail> list = deliveryVoucherDetailService.list(wmsDeliveryVoucherDetail);
				if (list != null && !list.isEmpty()) {
					wmsDeliveryVoucherDetail.setIsOverInventoryAmount("1");
					List<WmsDeliveryVoucherDetail> list2 = deliveryVoucherDetailService.list(wmsDeliveryVoucherDetail);
					if (list2 != null && !list2.isEmpty()) {
						throw new ShidaoException("你可能不知道--" + list2.get(0).getMedicine().getName() + "--超出库存量了！");
					}
				} else {
					throw new ShidaoException("没有记录，不能提交！");
				}
			}
			deliveryVoucherService.updateByPrimaryKeySelective(deliveryVoucher);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id) {
		try {
			deliveryVoucherService.delete(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 审核：通过，则减去库存里的数据
	 * 
	 * @param deliveryVoucher
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/approve", method = RequestMethod.POST)
	public JSONObject approve(Integer id , HttpServletRequest request) {
		try {
			EmployeeSessionManager sManager= new EmployeeSessionManager(request.getSession());
			Integer warehouseId = sManager.getWarehouseId();
			
			WmsDeliveryVoucherDetail condition = new WmsDeliveryVoucherDetail();
			condition.setDeliveryVoucherId(id);
			List<WmsDeliveryVoucherDetail> deliveryVoucherDetails = deliveryVoucherDetailService.list(condition);
			
			if (deliveryVoucherDetails == null || deliveryVoucherDetails.isEmpty()) {
				throw new ShidaoException("出库详情为空，出库失败！");
			}
			
			for (WmsDeliveryVoucherDetail wmsDeliveryVoucherDetail : deliveryVoucherDetails) {
				BigDecimal price = wmsDeliveryVoucherDetail.getPrice();
				BigDecimal jqPrice = BigDecimal.valueOf(inventoryService.getJqPrice(wmsDeliveryVoucherDetail.getProductId(), warehouseId)).setScale(6, RoundingMode.HALF_UP);
				
				if (price.compareTo(jqPrice) != 0) {
					return JsonUtil.succeedJson(false);
				}
			}
			
			deliveryVoucherService.approve(id, sManager.getEmployeeId());
			return JsonUtil.succeedJson(true);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 审核：通过，则减去库存里的数据
	 * 
	 * @param deliveryVoucher
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list/prescription", method = RequestMethod.GET)
	public JSONObject getVoucherForPrescritionList(WmsDeliveryVoucher deliveryVoucher, Integer pageNum,
			Integer pageSize) {
		try {

			return JsonUtil.succeedJson(
					deliveryVoucherService.getVoucherForPrescritionList(deliveryVoucher, pageNum, pageSize));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 审核：通过，则减去库存里的数据
	 * 
	 * @param deliveryVoucher
	 * @param request
	 * @return
	 */
	/*@RequestMapping(value = "/{id}/prescription", method = RequestMethod.GET)
	public JSONObject getPrescriptionForDelivery(@PathVariable(value="id") Integer id) {
		try {

			return JsonUtil.succeedJson(deliveryVoucherService.getPrescriptionDetails(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}*/
	
	/**
	 * 开始配药
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/start/dispensing", method = RequestMethod.POST)
	public JSONObject startDispensing(Integer id) {
		try {
			deliveryVoucherService.startDispensing(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping(value = "/cancell/dispensing", method = RequestMethod.POST)
	public JSONObject cancellDispensing(Integer id) {
		try {
			WmsDeliveryVoucher deliveryVoucher = new WmsDeliveryVoucher();
			deliveryVoucher.setId(id);
			deliveryVoucher.setOpeationStatus(OpeationStatus.NotStarted);
			deliveryVoucherService.updateByPrimaryKeySelective(deliveryVoucher);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 重新计算价格
	 * @param deliveryVoucherId
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/recalculation/{deliveryVoucherId}",method = RequestMethod.POST)
	public JSONObject recalculation(@PathVariable(value="deliveryVoucherId") Integer deliveryVoucherId, HttpServletRequest request) {
		try {			
			EmployeeSessionManager eManager = new EmployeeSessionManager(request.getSession());
			Integer warehouseId = eManager.getWarehouseId();
			
			deliveryVoucherDetailService.recalculation(deliveryVoucherId,warehouseId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
}
