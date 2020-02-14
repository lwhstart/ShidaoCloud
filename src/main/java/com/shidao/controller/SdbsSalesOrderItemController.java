package com.shidao.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsProduct;
import com.shidao.enums.ClubType;
import com.shidao.enums.ProductCategory;
import com.shidao.model.SdbsSalesOrder;
import com.shidao.model.SdbsSalesOrderItem;
import com.shidao.service.SdbsProductService;
import com.shidao.service.SdbsSalesOrderItemService;
import com.shidao.service.SdbsSalesOrderService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;
import com.shidao.util.StringUtil;

@RestController
@RequestMapping(value = "/salesOrderItem")
public class SdbsSalesOrderItemController extends BaseController {

	@Autowired
	private SdbsSalesOrderItemService salesOrderItemService;

	@Autowired
	private SdbsSalesOrderService salesOrderService;

	@Autowired
	private SdbsProductService productService;

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject getCustomerPrescription(SdbsSalesOrderItem salesOrderItem, Boolean isHaveSalesOrderIds,
			String membershipUuid) {
		try {

			String salesOrderIds = null;
			if (isHaveSalesOrderIds != null && isHaveSalesOrderIds) {
				salesOrderIds = salesOrderService.getSalesOrderIds(salesOrderItem.getSalesOrderId());
			}
			return JsonUtil.succeedJson(salesOrderItemService.insert(salesOrderItem, salesOrderIds, membershipUuid));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/getSuggestPayDetail", method = RequestMethod.GET)
	public JSONObject getData(Integer salesOrderId, Integer membershipId, String salesOrderIds,
			HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			ClubType clubType = sm.getClubType();
			JSONObject jsonObject;
			if (StringUtil.isNullOrEmpty(salesOrderIds)) {
				BigDecimal total = salesOrderService.calculateAmount(salesOrderId, null);
				jsonObject = JsonUtil
						.succeedJson(salesOrderService.getSuggestPayDetail(salesOrderId, membershipId, total, -1));
			} else {
				jsonObject = JsonUtil.succeedJson(salesOrderItemService.getMainCustomerInfoAndPayMethod(salesOrderIds,
						sm.getClubId(), membershipId));
			}

			jsonObject.put("clubType", clubType);
			return jsonObject;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject updateSdbsSalesOrderItem(SdbsSalesOrderItem sdbsSalesOrderItem, Boolean isHaveSalesOrderIds) {
		try {
			String salesOrderIds = null;
			if (isHaveSalesOrderIds != null && isHaveSalesOrderIds) {
				SdbsSalesOrderItem salesOrderItem = salesOrderItemService
						.selectByPrimaryKey(sdbsSalesOrderItem.getId());
				salesOrderIds = salesOrderService.getSalesOrderIds(salesOrderItem.getSalesOrderId());
			}
			if (sdbsSalesOrderItem.getJfAmount() != null) {
				return JsonUtil.succeedJson(salesOrderItemService.updateJf(sdbsSalesOrderItem.getId(), sdbsSalesOrderItem.getJfAmount()));
			}
			return JsonUtil.succeedJson(salesOrderItemService.update(sdbsSalesOrderItem, salesOrderIds));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/updatePayMethod", method = RequestMethod.POST)
	public JSONObject updatePayMethod(SdbsSalesOrderItem sdbsSalesOrderItem, Boolean isHaveSalesOrderIds) {
		try {
			return JsonUtil.succeedJson(salesOrderItemService.updatePayMethod(sdbsSalesOrderItem, isHaveSalesOrderIds));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月2日 功能:更新药费的折扣
	 * @param sdbsSalesOrderItem
	 * @return
	 */
	@RequestMapping(value = "/updateMedicine", method = RequestMethod.POST)
	public JSONObject updateMedicine(SdbsSalesOrderItem sdbsSalesOrderItem) {
		try {
			if (sdbsSalesOrderItem.getJfAmount() != null) {
				return JsonUtil.succeedJson(salesOrderService.updateJf4Medicine(sdbsSalesOrderItem.getSalesOrderId(), sdbsSalesOrderItem.getJfAmount()));
			}
			return JsonUtil.succeedJson(salesOrderItemService.updateMedicine(sdbsSalesOrderItem));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject deleteSalesOrderItem(Integer id, Boolean isHaveSalesOrderIds) {
		try {
			return JsonUtil.succeedJson(salesOrderItemService.delete(id, isHaveSalesOrderIds));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/sumTotal/{salesOrderId}", method = RequestMethod.GET)
	public JSONObject getSumTotalBysalesOrderId(@PathVariable Integer salesOrderId) {
		try {
			return JsonUtil.succeedJson(salesOrderItemService.getSumTotalBysalesOrderId(salesOrderId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 添加服务
	 * 
	 * @param request
	 * @param appointmentId
	 * @param category
	 * @param quantity
	 * @return
	 */
	@RequestMapping(value = "/addFee", method = RequestMethod.POST)
	public JSONObject addFee(Integer appointmentId, ProductCategory category, BigDecimal quantity) {
		try {
			SdbsProduct condition = new SdbsProduct();
			condition.setCategory(category);
			SdbsProduct product = productService.list(condition).get(0);
			if (product == null) {
				throw new ShidaoException("没有该类型的产品：" + category.getText());
			}
			SdbsSalesOrder salesOrder = salesOrderService.selectByAppointmentId(appointmentId);
			if (salesOrder == null)
				throw new ShidaoException("没有该订单，请重新发送病历");
			SdbsSalesOrderItem salesOrderItem = new SdbsSalesOrderItem();
			salesOrderItem.setCategory(category);
			salesOrderItem.setSalesOrderId(salesOrder.getId());
			List<SdbsSalesOrderItem> list = salesOrderItemService.list(salesOrderItem);
			if (list != null && !list.isEmpty())
				throw new ShidaoException("不能重复加‘" + category.getText() + "’");
			SdbsSalesOrderItem item = new SdbsSalesOrderItem();
			item.setSalesOrderId(salesOrder.getId());
			item.setProductId(product.getId());
			item.setQuantity(quantity);
			return JsonUtil.succeedJson(salesOrderItemService.insert(item, null, null));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/customerConsumption", method = RequestMethod.GET)
	public JSONObject getCustomerConsumptionItem(@RequestParam(value = "customerId") Integer customerId,
			@RequestParam(value = "date") Date date, Integer doctorId, Integer employeeId) {
		try {
			return JsonUtil.succeedJson(
					salesOrderItemService.getCustomerConsumptionItem(customerId, date, doctorId, employeeId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月20日
	 * 功能:设置积分,并同时设置salesOrder里的积分
	 * @param salesOrder
	 * @return
	 */
	@RequestMapping(value = "/update/jf", method = RequestMethod.POST)
	public JSONObject updateJf4medicine(SdbsSalesOrderItem salesOrderItem,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			salesOrderItemService.updateJf(salesOrderItem.getId(),salesOrderItem.getJfAmount());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

}
