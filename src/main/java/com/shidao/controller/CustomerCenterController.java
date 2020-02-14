package com.shidao.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.MedicalRecordStatus;
import com.shidao.enums.MessageType;
import com.shidao.model.SdbsCustomerMembership;
import com.shidao.model.TcmMedicalRecord;
import com.shidao.service.MzMaizhenyiHistoryService;
import com.shidao.service.SdbsCustomerMembershipService;
import com.shidao.service.SdbsCustomerService;
import com.shidao.service.SdbsPayOrderService;
import com.shidao.service.SdbsProfessioService;
import com.shidao.service.SdepConsultationService;
import com.shidao.service.TcmMedicalRecordService;
import com.shidao.service.WsMessageService;
import com.shidao.util.CustomerSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping("/customer/userCenter")
public class CustomerCenterController extends BaseController {

	@Autowired
	private MzMaizhenyiHistoryService mzyHistoryService;
	
	@Autowired
	private SdbsCustomerService customerService;
	
	@Autowired
	private SdbsCustomerMembershipService customerMembershipService;
	
	@Autowired
	private TcmMedicalRecordService medicalRecordService;
	
	@Autowired
	private SdbsPayOrderService payOrderService;
	
	@Autowired
	private SdbsProfessioService professioService;
	
	@Autowired
	private WsMessageService wsMessageService;
	
	@Autowired
	private SdepConsultationService consultationService;
	/**
	 * 我的未病分析报告列表
	 * @author yzl 2018年9月18日
	 * @param request
	 * @param count 返回的数量，默认是10
	 * @return
	 */
	@GetMapping("/mzy/list")
	public JSONObject getMyMzyList(HttpServletRequest request, Integer count) {
		try {
			CustomerSessionManager csManager = new CustomerSessionManager(request.getSession());
			int listCount = (count==null || count == 0)?  10 : count;
			return JsonUtil.succeedJson(mzyHistoryService.getByCustomerId(csManager.getCustomerId(),listCount));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月19日
	 * 功能:病历列表
	 * @param request
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/medicalRecord/list")
	public JSONObject medicalRecordList(HttpServletRequest request, Integer pageNum,Integer pageSize) {
		try {
			CustomerSessionManager csManager = new CustomerSessionManager(request.getSession());
			TcmMedicalRecord medicalRecord = new TcmMedicalRecord();
			medicalRecord.setCustomerId(csManager.getCustomerId());
			medicalRecord.setStatus(MedicalRecordStatus.Completed);
			medicalRecord.addParameter("orderBy", "a.CREATED_DATE DESC");
			return JsonUtil.succeedJson(medicalRecordService.list(medicalRecord, pageNum, pageSize));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月19日
	 * 功能:消费历史列表
	 * @param request
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/payOrder/list")
	public JSONObject payOrderList(HttpServletRequest request,@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		try {
			CustomerSessionManager csManager = new CustomerSessionManager(request.getSession());
			Integer custId = csManager.getCustomerId();
			Map<String, Object> map = new HashMap<>();
			if (date == null)
				date = payOrderService.getMaxPayDateByCustomerId(custId);
			if(date == null) {
				return JsonUtil.succeedJson(map);
			}
			Map<String, LocalDate> sidePayDate = payOrderService.getSidePayDateByCustomerId(custId, date);
			if(sidePayDate != null && sidePayDate.size()>0)
				map.putAll(sidePayDate);
			map.put("date", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			map.put("payOrderHistory", payOrderService.getPayOrderHistory(csManager.getCustomerId(), date));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月19日
	 * 功能:会员卡，套餐卡列表
	 * @param request
	 * @return
	 */
	@GetMapping("/membership/list")
	public JSONObject membershipList(HttpServletRequest request) {
		try {
			CustomerSessionManager csManager = new CustomerSessionManager(request.getSession());
			SdbsCustomerMembership membership = new SdbsCustomerMembership();
			membership.setCustomerId(csManager.getCustomerId());
			membership.setEnabled(1);
			return JsonUtil.succeedJson(customerMembershipService.listDetailByCondition(membership));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月19日
	 * 功能:个人资料
	 * @param request
	 * @return
	 */
	@GetMapping("/userInfo")
	public JSONObject customerDetail(HttpServletRequest request) {
		try {
			CustomerSessionManager csManager = new CustomerSessionManager(request.getSession());
			Map<String, Object> map = new HashMap<>();
			map.put("customerInfo", customerService.selectByPrimaryKey(csManager.getCustomerId()));
			map.put("profession", professioService.list());
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月26日
	 * 功能:获取服务组
	 * @param request
	 * @return
	 */
	@GetMapping("/service/groups")
	public JSONObject getServiceGroups(HttpServletRequest request) {
		try {
			CustomerSessionManager csManager = new CustomerSessionManager(request.getSession());
			return JsonUtil.succeedJson(customerService.getCustomerServiceGroup(csManager.getCustomerId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 或得某用户，指定类型消息的未读数量
	 * @author yzl 2018年9月29日
	 * @param websocketId 用户聊天编号
	 * @param type 聊天类型
	 * @return
	 */
	@RequestMapping(value = "/message/unreadCount/{type}")
	public JSONObject getUnreadMessaeCountOfType(HttpServletRequest request,  @PathVariable(value = "type") MessageType type) {
		try {
			CustomerSessionManager csManager = new CustomerSessionManager(request.getSession());
			
			return JsonUtil
					.succeedJson(wsMessageService.getUnreadMessageCountOfType(csManager.getWebsocketId(), type));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}


	/**
     * 获得未读咨询的数量
     * @author yzl 2018年9月29日
     * @return 未读咨询的数量
     */
	@RequestMapping(value = "/consultation/unreadCount")
	public JSONObject getUnreadConsultantCount(HttpServletRequest request) {
		try {
			CustomerSessionManager csManager = new CustomerSessionManager(request.getSession());
			
			return JsonUtil
					.succeedJson(consultationService.getCustomerUnreadCount(csManager.getCustomerId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
