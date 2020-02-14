package com.shidao.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.PayMethod;
import com.shidao.enums.Position;
import com.shidao.model.SdbsCustomer;
import com.shidao.model.SdbsCustomerMembership;
import com.shidao.model.SdbsDelivery;
import com.shidao.model.SdbsDelivery.DeliveryStatus;
import com.shidao.model.SdbsSalesOrder;
import com.shidao.model.SdbsSalesOrder.SalesOrderType;
import com.shidao.model.TcmMedicalRecord;
import com.shidao.service.SdbsCustomerMembershipService;
import com.shidao.service.SdbsCustomerService;
import com.shidao.service.SdbsSalesOrderItemService;
import com.shidao.service.SdbsSalesOrderService;
import com.shidao.service.TcmMedicalRecordService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;
import com.shidao.vo.SettlementVO;
import com.shidao.websocket.SendSystemMessage;

@RestController
@RequestMapping(value = "/sdbsSalesOrder")
public class SdbsSalesOrderController extends BaseController {

	@Autowired
	private SdbsSalesOrderService salesOrderService;

	@Autowired
	private SdbsSalesOrderItemService salesOrderItemService;

	@Autowired
	private TcmMedicalRecordService medicalRecordService;

	@Autowired
	private SdbsCustomerService customerService;

	@Autowired
	private SdbsCustomerMembershipService customerMembershipService;

	/**
	 * @author 修改人:liupengyuan,时间:2018年3月5日 修改功能:如果没有传入clubId则默认为登录人的clubid
	 * @param salesOrder
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insertSalesOrder(SdbsSalesOrder salesOrder, HttpServletRequest request) {
		try {
			JSONObject map = JsonUtil.succeedJson();
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (salesOrder.getRelatedSalesOrderId() != null)
				salesOrder.setType(SalesOrderType.ProductAndLiliao.getText());
			if (salesOrder.getClubId() == null)
				salesOrder.setClubId(sm.getClubId());
			// if (salesOrder.getSalesEmployeeId() == null)
			// salesOrder.setSalesEmployeeId(sm.getEmployeeId());
			if (salesOrder.getCategory() != null)
				salesOrder.setType(salesOrder.getCategory().getText());
			salesOrderService.insertSelective(salesOrder);
			map.put("totalCount",
					salesOrderService.getSettlementsCount(salesOrder.getCustomerId(), new Date()));
			map.put("id", salesOrder.getId());
			map.putAll(salesOrderService.isSupportJFK(salesOrder.getCustomerId(), salesOrder.getCategory(), sm.getClubId()));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 结算服务，结算后订单表相应的更新payDate,amount,payAmount,payMetnod
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(SdbsSalesOrder salesOrder, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			salesOrder.setLastModified(sm.getEmployeeId());
			salesOrderService.updateByPrimaryKeySelective(salesOrder);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/delivery/{id}", method = RequestMethod.POST)
	public JSONObject updatedelivery(@PathVariable Integer id) {
		try {
			// 1. 查找是否有这个salesorder
			SdbsSalesOrder salesOrder = salesOrderService.selectByPrimaryKey(id);
			if (salesOrder == null) {
				// 2. 没有，throw 出shidaoexception
				throw new ShidaoException("没有购买记录:" + id);
			} else {

				SdbsSalesOrder order = new SdbsSalesOrder();
				order.setDeliveryDate(new Date());
				order.setId(id);
				salesOrderService.update(order);
				String body = "顾客【" + salesOrder.getCustomer().getName() + "】已配好药。";
				SendSystemMessage.send(Position.FinanceAssistant, salesOrder.getClub().getId(), body);
			}
			// 3 有的话，新建SalesOrder，
			// 设置id， 设置发货日期，设置 最后修改日期
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/dispensingstatus/{salesid}", method = RequestMethod.POST)
	public JSONObject dispensingstatus(@PathVariable Integer salesid) {
		try {
			SdbsSalesOrder salesOrder = salesOrderService.selectByPrimaryKey(salesid);
			if (salesOrder.getDeliveryDate() == null) {
				return JsonUtil.succeedJson("未配药");
			} else {
				return JsonUtil.succeedJson("已配药");
			}
			// 3 有的话，新建SalesOrder，
			// 设置id， 设置发货日期，设置 最后修改日期

		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/quantity/{customerId}/{productId}", method = RequestMethod.GET)
	public JSONObject getCustomerquantity(@PathVariable Integer customerId, @PathVariable Integer productId) {
		try {
			Float quantity = 0f;
			quantity = salesOrderService.getCustomerquantity(customerId, productId);
			if (quantity == null || quantity == 0) {
				quantity = salesOrderItemService.getMaxQuantity(productId);
			}
			return JsonUtil.succeedJson(quantity);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(SdbsSalesOrder condition, Integer pageNum, Integer pageSize) {
		try {
			return JsonUtil.succeedJson(salesOrderService.list(condition, pageNum, pageSize).getList());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public JSONObject detail(@PathVariable Integer id) {
		try {
			return JsonUtil.succeedJson(salesOrderService.selectByPrimaryKey(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/pay/{salesOrderId}", method = RequestMethod.POST)
	public JSONObject pay(@PathVariable Integer salesOrderId, PayMethod method) {
		try {
			SdbsSalesOrder newValue = new SdbsSalesOrder();
			newValue.setId(salesOrderId);
			newValue.setPayDate(new Date());
			newValue.setPayMethod(method);
			salesOrderService.update(newValue);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 修改状态
	 * 
	 * @param appointmentId
	 * @return
	 */
	@RequestMapping(value = "/updating/state", method = RequestMethod.POST)
	public JSONObject updatingState(@RequestParam(value = "appointmentId") Integer appointmentId) {
		try {
			SdbsSalesOrder condition = new SdbsSalesOrder();
			condition.setAppointmentId(appointmentId);

			List<SdbsSalesOrder> sdbsSalesOrders = salesOrderService.list(condition, 1, Integer.MAX_VALUE).getList();

			if (sdbsSalesOrders.get(0).getLocked() == 1)
				throw new ShidaoException("该处方已锁定不能再次发送药房，如需发送请联系药房解锁！");

			SdbsSalesOrder updateCondition = new SdbsSalesOrder();
			updateCondition.setId(sdbsSalesOrders.get(0).getId());
			salesOrderService.updateByPrimaryKeySelective(updateCondition);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 锁定状态
	 * 
	 * @param appointmentId
	 * @return
	 */
	@RequestMapping(value = "/locked/state", method = RequestMethod.POST)
	public JSONObject lockedState(@RequestParam(value = "salesOrderId") Integer salesOrderId, Integer locked) {
		try {

			SdbsSalesOrder sdbsSalesOrders = salesOrderService.selectByPrimaryKey(salesOrderId);

			if (locked == null)
				locked = (sdbsSalesOrders.getLocked() + 1) % 2;

			SdbsSalesOrder updateCondition = new SdbsSalesOrder();
			updateCondition.setLocked(locked);
			updateCondition.setId(salesOrderId);
			salesOrderService.updateByPrimaryKeySelective(updateCondition);
			return JsonUtil.succeedJson(locked);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 根据订单ID所用tcm_medical_record
	 * 
	 * @param salesOrderId
	 * @param locked
	 * @return
	 */
	@RequestMapping(value = "medicalRecord/locked/state", method = RequestMethod.POST)
	public JSONObject medicalRecordLockedState(@RequestParam(value = "salesOrderId") Integer salesOrderId,
			Integer locked) {
		try {

			SdbsSalesOrder sdbsSalesOrders = salesOrderService.selectByPrimaryKey(salesOrderId);
			TcmMedicalRecord medicalRecord = new TcmMedicalRecord();
			medicalRecord.setAppointmentId(sdbsSalesOrders.getAppointmentId());
			List<TcmMedicalRecord> list = medicalRecordService.list(medicalRecord);
			if (list == null || list.isEmpty())
				throw new ShidaoException("确认医生是否发过药！");
			TcmMedicalRecord updateCondition = new TcmMedicalRecord();
			updateCondition.setId(list.get(0).getId());
			updateCondition.setLocked(locked);
			medicalRecordService.updateByPrimaryKeySelective(updateCondition);
			return JsonUtil.succeedJson(locked);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/invalid", method = RequestMethod.POST)
	public JSONObject getInvalid(@RequestParam(value = "salesOrderId") Integer salesOrderId) {
		try {
			salesOrderService.getInvalid(salesOrderId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/cancelled", method = RequestMethod.POST)
	public JSONObject cancelled(@RequestParam(value = "salesOrderId") Integer salesOrderId, Integer cancelled) {
		try {
			SdbsSalesOrder salesOrder = new SdbsSalesOrder();
			salesOrder.setId(salesOrderId);
			if (cancelled == null) {
				salesOrder.setCancelled(1);
				salesOrder.setLocked(1);
			} else {
				salesOrder.setCancelled(cancelled);
			}
			salesOrderService.updateByPrimaryKeySelective(salesOrder);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id, HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			return JsonUtil.succeedJson(salesOrderService.delete(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月5日 功能:结算多个订单
	 * @param vo
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/doSettlement", method = RequestMethod.POST)
	public JSONObject doSettlement(SettlementVO vo, HttpServletRequest request) {
		try {
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession());			
			return JsonUtil.succeedJson(salesOrderService.doSettlement(vo, sManager.getEmployeeId(),sManager.getClubId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 点开始结算的时候， 把salesOrder,tcm_medical 的locked 设置为1， 把他作为结算的一个标志 取消结算则为0
	 * 
	 * @param request
	 * @param salesOrderId
	 * @return
	 */
	@RequestMapping(value = "settlementsLocked", method = RequestMethod.POST)
	public JSONObject settlementLocked(String customerUUID, Date date, Integer locked,String membershipUuid, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			salesOrderService.settlementLocked(customerUUID, date, locked, sm.getClubId(),membershipUuid);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月12日 功能:代付时调用改接口，实现更新折扣
	 * @param customerUUID
	 * @param date
	 * @param customerMembershipId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "daifuUpdateDiscount/{customerUUID}", method = RequestMethod.POST)
	public JSONObject daifuUpdateDiscount(@PathVariable("customerUUID") String customerUUID, Date date,
			Integer customerMembershipId, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			SdbsCustomer customer = customerService.selectByUuid(customerUUID);
			SdbsSalesOrder condition = new SdbsSalesOrder();
			condition.setCustomerId(customer.getId());
			condition.setCreatedDate(date);
			condition.setClubId(sm.getClubId());
			condition.setPayed(false);
			String salesOrderIds = salesOrderService.getSalesOrderIds(condition);
			salesOrderItemService.updateDiscountAndTotalByTCM(customerMembershipId, salesOrderIds);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/refund", method = RequestMethod.POST)
	public JSONObject refund(HttpServletRequest request, Integer salesOrderId) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			salesOrderService.refund(salesOrderId, sm.getEmployeeId(), sm.getClubTelephone());
			SdbsSalesOrder key = salesOrderService.selectByPrimaryKey(salesOrderId);
			return JsonUtil.succeedJson(salesOrderService.getThreeDataOfSettlement(key.getCustomerId(), sm.getClubId(),
					key.getCreatedDate()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	// 选择需要快递复选框
	@RequestMapping(value = "/needDelivery", method = RequestMethod.POST)
	public JSONObject needDelivery(Boolean needed, Integer salesOrderId) {
		try {
			return JsonUtil.succeedJson(salesOrderService.needDelivery(needed, salesOrderId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	// 填写地址，点确定
	@RequestMapping(value = "/setDeliveryAddress", method = RequestMethod.POST)
	public JSONObject setDeliveryAddress(SdbsDelivery delivery, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			delivery.setCreator(sm.getEmployeeId());
			delivery.setStatus(DeliveryStatus.DeliveryRequested);
			salesOrderService.updateDelivery(delivery);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	// 填写订单信息，点确定
	@RequestMapping(value = "/setDeliveryInfo", method = RequestMethod.POST)
	public JSONObject setDeliveryInfo(SdbsDelivery delivery, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			delivery.setDeliveryId(sm.getEmployeeId());
			delivery.setStatus(DeliveryStatus.Delivered);
			delivery.setDeliveryDate(new Date());
			salesOrderService.updateDelivery(delivery);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	// 修改快递单
	@RequestMapping(value = "/update/delivery", method = RequestMethod.POST)
	public JSONObject updateDelivery(SdbsDelivery delivery, HttpServletRequest request) {
		try {
			if (delivery.getDeliveryInfo() != null) {
				EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
				delivery.setDeliveryId(sm.getEmployeeId());
			}
			salesOrderService.updateDelivery(delivery);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月19日 功能:项目卡余额和积分卡余额互相换
	 * @param XMKId
	 * @param XMKAmount
	 * @param JFKAmount
	 * @param total
	 * @param liliaoTotal
	 * @return
	 */
	@RequestMapping(value = "/change/xmk", method = RequestMethod.POST)
	public JSONObject change(Integer XMKId, Integer XMKAmount, Integer JFKAmount, Integer total, Integer liliaoTotal) {
		try {
			JSONObject map = JsonUtil.succeedJson();
			if (total == null)
				throw new ShidaoException("发生错误");
			if (JFKAmount == null)
				JFKAmount = 0;
			SdbsCustomerMembership key = customerMembershipService.selectByPrimaryKey(XMKId);
			BigDecimal payLimit = key.getPayLimit();
			Integer unitLeft = key.getItems().get(0).getUnitLeft();
			if (XMKAmount != null && (XMKAmount > total || XMKAmount + JFKAmount > total))
				throw new ShidaoException("所付超出总金额");
			if (JFKAmount >= 0) {
				if (new BigDecimal(JFKAmount).compareTo(new BigDecimal(liliaoTotal).multiply(payLimit)) ==1)
					throw new ShidaoException("积分超出所付上限");
				if (JFKAmount > unitLeft)
					throw new ShidaoException("积分余额不足");
				if (XMKAmount == null) {
					map.put("XMKAmount", total - JFKAmount);
					map.put("JFKAmount", JFKAmount);
				}
			}
			if (XMKAmount != null && liliaoTotal != null) {
				if (total - XMKAmount > unitLeft || new BigDecimal(total - XMKAmount).compareTo(new BigDecimal(liliaoTotal).multiply(payLimit)) == 1)
					throw new ShidaoException("积分不能超出上限或余额");
				map.put("XMKAmount", XMKAmount);
				map.put("JFKAmount", total - XMKAmount);
			}
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月20日
	 * 功能:设置药和诊费的积分
	 * @param salesOrder
	 * @return
	 */
	@RequestMapping(value = "/update/jf4Medicine", method = RequestMethod.POST)
	public JSONObject updateJf4medicine(SdbsSalesOrder salesOrder,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			salesOrderService.updateJf4Medicine(salesOrder.getId(),salesOrder.getJfAmount());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 正式支付延期支付的订单。
	 * @param request
	 * @param id
	 * @param payMethod
	 * @return
	 * @author yzl , Created at 2019年8月27日
	 *
	 */
	@PostMapping(value = "/pay4Postpone")
	public JSONObject pay4Postpone(HttpServletRequest request, Integer id, PayMethod payMethod) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			salesOrderService.pay4Postpone(id, payMethod);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
