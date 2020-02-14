package com.shidao.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.MRPrescriptionCategory;
import com.shidao.enums.ServiceCardType;
import com.shidao.model.SdbsAppointment;
import com.shidao.model.SdbsCustomer;
import com.shidao.model.SdbsCustomerMembership;
import com.shidao.model.SdbsCustomerMembershipItem;
import com.shidao.model.SdbsProfession;
import com.shidao.model.TcmMedicalRecord;
import com.shidao.model.TcmMedicalRecordPrescription;
import com.shidao.service.MzMaizhenyiHistoryService;
import com.shidao.service.SdbsAppointmentD2dService;
import com.shidao.service.SdbsAppointmentService;
import com.shidao.service.SdbsCustomerCareService;
import com.shidao.service.SdbsCustomerMembershipItemService;
import com.shidao.service.SdbsCustomerMembershipService;
import com.shidao.service.SdbsCustomerService;
import com.shidao.service.SdbsCustomerServiceGroupService;
import com.shidao.service.SdbsPayOrderService;
import com.shidao.service.SdbsProfessioService;
import com.shidao.service.SdcomClubService;
import com.shidao.service.SdepConsultationService;
import com.shidao.service.TcmMedicalRecordService;
import com.shidao.util.CustomerSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ListResult;
import com.shidao.util.NotLoginException;
import com.shidao.util.ShidaoException;
import com.shidao.util.StringUtil;

@Controller
@RequestMapping(value = "/customer")
public class CustomerRedirectorController extends BaseController {

	private static final String CUSTOMER_CENTER_PATH = "/display/customerCenter/";

	@Autowired
	private TcmMedicalRecordService tcmMedicalrecordService;
	
	@Autowired
	private SdbsAppointmentD2dService sdbsAppointmentD2dService;

	@Autowired
	private SdbsCustomerMembershipService customerMembershipService;

	@Autowired
	private SdbsCustomerService customerService;

	@Autowired
	private SdbsCustomerMembershipItemService customerMembershipItemService;

	@Autowired
	private SdbsPayOrderService payOrderService;

	@Autowired
	private TcmMedicalRecordService medicalRecordService;

	@Autowired
	private MzMaizhenyiHistoryService maizhenyiHistoryService;

	@Autowired
	private SdbsProfessioService professioService;

	@Autowired
	private SdbsCustomerCareService customerCareService;
	
	@Autowired
	private SdbsCustomerServiceGroupService customerServiceGroupService;
	
	@Autowired
	private SdepConsultationService consultationService;
	
//	@Autowired
//	private SdcomEmployeeService employeeService;
	
	@Autowired
	private SdcomClubService clubService;
	
	@Autowired
	private SdbsAppointmentService appointmentService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String defaultPage() {
		return CUSTOMER_CENTER_PATH + "/login";
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String customerDetailInfo(Model model, HttpServletRequest request) {
		try {
			CustomerSessionManager cm = new CustomerSessionManager(request.getSession());
			Integer customerId = cm.getCustomerId();
			if(customerId == null)
				return "redirect:/";
			SdbsCustomer customer = customerService.selectByPrimaryKey(customerId);
			if(customer == null)
				return "redirect:/";
			int defaultCount = 6;
			// 鑾峰彇clubId
			
			model.addAttribute("customer", customer);
			Map<Object, Object> map = new HashMap<>();
			SdbsCustomerMembership customerMembership = new SdbsCustomerMembership();
			customerMembership.setCustomerId(customerId);
			customerMembership.setServiceCardType(ServiceCardType.TCK);
			List<SdbsCustomerMembership> customerMemberships = customerMembershipService.list(customerMembership);
			for (SdbsCustomerMembership sdbsCustomerMembership : customerMemberships) {
				SdbsCustomerMembershipItem customerMembershipItem = new SdbsCustomerMembershipItem();
				customerMembershipItem.setCustomerMembershipId(sdbsCustomerMembership.getId());
				map.put(sdbsCustomerMembership.getServiceCardName(), customerMembershipItemService.list(customerMembershipItem));
			}
			//服务组
			model.addAllAttributes(customerServiceGroupService.getCustomerServiceGroups(customerId,null));
			
			model.addAllAttributes(payOrderService.listByCustomerId(customerId, defaultCount));
//			model.addAttribute("payorders", payOrderService.listByCustomerId(customerId, defaultCount));
			// 病历列表
			model.addAttribute("records", medicalRecordService.listByCustomerId(customerId, defaultCount));
			// 脉诊列表
			model.addAttribute("mzHistories", maizhenyiHistoryService.getByCustomerId(customerId, defaultCount));
			model.addAttribute("memberships", map);
			// 职业列表
			model.addAttribute("profession", professioService.list(new SdbsProfession()));
			// 客情关怀信息
			model.addAttribute("cares", customerCareService.getCareOfCutomer(customerId, defaultCount));
			// 服务组
			model.addAttribute("serviceGroup", customerService.getServiceGroup(customerId));
			// 项目卡
			model.addAttribute("XMKList", customerService.listXMKById(customerId, null));
			//前三条咨询列表
			model.addAttribute("consultation", consultationService.listConsultAboutCustomer(customerId, 1, 3));
			// 家属列表
			List<SdbsCustomerMembership> memberships = customer.getMemberships();
			if (memberships!= null && memberships.size()>0 ){
				List<SdbsCustomer> relations = customerService.listCustomerRelation(customerId);
				model.addAttribute("customerRelation", relations);
				Object object = memberships.stream().anyMatch(m->m.getNumberOfUsers()==-1) ?
						"无限制" :
							memberships.stream().mapToInt(SdbsCustomerMembership::getNumberOfUsers).sum() ;	
					
				model.addAttribute("num", relations.size()+"/"+object);
				model.addAttribute("persons", object);
			}
			else {
				SdbsCustomerMembership key = customerMembershipService.selectByPrimaryKey(customer.getMainId());
				if (key != null) {
					// 主卡信息
					model.addAttribute("mainMembership", customerService.selectByPrimaryKey(key.getCustomerId()));
				}
			}
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return CUSTOMER_CENTER_PATH + "/userCenter";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request, Model model) {
		return CUSTOMER_CENTER_PATH + "/login";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, Model model, HttpServletResponse response) throws IOException {
		request.getSession().invalidate();
		return "redirect:login";
	}

	@RequestMapping(value = "/D2DQuickAppoint")
	public String D2DQuickAppoint(HttpServletRequest request, Model model) {
		try {
			SdbsCustomer.getCurrentId(request);
			model.addAttribute("title", "快速预约");
			return CUSTOMER_CENTER_PATH + "/D2DQuickAppoint";
		} catch (Exception e) {
		}
		return defaultPage();
	}

	//
	@RequestMapping(value = "/D2DJoinGroup")
	public String D2DJoinGroup(HttpServletRequest request, Model model) {
		try {
			SdbsCustomer.getCurrentId(request);
			model.addAttribute("title", "加入群组");
			return CUSTOMER_CENTER_PATH + "/D2DJoinGroup";
		} catch (Exception e) {
		}
		return defaultPage();
	}

	@RequestMapping(value = "/D2DPay")
	public String D2DPay(HttpServletRequest request, Model model) {

		return CUSTOMER_CENTER_PATH + "/D2DPay";
	}

	//
	@RequestMapping(value = "/D2DMyAppointments", method = RequestMethod.GET)
	public String D2DMyAppointments(HttpServletRequest request, Model model) {
		Integer curCustomerId;
		try {
			curCustomerId = SdbsCustomer.getCurrentId(request);
			Map<String, Object> sdbsAppoinmentD2ds = sdbsAppointmentD2dService.getByCustomerId(curCustomerId, 1, 10);
			model.addAllAttributes(sdbsAppoinmentD2ds);
			model.addAttribute("title", "我的预约列表");
			model.addAttribute("topText", java.net.URLEncoder.encode("中文标题", "UTF-8"));
		} catch (NotLoginException e) {
			return defaultPage();
		} catch (ShidaoException e) {
			e.printStackTrace();
			model.addAttribute("error", e);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", e);
		}
		return CUSTOMER_CENTER_PATH + "D2DMyAppointments";
	}

	@RequestMapping(value = "/{pageName}", method = RequestMethod.GET)
	public String redirectPage(@PathVariable(value = "pageName") String pageName) {
		return CUSTOMER_CENTER_PATH + pageName;
	}

	@RequestMapping(value = "/top/{title}")
	public String redirectPage(@PathVariable(name = "title") String title, Model model) {
		model.addAttribute("title", title);
		return CUSTOMER_CENTER_PATH + "top";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月12日
	 * 功能: 显示当前uuid对应的病历
	 * @param request
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping(value = "/medicalRecord/detail/{uuid}")
	public String medicalRecordDetailByUuid(HttpServletRequest request, Model model, @PathVariable(name = "uuid") String uuid,Boolean needPager) {
		try {
			TcmMedicalRecord record = tcmMedicalrecordService.selectByUuid(uuid);
			if (record != null) {
				List<TcmMedicalRecordPrescription> prescriptions = record.getPrescriptions();
				
				Map<MRPrescriptionCategory, Object> notMedicinePrescription = new LinkedHashMap<>();
				for (MRPrescriptionCategory category : MRPrescriptionCategory.notMedicine) {
					notMedicinePrescription.put(category, prescriptions.stream().filter(a->a.getCategory()==category).findFirst().orElse(null));
					prescriptions.removeIf(a->a.getCategory() == category);
				}
				model.addAttribute("notMedicinePrescription", notMedicinePrescription);
				if(needPager == null || needPager) {
					needPager = true;
					model.addAttribute("pager", tcmMedicalrecordService.getPagerInfo(uuid,uuid));
				}
				model.addAttribute("detail",record.transferFields2Html());
			}
			model.addAttribute("needPager", needPager);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return CUSTOMER_CENTER_PATH + "medicalRecords";
	}	

	@ResponseBody
	@RequestMapping(value = "/medicalRecords/json")
	public JSONObject myMedicalRecordJson(HttpServletRequest request, Model model, Integer customerId, Integer pageSize,
			Integer pageNum) {
		try {
			TcmMedicalRecord condition = new TcmMedicalRecord();
			condition.setCustomerId(customerId);

			ListResult<TcmMedicalRecord> map = tcmMedicalrecordService.list(condition, pageNum, pageSize);

			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 刘鹏远
	 */
	@RequestMapping(value = "/consult",method=RequestMethod.GET)
	public String consult(Model model,HttpServletRequest request) {
		try {
			Integer customerId = (Integer)request.getSession().getAttribute("customerId");
			if (customerId != null)
				model.addAttribute("customer", customerService.selectByPrimaryKey(customerId));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return CUSTOMER_CENTER_PATH + "consult";
	}
	
	@RequestMapping(value = "/consult/list",method=RequestMethod.GET)
	public String consultList(Model model,Integer pageNum,Integer pageSize,HttpServletRequest request) {
		try {
			CustomerSessionManager cm = new CustomerSessionManager(request.getSession());
			model.addAttribute("customerId", cm.getCustomerId());
			model.addAttribute("result", consultationService.listConsultAboutCustomer(cm.getCustomerId(), pageNum, pageSize));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return CUSTOMER_CENTER_PATH + "consultList";
	}
	
	@RequestMapping(value = "/consult/detail/{id}",method=RequestMethod.GET)
	public String consultDetail(@PathVariable("id")Integer id,Model model,HttpServletRequest request) {
		try {
			CustomerSessionManager csm =  new CustomerSessionManager(request.getSession());
			model.addAttribute("id", id);
			model.addAttribute("consultDetail", consultationService.getOneConsultationDetail(id,csm.getCustomerId(),null));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return CUSTOMER_CENTER_PATH + "consultDetail";
	}
	
	@RequestMapping(value = "/consultLogin",method=RequestMethod.GET)
	public String consultModified(Model model,String mobile) {
		try {
			model.addAttribute("mobile", mobile);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return CUSTOMER_CENTER_PATH + "consultLogin";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月28日
	 * 功能:预约页面
	 * @param model
	 * @param request
	 * @param clubId
	 * @return
	 */
	@GetMapping("appointment/list")
	public String appointment(Model model,HttpServletRequest request,Integer clubId,String action,Integer doctorId) {
		try {
			model.addAttribute("clubId", clubId);
			model.addAttribute("action", action);
			model.addAttribute("doctorId", doctorId);
			if (!StringUtil.isNullOrEmpty(action) && action.equals("make") && clubId != null) {
				model.addAttribute("clubs", clubService.listClubRelationDoctor());
				return CUSTOMER_CENTER_PATH + "appointmentList";
			}
			CustomerSessionManager cm = new CustomerSessionManager(request.getSession());
			SdbsAppointment condition = new SdbsAppointment();
			condition.setCustomerId(cm.getCustomerId());
			condition.setClubId(clubId);
			condition.addParameter("Future", true);
			// 1. 获取预约记录
			model.addAttribute("list", appointmentService.list(condition));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return CUSTOMER_CENTER_PATH + "appointmentList";
	}
}
