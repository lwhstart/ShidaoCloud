package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.ContactorStatus;
import com.shidao.enums.SdepContactorRequestStatus;
import com.shidao.model.SdbsCustomer;
import com.shidao.model.SdcomEmployee;
import com.shidao.model.SdepContactorRequest;
import com.shidao.service.SdbsCustomerService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.service.SdepContactorRequestService;
import com.shidao.service.SdepContactorService;
import com.shidao.service.TcmMedicalRecordService;
import com.shidao.util.CustomerSessionManager;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;
import com.shidao.websocket.MyWebSocket;

@RestController
@RequestMapping(value = "/contactor")
public class ContactorController extends BaseController{

	@Autowired
	private SdepContactorRequestService contactorRequestService;
	
	@Autowired
	private SdepContactorService contactorService;
	
	@Autowired
	private SdcomEmployeeService employeeService;
	
	@Autowired
	private SdbsCustomerService customerService;
	
	@Autowired
	private TcmMedicalRecordService medicalRecordService;
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月18日
	 * 功能:客户想医生申请好友
	 * @param request
	 * @param contactorRequest
	 * @param contactor
	 * @return
	 */
	@RequestMapping(value = "/request/send",method=RequestMethod.POST)
	public JSONObject request(HttpServletRequest request,String contactorId,String nickName,String description,String requestMessage) {
		try {
			JSONObject map = JsonUtil.succeedJson();
			CustomerSessionManager cm = new CustomerSessionManager(request.getSession());
			Integer requestId = contactorRequestService.requestSend(cm.getWebsocketId(),contactorId,nickName,description,requestMessage);
			map.put("contactorId", contactorId);
			map.put("description", description);
			map.put("requestMessage", requestMessage);
			map.put("requestId", requestId);
			SdepContactorRequest key = contactorRequestService.selectByPrimaryKey(requestId);
			map.put("nickName", key.getNickName());
			map.put("requestDatetime", key.getRequestDatetime());
			map.put("responseDatetime", key.getResponseDatetime());
			MyWebSocket.sendRequest(requestId,contactorId);
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月18日
	 * 功能:是否可以直接通讯
	 * @param request
	 * @param contactorId
	 * @return
	 */
	@RequestMapping(value = "/canCommunicate",method=RequestMethod.GET)
	public JSONObject canCommunicate(HttpServletRequest request,String contactorId) {
		try {
			JSONObject map = JsonUtil.succeedJson();
			CustomerSessionManager cm = new CustomerSessionManager(request.getSession(),false);
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession(), false);
			if (cm.isLoggedIn())
				map.putAll(contactorService.canCommunicate(cm.getWebsocketId(), contactorId)); 
				map.put("contactorStatus", ContactorStatus.Contactor);
			if (sm.isLoggedIn()) {
				contactorService.insertContactor(sm.getWebsocketId(), contactorId);
				map.put("contactorStatus", ContactorStatus.Contactor);
			}
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月18日
	 * 功能:申请被拒绝，联系人表里的数据也被删除
	 * @param requestId
	 * @return
	 */
	@RequestMapping(value = "/request/reject",method=RequestMethod.POST)
	public JSONObject reject(Integer requestId,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			contactorRequestService.reject(requestId);
			SdepContactorRequest key = contactorRequestService.selectByPrimaryKey(requestId);
			MyWebSocket.sendRequest(requestId, key.getUserId());
			return JsonUtil.succeedJson(key);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月18日
	 * 功能:申请接受
	 * @param requestId
	 * @return
	 */
	@RequestMapping(value = "/request/accept",method=RequestMethod.POST)
	public JSONObject accept(Integer requestId,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			contactorRequestService.accept(requestId);
			SdepContactorRequest key = contactorRequestService.selectByPrimaryKey(requestId);
			MyWebSocket.sendRequest(requestId, key.getUserId());
			return JsonUtil.succeedJson(key);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月18日
	 * 功能:员工查询克聊天的人
	 * @param request
	 * @param name
	 * @param category{
	 * currentClubEmployee:当前门店的员工
	 * allEmployee：所有员工
	 * myCustomer：我的客户
	 * allCustomer:所有客户} 
	 * @return
	 */
	@RequestMapping(value = "/employee/search",method=RequestMethod.GET)
	public JSONObject employeeSearch(HttpServletRequest request,String name,String category,Integer pageNum,Integer pageSize) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			SdcomEmployee employee = new SdcomEmployee();
			employee.setName(name);
			SdbsCustomer customer = new SdbsCustomer();
			customer.setName(name);
			if (category.equals("currentClubEmployee")){
				employee.setClubId(sm.getClubId());
				return JsonUtil.succeedJson(employeeService.list(employee, pageNum, pageSize));
			}
			if (category.equals("allEmployee"))
				return JsonUtil.succeedJson(employeeService.list(employee, pageNum, pageSize));
			if (category.equals("allCustomer"))
				return JsonUtil.succeedJson(customerService.list(customer, pageNum, pageSize));
			if (category.equals("myCustomer"))
				return JsonUtil.succeedJson(customerService.listMyCustomerByEmployeeId(sm.getEmployeeId(), name, pageNum, pageSize));
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月19日
	 * 功能:申请列表里的单个信息
	 * @param request
	 * @param userId
	 * @param contactorId
	 * @return
	 */
	@RequestMapping(value = "/request/message",method=RequestMethod.GET)
	public JSONObject requestMessage(HttpServletRequest request,String userId,String contactorId) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (contactorId == null)
				contactorId = sm.getWebsocketId();
			SdepContactorRequest condition = new SdepContactorRequest();
			condition.setUserId(userId);
			condition.setContactorId(contactorId);
			return JsonUtil.succeedJson(contactorRequestService.list(condition));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月20日
	 * 功能:根据顾客的uid得到其customerUuid和
	 * @param request
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/getCustomerInfo",method=RequestMethod.GET)
	public JSONObject getCustomerMedicalOrDetail(HttpServletRequest request,String userId) {
		try {
			JSONObject map= JsonUtil.succeedJson();
			if (!userId.contains("C"))
				throw new ShidaoException("不能查看");
			Integer customerId = Integer.parseInt(userId.substring(1, userId.length()));
			map.put("customerUuid", customerService.selectByPrimaryKey(customerId).getUuid());
			map.put("medicalUuid", medicalRecordService.getLastUuidOfCustomer(customerId,null));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月23日
	 * 功能:得到客户的所有申请信息
	 * @param request
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/getRequestMessage",method=RequestMethod.GET)
	public JSONObject getRequestMessage(HttpServletRequest request,Integer pageNum,Integer pageSize) {
		try {
			SdepContactorRequest condition = new SdepContactorRequest();
			Object employeeId = request.getSession().getAttribute("employeeId");
			Object customerId = request.getSession().getAttribute("customerId");
			if (employeeId ==null && customerId == null)
				throw new ShidaoException("先登录");
			if (employeeId != null)
				condition.setUserId("E"+employeeId);
			if (customerId != null)
				condition.setUserId("C"+customerId);
			return JsonUtil.succeedJson(contactorRequestService.getRequestMessage(condition, pageNum, pageSize));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月24日
	 * 功能:客户查看自己的申请信息
	 * @param request
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/getCustomerRequestMessage",method=RequestMethod.GET)
	public JSONObject getCustomerRequestMessage(HttpServletRequest request) {
		try {
			SdepContactorRequest condition = new SdepContactorRequest();
			CustomerSessionManager cm = new CustomerSessionManager(request.getSession());
			condition.setUserId(cm.getWebsocketId());
			condition.setStatuses(SdepContactorRequestStatus.noAccept);
			return JsonUtil.succeedJson(contactorRequestService.list(condition));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
