package com.shidao.controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.businessHandler.HandlerFactory;
import com.shidao.enums.AppointmentCategory;
import com.shidao.enums.ModifierRole;
import com.shidao.model.SdbsAppointment;
import com.shidao.model.SdbsCustomer;
import com.shidao.model.SdbsCustomerInfo;
import com.shidao.model.SdbsCustomerMembership;
import com.shidao.model.SdcomRelationDoctorClub;
import com.shidao.model.SdepModifyHistory;
import com.shidao.service.SdbsAppointmentService;
import com.shidao.service.SdbsCustomerInfoServices;
import com.shidao.service.SdbsCustomerService;
import com.shidao.service.SdcomRelationDoctorClubService;
import com.shidao.util.CustomerSessionManager;
import com.shidao.util.DateUtil;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.HttpUtil;
import com.shidao.util.JsonUtil;
import com.shidao.util.ListResult;
import com.shidao.util.ShidaoException;
import com.shidao.util.StringUtil;
import com.shidao.util.ValidList.ValidListCategory;
import com.shidao.vo.XMKCustomerVO;

@RestController
@RequestMapping(value = "/sdbsCustomer")
public class SdbsCustomerController extends BaseController {

	@Autowired
	private SdbsCustomerService sdbsCustomerService;

	@Autowired
	private SdcomRelationDoctorClubService relationDoctorClubService;

	@Autowired
	private SdbsAppointmentService appointmentService;
	
	@Autowired
	private SdbsCustomerInfoServices customerInfoService;

	/**
	 * 判断手机是否已经被注册
	 * 
	 * @param mobile
	 * @return 存在true 不存在false
	 */
	@RequestMapping(value = "/isRegisted/{mobile}", method = RequestMethod.POST)
	public JSONObject isMobileRegisted(@PathVariable(value = "mobile") String mobile) {
		try {

			return JsonUtil.succeedJson(sdbsCustomerService.isMobileRegisted(mobile));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 根据手机获取客户信息
	 * 
	 * @param 客户手机号
	 * @return 客户详细信息
	 */
	@RequestMapping(value = "/mobile/{mobile}", method = RequestMethod.GET)
	public JSONObject getByMobile(@PathVariable(value = "mobile") String mobile) {
		try {
			return JsonUtil.succeedJson(sdbsCustomerService.getByMobile(mobile));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}

	}

	/**
	 * 
	 * @param sdbsCustomer（mobile：手机号码，password:密码）
	 * @param code
	 *            验证码
	 * @param verifyPassword
	 *            再次输入密码
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public JSONObject register(SdbsCustomer sdbsCustomer, String code,
			String verifyPassword) {
		try {
			Integer clubId = 1;
			// 创建员工
			sdbsCustomerService.register(sdbsCustomer, code, verifyPassword, clubId);
			return JsonUtil.succeedJson(sdbsCustomer.getId());
		} catch (ShidaoException e) {
			return JsonUtil.errjson(e);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 
	 * @param mobile
	 * @param password
	 * @param request(传入session)
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public JSONObject login(@RequestParam(value = "mobile") String mobile,
			@RequestParam(value = "password") String password, HttpServletRequest request) {
		try {
			HttpUtil.clearSession(request);
			SdbsCustomer currentCustomer = sdbsCustomerService.login(mobile, password);
			request.getSession().setAttribute("customerId", currentCustomer.getId());
			request.getSession().setAttribute("uid", currentCustomer.getWebsocketId());
			request.getSession().setAttribute("customerName", currentCustomer.getName());
			request.getSession().setAttribute("customerLevel", currentCustomer.getMemberType());
			JSONObject result = JsonUtil.succeedJson(currentCustomer.getId());
			result.put("customer", currentCustomer);
			return result;
		} catch (ShidaoException e) {
			return JsonUtil.errjson(e);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 
	 * @param mobile
	 * @param password
	 * @param verifyPassword
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
	public JSONObject forgetPassword(@RequestParam(value = "mobile") String mobile,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "verifyPassword") String verifyPassword, @RequestParam(value = "code") String code) {
		try {
			sdbsCustomerService.forgetPassword(mobile, password, verifyPassword, code);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(HttpServletRequest request, SdbsCustomer condition, Integer clubId, Integer pageNum,
			Integer pageSize) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			condition.addClubIdParam(esManager.isLoggedIn() ? esManager.getClubId() : clubId);
			ListResult<SdbsCustomer> list = sdbsCustomerService.list(condition, pageNum, pageSize);
			return JsonUtil.succeedJson(list);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 查看用户个人信息
	 * 
	 * @param customerId
	 * @return
	 */
//	@RequestMapping(value = "/{customerId}", method = RequestMethod.GET)
//	public JSONObject detail1(@PathVariable Integer customerId) {
//		try {
//			return JsonUtil.succeedJson(sdbsCustomerService.selectByPrimaryKey(customerId));
//		} catch (Exception e) {
//			return JsonUtil.errjson(e);
//		}
//	}

	@RequestMapping(value = "/detail/{uuid}", method = RequestMethod.GET)
	public JSONObject detail(@PathVariable String uuid) {
		try {
			return JsonUtil.succeedJson(sdbsCustomerService.selectByUuid(uuid));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@PostMapping(value = "/update")
	public JSONObject update(SdbsCustomer sdbsCustomer,HttpServletRequest request) {
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			SdepModifyHistory modifyHistory = new SdepModifyHistory();
			modifyHistory.setModifierId(employeeSessionManager.getEmployeeId());
			modifyHistory.setModifierRole(ModifierRole.Employee);
			sdbsCustomerService.updateByPrimaryKeySelective(sdbsCustomer,modifyHistory);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月26日
	 * 功能:顾客自己修改个人信息
	 * @param sdbsCustomer
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/updateMyInfo")
	public JSONObject updateMyInfo(SdbsCustomer sdbsCustomer,HttpServletRequest request) {
		try {
			CustomerSessionManager cs = new CustomerSessionManager(request.getSession());
			sdbsCustomer.setId(cs.getCustomerId());
			SdepModifyHistory modifyHistory = new SdepModifyHistory();
			modifyHistory.setModifierId(cs.getCustomerId());
			modifyHistory.setModifierRole(ModifierRole.Customer);
			sdbsCustomerService.updateByPrimaryKeySelective(sdbsCustomer,modifyHistory);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 根据用户手机和验证码获取用户id
	 * 
	 * @param code
	 * @param mobile
	 * @return
	 */
	@RequestMapping(value = "/customerId/{code}/{mobile}")
	public JSONObject getIdByCodeAndMobile(@PathVariable String code, @PathVariable String mobile) {
		try {
			sdbsCustomerService.verifyCaptcha(code, mobile);
			SdbsCustomer condition = new SdbsCustomer();
			condition.setMobile(mobile);
			ListResult<SdbsCustomer> customer = sdbsCustomerService.list(condition, 1, 1);
			return JsonUtil.succeedJson(customer.getList().get(0).getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(SdbsCustomer customer, HttpServletRequest request) {
		try {
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession());
			customer.setCreator(sManager.getEmployeeId());
			sdbsCustomerService.insertSelective(customer, sManager.getCurrent().getName(), sManager.getClubId());
			JSONObject result = JsonUtil.succeedJson(customer.getId());
			result.put("uuid", customer.getUuid());
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/insert/familyMember", method = RequestMethod.POST)
	public JSONObject insertFamilyMember(Integer hostId, Integer familyMemberId, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			SdbsCustomer customer = sdbsCustomerService.selectByPrimaryKey(hostId,sm.getClubId());
			Integer personNum = null;
			List<SdbsCustomerMembership> memberships = customer.getMemberships();
			if (memberships!= null &&memberships.size()>0 ){
				personNum = memberships.stream().anyMatch(m->m.getNumberOfUsers()==-1) ?
						-1 :
							memberships.stream().mapToInt(SdbsCustomerMembership::getNumberOfUsers).sum() ;	
			}
			if (memberships == null || memberships.size()<=0)
				throw new ShidaoException("没有权限添加家属成员!");
			if (personNum == null || personNum == 1)
				throw new ShidaoException("该卡暂不支持添加家属成员!");
			XMKCustomerVO vo = sdbsCustomerService.listXMKById(familyMemberId, sm.getClubId());
			if (vo != null)
				throw new ShidaoException("该用户拥有项目卡不可成为家属成员");
			sdbsCustomerService.insertFamilyMember(hostId, familyMemberId, personNum);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "list/daifus", method = RequestMethod.GET)
	public JSONObject customerDaifus(String name, HttpServletRequest request) {
		try {
			if (StringUtil.isNullOrEmpty(name)) {
				throw new ShidaoException("请提供代付人姓名");
			}
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			SdbsCustomer condition = new SdbsCustomer();
			condition.setName(name);
			condition.addClubIdParam(sm.getClubId());
			condition.addNumberOfUsersParam(-1);
			condition.addTypeParam("membership");
			return JsonUtil.succeedJson(sdbsCustomerService.list(condition));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/mergeCustomer", method = RequestMethod.POST)
	public JSONObject mergeCustomer(HttpServletRequest request,
			@RequestParam(value = "newUuid") String newUuid,
			@RequestParam(value = "oldUuid") String oldUuid) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			sdbsCustomerService.mergeCustomer(newUuid, oldUuid, sm.getEmployeeId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/isLogin", method = RequestMethod.GET)
	public JSONObject mergeCustomer(HttpServletRequest request) {
		try {
			Boolean isLogin = false;
			Integer customerId = (Integer) request.getSession().getAttribute("customerId");
			if (customerId != null)
				isLogin = true;
			return JsonUtil.succeedJson(isLogin);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年5月28日 功能:顾客自己预约
	 * @param clubId
	 * @param request
	 * @param doctorId
	 * @param period
	 * @return
	 */
	@PostMapping("appointment/prepare/{clubId}")
	public JSONObject appointmentPrepare(@PathVariable Integer clubId, HttpServletRequest request,
			SdbsAppointment appointment) {
		try {
			CustomerSessionManager cm = new CustomerSessionManager(request.getSession());
			String period = appointment.getPeriod();
			Integer doctorId = appointment.getDoctorId();
			if (clubId == null)
				throw new ShidaoException("设置门店");
			if (doctorId == null)
				throw new ShidaoException("设置医生");
			if (StringUtil.isNullOrEmpty(period))
				throw new ShidaoException("选择时间段");
			if (!DateUtil.isLocalTimeInPeriod(period))
				throw new ShidaoException("该时间段已失效");
			// 设置顾客编号
			appointment.setCustomerId(cm.getCustomerId());
			// 设置门店
			appointment.setClubId(clubId);
			// 同一个顾客同一天同一个门店同一个医生只能预约一次
			appointment.setPeriod(null);
			List<SdbsAppointment> list = appointmentService.list(appointment);
			if (list != null && !list.isEmpty())
				throw new ShidaoException("不能重复预约");
			// 设置时间段
			appointment.setPeriod(period);
			// 设置助理
			SdcomRelationDoctorClub assCondition = new SdcomRelationDoctorClub();
			assCondition.setClubId(clubId);
			assCondition.setDoctorId(doctorId);
			List<SdcomRelationDoctorClub> relationDoctorClubs = relationDoctorClubService.list(assCondition);
			if (relationDoctorClubs == null || relationDoctorClubs.isEmpty())
				throw new ShidaoException("医生门店助理关系没有建立，快速预约失败");
			if (relationDoctorClubs.get(0).getAssistantId() == null)
				throw new ShidaoException("该医生在这个门店没有安排助理，快速预约失败");
			appointment.setAssistantId(relationDoctorClubs.get(0).getAssistantId());
			HandlerFactory.createAppointmentHandler(AppointmentCategory.TCM).insert(appointment);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年5月29日 功能:
	 * @param date
	 * @return
	 */
	@GetMapping("/getPeriods")
	public JSONObject getPeriods(Date date) {
		try {
			JSONObject map = JsonUtil.succeedJson();
			if (DateUtils.isSameDay(new Date(), date))
				map.put("periods", DateUtil.getTodayDoctorAppointmentPeriods());
			else
				map.put("periods", DateUtil.getPeriod("09:00", "18:00", 30));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年5月29日 功能:顾客的预约列表
	 * @param request
	 * @param clubId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/appointment/list")
	public JSONObject appointmentList(HttpServletRequest request, Integer clubId) {
		try {
			JSONObject map = JsonUtil.succeedJson();
			CustomerSessionManager cm = new CustomerSessionManager(request.getSession());
			SdbsAppointment condition = new SdbsAppointment();
			condition.setCustomerId(cm.getCustomerId());
			condition.setClubId(clubId);
			condition.addParameter("Future", true);
			// 1. 获取预约记录
			map.put("result", appointmentService.list(condition));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 客户退出
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/quit")
	public JSONObject quit(HttpServletRequest request) {
		try {
			CustomerSessionManager csManager = new CustomerSessionManager(request.getSession(), false);
			if (csManager.isLoggedIn()) {
				request.getSession().invalidate();
			}
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping("/listLastVisitInfo")
	public JSONObject listLastVisitInfo(HttpServletRequest request,
			@RequestParam(required=false) LocalDate dateStart, @RequestParam(required=false) LocalDate dateEnd, Integer doctorId) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession()); 
			return JsonUtil.succeedJson(sdbsCustomerService.listLastVisitInfo(es.getClubId(), dateStart, dateEnd,es.getClubId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping("/customer")
	public JSONObject listValidListOfCategory(HttpServletRequest request,String customerId,  ValidListCategory category) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession()); 
			return JsonUtil.succeedJson(customerInfoService.listValidListOfCategory(customerId, es.getClubId(), category));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping("/customerInfo/insert")
	public JSONObject insertFieldValue(HttpServletRequest request,SdbsCustomerInfo customerInfo) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession()); 
			customerInfo.setClubId(es.getClubId());
			customerInfoService.insertSelective(customerInfo);
			return JsonUtil.succeedJson(customerInfo.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping("/customerInfo/delete")
	public JSONObject deleteFieldValue(HttpServletRequest request,Integer infoId) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession()); 			
			customerInfoService.deleteByPrimaryKey(infoId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
	
	
}
