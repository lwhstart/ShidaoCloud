package com.shidao.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.shidao.enums.ClubType;
import com.shidao.enums.DeliveryVoucherCategory;
import com.shidao.enums.MRPrescriptionCategory;
import com.shidao.enums.MedicalRecordStatus;
import com.shidao.enums.PDFPrescriptionCategory;
import com.shidao.enums.PayCategory;
import com.shidao.enums.SupportPayType;
import com.shidao.enums.PayMethod;
import com.shidao.enums.Position;
import com.shidao.enums.ProductCategory;
import com.shidao.enums.SalesOrder4CustomerOperation;
import com.shidao.enums.SalesOrderCategory;
import com.shidao.enums.SdepContactorRequestStatus;
import com.shidao.enums.ServiceCardType;
import com.shidao.jsp.information.SalesOrder4CustomerInformation;
import com.shidao.model.CrmCustomerNotice;
import com.shidao.model.MzMaizhenyiHistory;
import com.shidao.model.SdbsAppointment;
import com.shidao.model.SdbsAppointmentD2d;
import com.shidao.model.SdbsCompany;
import com.shidao.model.SdbsCustomer;
import com.shidao.model.SdbsCustomerCare;
import com.shidao.model.SdbsCustomerCare.CustomerCareType;
import com.shidao.model.SdbsCustomerSource;
import com.shidao.model.SdbsDelivery.DeliveryStatus;
import com.shidao.model.SdbsPayOrder;
import com.shidao.model.SdbsPayOrderItem;
import com.shidao.model.SdbsPeriod;
import com.shidao.model.SdbsProduct;
import com.shidao.model.SdbsSalesOrder;
import com.shidao.model.SdbsSalesOrderItem;
import com.shidao.model.SdbsServiceCard;
import com.shidao.model.SdbsVendor;
import com.shidao.model.SdcomClub;
import com.shidao.model.SdcomClubMembership;
import com.shidao.model.SdcomClubMembership.OperationCategory;
import com.shidao.model.SdcomEmployee;
import com.shidao.model.SdepConsultation;
import com.shidao.model.SdepContactorRequest;
import com.shidao.model.SdepPayCategory;
import com.shidao.model.SdepPayMethod;
import com.shidao.model.TcmMedicalRecord;
import com.shidao.model.TcmMedicalRecordPrescription;
import com.shidao.model.WmsCheckOrder;
import com.shidao.model.WmsCheckOrder.CheckStatus;
import com.shidao.model.WmsCheckOrderDetail;
import com.shidao.model.WmsDeliveryVoucher;
import com.shidao.model.WmsDeliveryVoucherDetail;
import com.shidao.model.WmsFinancialStatement;
import com.shidao.model.WmsInventory;
import com.shidao.model.WmsPurchaseOrder;
import com.shidao.model.WmsPurchaseOrderDetail;
import com.shidao.model.WmsRelationClubWarehouse;
import com.shidao.model.WmsSetting;
import com.shidao.model.WmsSetting.SettingCategory;
import com.shidao.model.WmsWarehouse;
import com.shidao.model.WmsWarehouseVoucher;
import com.shidao.model.WmsWarehouseVoucher.StatusType;
import com.shidao.model.WmsWarehouseVoucher.WarehouseVoucherCategory;
import com.shidao.model.WmsWarehouseVoucherDetail;
import com.shidao.service.CrmCustomerNoticeService;
import com.shidao.service.MzMaizhenyiHistoryService;
import com.shidao.service.SdbsAppointmentD2dOrderService;
import com.shidao.service.SdbsAppointmentD2dService;
import com.shidao.service.SdbsAppointmentService;
import com.shidao.service.SdbsCompanyService;
import com.shidao.service.SdbsCustomerCareService;
import com.shidao.service.SdbsCustomerMembershipService;
import com.shidao.service.SdbsCustomerService;
import com.shidao.service.SdbsCustomerSourceService;
import com.shidao.service.SdbsDeliveryService;
import com.shidao.service.SdbsPayOrderItemService;
import com.shidao.service.SdbsPayOrderService;
import com.shidao.service.SdbsPeriodService;
import com.shidao.service.SdbsProductService;
import com.shidao.service.SdbsProfessioService;
import com.shidao.service.SdbsSalesOrderItemService;
import com.shidao.service.SdbsSalesOrderService;
import com.shidao.service.SdbsServiceCardService;
import com.shidao.service.SdbsVendorService;
import com.shidao.service.SdcomClubMembershipService;
import com.shidao.service.SdcomClubService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.service.SdcomRecruitmentService;
import com.shidao.service.SdepConsultationService;
import com.shidao.service.SdepContactorRequestService;
import com.shidao.service.SdepPayCategoryService;
import com.shidao.service.SdepPayMethodService;
import com.shidao.service.TcmMedicalRecordService;
import com.shidao.service.WmsCheckOrderDetailService;
import com.shidao.service.WmsCheckOrderService;
import com.shidao.service.WmsDeliveryVoucherDetailService;
import com.shidao.service.WmsDeliveryVoucherService;
import com.shidao.service.WmsFinancialStatementService;
import com.shidao.service.WmsInventoryService;
import com.shidao.service.WmsPurchaseOrderDetailService;
import com.shidao.service.WmsPurchaseOrderService;
import com.shidao.service.WmsRelationClubWarehouseService;
import com.shidao.service.WmsSettingService;
import com.shidao.service.WmsWarehouseService;
import com.shidao.service.WmsWarehouseVoucherDetailService;
import com.shidao.service.WmsWarehouseVoucherService;
import com.shidao.service.WsMessageService;
import com.shidao.setting.ClubSetting;
import com.shidao.util.ApplicationPropertiesBase.TestProperties;
import com.shidao.util.CustomerSessionManager;
import com.shidao.util.DateUtil;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.HttpUtil;
import com.shidao.util.JsonUtil;
import com.shidao.util.ListResult;
import com.shidao.util.NotLoginException;
import com.shidao.util.PermissionEnum;
import com.shidao.util.ShidaoException;
import com.shidao.util.StreamUtil;
import com.shidao.util.StringUtil;
import com.shidao.util.TimeRecord;
import com.shidao.vo.DeliveryVoucherManauaVO;
import com.shidao.vo.EmployeeVO;
import com.shidao.vo.SalesOrderVO;
import com.shidao.vo.SdbsAppointmentD2dOrderVO;
import com.shidao.vo.SdbsAppointmentVO;
import com.shidao.vo.SdbsCompanyVO;
import com.shidao.vo.YunmaiUI;

/**
 * @author liupengyuan
 *
 */
@Controller
@RequestMapping(value = "/workspace")
public class WorkspaceRedirectorController extends BaseController {
	private static final String WORKSPACE_PATH = "/display/workspace/";

	@Autowired
	private SdcomEmployeeService employeeService;

	@Autowired
	private SdbsCompanyService companyService;

	@Autowired
	private SdcomClubService clubService;

	@Autowired
	private SdbsAppointmentService appointmentService;

	@Autowired
	private SdbsAppointmentD2dService appointmentD2dService;

	@Autowired
	private SdbsPeriodService sdbsPeriodService;

	@Autowired
	private TcmMedicalRecordService tcmmedicalrecordService;

	@Autowired
	private SdbsAppointmentD2dOrderService sdbsD2DOrderService;

	@Autowired
	private SdbsSalesOrderService salesOrderService;

	@Autowired
	private WsMessageService wsMessageService;

	@Autowired
	private SdcomRecruitmentService sdcomRecruitmentService;

	@Autowired
	private SdbsSalesOrderItemService salesOrderItemService;

	@Autowired
	private SdbsCustomerMembershipService customerMembershipService;

	@Autowired
	private SdbsProductService sdbsProductService;

	@Autowired
	private SdbsCustomerService customerService;

	@Autowired
	private SdbsServiceCardService serviceCardService;

	@Autowired
	private MzMaizhenyiHistoryService maizhenyiHistoryService;

	@Autowired
	private SdbsPayOrderService payOrderService;

	@Autowired
	private SdbsPayOrderItemService payOrderItemService;

	@Autowired
	private SdcomClubMembershipService membershipService;

	@Autowired
	private WmsInventoryService inventoryService;

	@Autowired
	private SdbsVendorService sdbsVendorService;

	@Autowired
	private WmsPurchaseOrderDetailService purchaseOrderDetailService;

	@Autowired
	private WmsPurchaseOrderService purchaseOrderService;

	@Autowired
	private WmsWarehouseVoucherService warehouseVoucherService;

	@Autowired
	private WmsWarehouseService warehouseService;

	@Autowired
	private WmsWarehouseVoucherDetailService warehouseVoucherDetailService;

	@Autowired
	private WmsRelationClubWarehouseService relationClubWarehouseService;

	@Autowired
	private WmsSettingService settingService;

	@Autowired
	private WmsCheckOrderDetailService checkOrderDetailService;

	@Autowired
	private WmsCheckOrderService checkOrderService;

	@Autowired
	private WmsDeliveryVoucherService deliveryVoucherService;

	@Autowired
	private WmsDeliveryVoucherDetailService deliveryVoucherDetailService;

	@Autowired
	private WmsFinancialStatementService financialStatementService;

	@Autowired
	private CrmCustomerNoticeService customerNoticeService;

	@Autowired
	private SdbsCustomerCareService customerCareService;

	@Autowired
	private SdbsProfessioService professioService;

	@Autowired
	private SdepConsultationService consultationService;

	@Autowired
	private MzMaizhenyiHistoryService mzyHistoryService;

	@Autowired
	private SdbsDeliveryService deliveryService;

	@Autowired
	private SdbsCustomerSourceService customerSourceService;

	@Autowired
	private SdepContactorRequestService contactorRequestService;

	@Autowired
	private SdepPayCategoryService payCategoryService;

	@Autowired
	private SdepPayMethodService payMethodService;

	@RequestMapping(value = "/{pageName}")
	public String redirectPage(@PathVariable(value = "pageName") String pageName) {
		return WORKSPACE_PATH + pageName;
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String defaultPage(HttpServletRequest request, Model model) {
		return "redirect:/workspace/index";
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String indexPage(HttpServletRequest request, Model model, String version) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (!sm.isLoggedIn()) {
				return "redirect:login";
			}
//			String gls =GlobalSetting.Instance.getClubLogoUUID();
			model.addAttribute("clubType", sm.getClubType());
			model.addAttribute("logoUuid", sm.getClubLogoUuid());
			model.addAttribute("clubSetting", ClubSetting.getSetting(sm.getClubId()));
			model.addAttribute("userName", sm.getCurrent().getName());
			model.addAttribute("today", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			if (sm.hasPermission(PermissionEnum.My_Consulation)) {
				SdepConsultation condition = new SdepConsultation();
				condition.setEmployeeId(sm.getEmployeeId());
				condition.setListSpecialCondition("NoRead");
				List<SdepConsultation> list = consultationService.listConsultAboutEmployee(condition, 1, 1).getList();
				if (list.size() > 0) {
					model.addAttribute("employeeIdWithUnreadConsultant", sm.getEmployeeId());
					model.addAttribute("customerIdWithUnreadConsultant", list.get(0).getCustomerId());
				}
			}
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "index" + (version != null ? version : "");

	}

	@RequestMapping(value = "chat")
	public String redirectChat(HttpServletRequest request, Model model) {
		try {
			Object employeeId = request.getSession().getAttribute("employeeId");
			Object customerId = request.getSession().getAttribute("customerId");
			String loginType = "employee";
			String name = null;
			if (employeeId != null) {
				EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
//				model.addAttribute("defaultContactors", employeeService.getDoctorsOfClub(sm.getClubId()));
				model.addAttribute("unreadMessageCount",
						wsMessageService.getUnreadMessageCount(sm.getEmployeeId(), sm.getClubId()));
				name = sm.getCurrent().getName();
			}
			if (customerId != null) {
				CustomerSessionManager cm = new CustomerSessionManager(request.getSession());
				model.addAttribute("unreadMessageCount",
						wsMessageService.getCustomerUnreadMessageCount(cm.getCustomerId()));
				name = cm.getCurrent().getName();
				loginType = "customer";
			}
			String uid = request.getSession().getAttribute("uid").toString();
			/*
			 * SdepContactor condition = new SdepContactor(); condition.setUserId(uid);
			 * condition.setCategory(SdepContactorCategory.Contactor);
			 * model.addAttribute("contactors",
			 * contactorService.listCustomerContactor(condition));
			 */
			SdepContactorRequest contactorRequest = new SdepContactorRequest();
			contactorRequest.setContactorId(uid);
			contactorRequest.setStatus(SdepContactorRequestStatus.Requested);
			List<SdepContactorRequest> list = contactorRequestService.list(contactorRequest);
			Boolean isRequest = false;
			if (list != null && !list.isEmpty())
				isRequest = true;
			model.addAttribute("isRequest", isRequest);
			model.addAttribute("loginType", loginType);
			model.addAttribute("name", name);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}

		return WORKSPACE_PATH + "chat";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request, Model model) {
		if (TestProperties.getInstance().getTestOpened())
			model.addAttribute("testInformationString", TestProperties.getInstance().getTestInformationString());
		return WORKSPACE_PATH + "/login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(String loginName, String password, HttpServletRequest request, RedirectAttributesModelMap model) {

		try {
			HttpUtil.clearSession(request);
			Map<String, Object> loginINfo = employeeService.login(loginName, password);
			loginINfo.forEach((k,v) ->request.getSession().setAttribute(k,v));			
			return "redirect:index";
		} catch (ShidaoException e) {
			model.addFlashAttribute("errorMsg", e.getMessage());
			return "redirect:login";
		}
	}

	@RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
	public String postPassword(@RequestParam String mobile, @RequestParam(value = "password") String password,
			@RequestParam(value = "verifyPassword") String verifyPassword, @RequestParam(value = "code") String code,
			RedirectAttributesModelMap redirectMap) {
		redirectMap.addFlashAttribute("mobile", mobile);
		try {
			employeeService.forgetPassword(mobile, password, verifyPassword, code);
			return "redirect:login";
		} catch (Exception e) {
			redirectMap.addFlashAttribute("errorMsg", e.getLocalizedMessage());
			return "redirect:login?type=reset";
		}
	}

	@GetMapping(value = "/resetPassword")
	public String resetPassword() {
		return WORKSPACE_PATH + "/resetPassword";
	}

	@PostMapping(value = "/resetPassword")
	public String resetPassword(@RequestParam(value = "oldPassword") String oldPassword,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "verifyPassword") String verifyPassword, HttpServletRequest request,
			RedirectAttributesModelMap redirectMap) {
		try {
			EmployeeSessionManager eManager = new EmployeeSessionManager(request.getSession());
			employeeService.resetPassword(eManager.getCurrent().getLoginName(), oldPassword, password, verifyPassword);
			return "redirect:login";
		} catch (Exception e) {
			redirectMap.addFlashAttribute("errorMsg", e.getLocalizedMessage());
			return "redirect:resetPassword";
		}
	}

	/* Company---------------------------------- */
	@RequestMapping(value = "/company/list", method = RequestMethod.GET)
	public String companyList(HttpServletRequest request, Model model, SdbsCompanyVO condition, Integer pageNum,
			Integer pageSize) {
		model.addAttribute("condition", condition);
		try {
			Map<String, Object> result = companyService.listByCondition(condition, pageNum, pageSize);
			model.addAllAttributes(result);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "companyList";
	}

	@RequestMapping(value = "/company/detail", method = RequestMethod.GET)
	public String companyEdit(HttpServletRequest request, Model model, Integer id) {
		try {
			SdbsCompany company = (id == null || id <= 0) ? new SdbsCompany() : companyService.selectByPrimaryKey(id);
			model.addAttribute("company", company);
			model.addAttribute("clubs", clubService.list());
			model.addAttribute("salesId", employeeService.list());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "companyDetail";
	}

	@RequestMapping(value = "/company/post", method = RequestMethod.POST)
	public String postCompany(HttpServletRequest request, Model model, SdbsCompany company,
			RedirectAttributes attributes) {
		try {
			if (company.getId() == null || company.getId() == 0) {
				companyService.insertSelective(company);
			} else {
				companyService.updateByPrimaryKeySelective(company);
			}

			return WORKSPACE_PATH + "OperationDone";// "redirect:list";
		} catch (Exception e) {
			attributes.addFlashAttribute("employee", company);
			attributes.addFlashAttribute("clubs", clubService.list());
			attributes.addFlashAttribute("salesId", employeeService.list());

			attributes.addFlashAttribute("errorMsg", e.getMessage());
			return WORKSPACE_PATH + "employeeDetail";
		}
	}

	/* AppointmentD2D - */
	@RequestMapping(value = "/appointmentD2D/list", method = RequestMethod.GET)
	public String appointmentD2DList(HttpServletRequest request, Model model, SdbsAppointmentD2d condition,
			Integer pageNum, Integer pageSize) {
		model.addAttribute("condition", condition);
		try {
			Map<String, Object> appointmentD2D = appointmentD2dService.listByCondition(condition, pageNum, pageSize);
			model.addAllAttributes(appointmentD2D);
			model.addAttribute("condition", appointmentD2D);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "companyList";
	}

	@GetMapping("/doctor/todayWork")
	public String getDoctorTodayWork(HttpServletRequest request, Model model, SdbsAppointment condition) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setDoctorId(sm.getEmployeeId());
			condition.setClubId(sm.getClubId());
			condition.setDate();
			model.addAttribute("customerName",
					condition.getCustomer() == null ? "" : condition.getCustomer().getName());
			condition.setMedicalRecord(new TcmMedicalRecord());
			MedicalRecordStatus[] statuses = { MedicalRecordStatus.Created, MedicalRecordStatus.Diagnosing,
					MedicalRecordStatus.Completed };
			Map<MedicalRecordStatus, List<SdbsAppointment>> appointments = new LinkedHashMap<>();

			for (MedicalRecordStatus status : statuses) {
				condition.getMedicalRecord().setStatus(status);
				appointments.put(status, appointmentService.list(condition));
			}
			model.addAttribute("total", appointments.values().stream().mapToInt(l -> l.size()).sum());
			model.addAttribute("appointments", appointments);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "doctorTodayWork";
	}

	@RequestMapping(value = "/appointment/list/{type}", method = RequestMethod.GET)
	public String appointmentList(HttpServletRequest request, Model model, SdbsAppointment condition, Integer pageNum,
			Integer pageSize, @PathVariable("type") String type) {
		try {
			/*
			 * 1. 设置基本信息:门诊列表，
			 */
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.addParameter(type, true);
			if (sm.hasPermission(PermissionEnum.All_APPOINTMENTS)) {
				model.addAttribute("clubs", clubService.list());
				if (condition.getClubId() != null) {
					model.addAttribute("doctors", employeeService.list(condition.getClubId(), Position.Doctor));
				}
			} else if (sm.hasPermission(PermissionEnum.Club_APPOINTMENTS)) {
				model.addAttribute("doctors", employeeService.list(sm.getClubId(), Position.Doctor));
				condition.setClubId(sm.getClubId());
			} else if (sm.hasPermission(PermissionEnum.My_APPOINTMENTS)) {
				condition.setClubId(sm.getClubId());
				if (sm.getPosition().equals(Position.Doctor.getText())
						|| sm.getPosition().equals(Position.Doctor.getText()))
					condition.setDoctorId(sm.getEmployeeId());
				else if (sm.getPosition() == Position.Assistant.getText())
					condition.setAssistantId(sm.getEmployeeId());
			} else {
				throw new ShidaoException("没有权限!");
			}
			// 2.保留条件
			model.addAttribute("condition", condition);
			model.addAttribute("type", type);
			// 3. 获取预约记录
			ListResult<SdbsAppointment> appointments = appointmentService.list(condition, pageNum, pageSize);
			model.addAttribute("result", appointments);
			// 4. 分页所需信息
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "appointmentList";
	}

	// 查看病历
	@RequestMapping(value = "/checkMedicalRecord/{salesOrderId}", method = RequestMethod.GET)
	public String checkMedicalRecord(HttpServletRequest request, Model model,
			@PathVariable(value = "salesOrderId") Integer salesOrderId) {
		try {
			Integer employeeId = (Integer) request.getSession().getAttribute("employeeId");
			if (employeeId == null) {
				throw new ShidaoException("请先登录");
			}
			SdbsSalesOrder salesOrder = salesOrderService.selectByPrimaryKey(salesOrderId);
			model.addAttribute("salesOrder", salesOrder);
			model.addAttribute("sumTotal", salesOrderItemService.getSumTotalBysalesOrderId(salesOrderId));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "checkCase";
	}

	// 药房 pharmacy
	@RequestMapping(value = "/pharmacy/order/list/{status}/{date}", method = RequestMethod.GET)
	public String pharmacyList(HttpServletRequest request, Model model, WmsDeliveryVoucher condition,
			@PathVariable(name = "status") WmsDeliveryVoucher.StatusType status,
			@PathVariable(name = "date") String date, Integer pageNum, Integer pageSize) {
		try {
			// 该门店仓库
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setWarehouseId(sm.getWarehouseId());
			if (date.equals("today")) {
				condition.setCreatedDate(new Date());
			}
			condition.setStatus(status);
			model.addAttribute("condition", condition);
			model.addAttribute("status", status);
			model.addAttribute("date", date);
			/*
			 * 获取列表
			 */
			model.addAttribute("result",
					deliveryVoucherService.getVoucherForPrescritionList(condition, pageNum, pageSize));

			model.addAttribute("clubs", sm.getRelatedClubs());
			/*
			 * 获取医生
			 */
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "pharmacyList";
	}

	// 药房 pharmacy
	@RequestMapping(value = "/pharmacy/order/UD/list", method = RequestMethod.GET)
	public String pharmacyManualList(HttpServletRequest request, Model model, DeliveryVoucherManauaVO condition,
			Integer pageNum, Integer pageSize) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			model.addAttribute("condition", condition);
			model.addAttribute("result", deliveryVoucherService.udPharmacyOrderList(condition, pageNum, pageSize));
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "pharmacyOrderUDList";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年4月3日 功能:协定方详情页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "pharmacy/order/UD/Detail", method = RequestMethod.GET)
	public String pharmacyOrderUDDetail(Model model, Integer deliveryVoucherId) {
		try {
			WmsDeliveryVoucherDetail condition = new WmsDeliveryVoucherDetail();
			condition.setDeliveryVoucherId(deliveryVoucherId);
			// 药品详情
			model.addAttribute("XDFinfo", deliveryVoucherService.getXDFInfoById(deliveryVoucherId));
			model.addAttribute("medicines", deliveryVoucherDetailService.list(condition));
			model.addAttribute("deliveryVoucherId", deliveryVoucherId);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "pharmacyOrderUDDetail";
	}

	// 第三节 上门预约服务列表
	@RequestMapping(value = "/companyd2dList", method = RequestMethod.GET)
	public String d2dServiceList(HttpServletRequest request, Model model,
			SdbsAppointmentD2dOrderVO sdbsAppointmentD2dOrder, Integer pageNum, Integer pageSize) {
		model.addAttribute("condition", sdbsAppointmentD2dOrder);
		try {
			SdbsPeriod sdbsPeriod = new SdbsPeriod();
			Map<String, Object> d2dList = sdbsD2DOrderService.listByCondition(sdbsAppointmentD2dOrder, pageNum,
					pageSize);
			Map<String, Object> sdbsPeriods = sdbsPeriodService.listByCondition(sdbsPeriod, 1, 100);
			List<SdbsCompany> companies = companyService.list();
			List<SdcomClub> sdbclub = clubService.list();

			model.addAllAttributes(d2dList);
			model.addAttribute("d2dList", d2dList.get("list"));
			model.addAttribute("contion", sdbsAppointmentD2dOrder);
			model.addAttribute("d2dperiods", sdbsPeriods.get("list"));
			model.addAttribute("d2dcompany", companies);
			model.addAttribute("clubList", sdbclub);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "d2dServiceList";
	}

	// 没有权限页面
	@RequestMapping(value = "/unauthorized")
	public String unauthorized() {
		return WORKSPACE_PATH + "unauthorized";
	}

	// 预约模块 快速预约页面
	@RequestMapping(value = "/quickAppointment")
	public String quickAppointment(HttpServletRequest request, Model model, SdbsAppointmentVO sdbsAppointmentVO,
			Integer pageNum, Integer pageSize) {
		model.addAttribute("condition", sdbsAppointmentVO);
		try {
			sdbsAppointmentVO.setDate(new Date());
			Map<String, Object> sdbsAppointmentVOsList = appointmentService.listByCondition(sdbsAppointmentVO, pageNum,
					pageSize);
			model.addAllAttributes(sdbsAppointmentVOsList);

			model.addAttribute("quickList", sdbsAppointmentVOsList.get("list"));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "quickAppointment";
	}

	@RequestMapping(value = "/additional/information/{medicalRecordId}", method = RequestMethod.GET)
	public String additionalInformation(@PathVariable Integer medicalRecordId, Model model) {
		try {
			TcmMedicalRecord tcmMedicalRecord = tcmmedicalrecordService.selectByPrimaryKey(medicalRecordId);
			model.addAttribute("sdbsCustomerCare",
					tcmmedicalrecordService.getCareQuestionBymedicalRecordId(tcmMedicalRecord.getCustomerId()));
			model.addAttribute("tcmMedicalRecord", tcmMedicalRecord);
			String uuid = mzyHistoryService.getMedicalRelatedMZYUUID(medicalRecordId);
			if (!com.shidao.util.StringUtil.isNullOrEmpty(uuid)) {
				model.addAttribute("mzySummary", mzyHistoryService.getMaizhenyiResultSummary(uuid));
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "/imageViewer";
	}

	@RequestMapping(value = "/forgetPassword", method = RequestMethod.GET)
	public String forgetPassword() {
		return WORKSPACE_PATH + "/forgetPassword";
	}

	// 后台招聘列表
	@RequestMapping(value = "recruit/list", method = RequestMethod.GET)
	public String recruitList(Model model, Integer pageNum, Integer pageSize) throws ShidaoException {

		Map<String, Object> sdcomRecruitmentMap = sdcomRecruitmentService.listByCondition(null, pageNum, pageSize);
		model.addAllAttributes(sdcomRecruitmentMap);
		model.addAttribute("recruitmentList", sdcomRecruitmentMap.get("list"));
		System.out.println("输出：" + sdcomRecruitmentMap.get("pageNum"));
		return WORKSPACE_PATH + "/recruit_list";
	}

	// 后台编辑招聘
	@RequestMapping(value = "recruit/{id}", method = RequestMethod.GET)
	public String recruitmentDetail(Model model, @PathVariable int id) {
		model.addAttribute("recruitmentDetail", sdcomRecruitmentService.selectByPrimaryKey(id));
		model.addAttribute("type", "edit");
		return WORKSPACE_PATH + "/recruit_add";
	}

	// 后台增加招聘
	@RequestMapping(value = "recruit/add", method = RequestMethod.GET)
	public String recruitmentAdd(Model model) {
		model.addAttribute("type", "add");
		return WORKSPACE_PATH + "/recruit_add";
	}

	// 输入病历帮助文档
	@RequestMapping(value = "/caseHelp", method = RequestMethod.GET)
	public String caseHelp() {
		return WORKSPACE_PATH + "/caseHelp";
	}

	// 客户充值记录
	@RequestMapping(value = "/rechargeRecord", method = RequestMethod.GET)
	public String rechargeRecord(Model model, SdbsPayOrder payOrder) {
		try {
			SdbsSalesOrder salesOrder = new SdbsSalesOrder();
			salesOrder.setType("充值会员卡");
			payOrder.setSalesOrder(salesOrder);
			List<SdbsPayOrder> list = payOrderService.list(payOrder);
			model.addAttribute("rechargeRecordList", list);
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return WORKSPACE_PATH + "/customerInfo/rechargerecord";
	}

	/**
	 * 统计信息，以月为单位把数据划分
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/summaryInfo", method = RequestMethod.GET)
	public String summaryInfo(Model model, SdbsPayOrderItem payOrderItem, HttpServletRequest request) {
		Integer employeeId = (Integer) request.getSession().getAttribute("employeeId");
		SdbsSalesOrder salesOrder = new SdbsSalesOrder();
		SdcomEmployee key = employeeService.selectByPrimaryKey(employeeId);
		salesOrder.setClubId(key.getClubId());
		payOrderItem.setSalesOrder(salesOrder);
		if (payOrderItem.getStartDate() == null && payOrderItem.getEndDate() == null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
			Date nowDate = new Date();
			String date = dateFormat.format(nowDate);
			payOrderItem.setStartDate(date);
		}
		List<SdbsPayOrderItem> payOrderItems = payOrderItemService.getSummaryInfo(payOrderItem);
		model.addAttribute("payOrderItems", payOrderItems);
		model.addAttribute("payOrderItem", payOrderItem);
		return WORKSPACE_PATH + "/summaryInfo";
	}

	/**
	 * getDoctorProduct该变量有值则是调理师的总月收入及总共做了多少次项目 getProduct有值表示每个项目的总次数
	 * 两个变量都为空则表示每个理疗师做了那几个项目及每个项目的次数和每个项目的总价
	 */
	@RequestMapping(value = "/getDoctorProduct", method = RequestMethod.GET)
	public String getDoctorProduct(Model model, SdbsPayOrderItem payOrderItem, HttpServletRequest request) {
		Integer employeeId = (Integer) request.getSession().getAttribute("employeeId");
		SdbsSalesOrder salesOrder = new SdbsSalesOrder();
		SdcomEmployee key = employeeService.selectByPrimaryKey(employeeId);
		salesOrder.setClubId(key.getClubId());
		payOrderItem.setSalesOrder(salesOrder);
		if (payOrderItem.getStartDate() == null && payOrderItem.getEndDate() == null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
			Date nowDate = new Date();
			String date = dateFormat.format(nowDate);
			payOrderItem.setStartDate(date);
		}

		// 调理师的总月收入及总共做了多少次项目
		SdbsSalesOrderItem salesOrderItem = new SdbsSalesOrderItem();
		salesOrderItem.setDeleted(0);
		payOrderItem.setSalesOrderItem(salesOrderItem);
		SdbsProduct product = new SdbsProduct();
		product.setCategory(ProductCategory.Liliao);
		// product.setGetAllProduct("notEmpty");
		if (payOrderItem.getGetDoctorProduct() != null) {
			salesOrderItem.setProductType(ProductCategory.Liliao.name());
			model.addAttribute("doctorProductList", payOrderItemService.getDoctorProduct(payOrderItem));
			model.addAttribute("payOrderItem", payOrderItem);
			payOrderItem.setGetDoctorProduct(null);
			salesOrderItem.setProductType(null);
			return WORKSPACE_PATH + "/doctorservicestatistics";
		}
		if (payOrderItem.getGetDoctorProduct() == null && payOrderItem.getGetProduct() == null) {
			model.addAttribute("doctorProductList", payOrderItemService.getDoctorProduct(payOrderItem));
			model.addAttribute("payOrderItem", payOrderItem);
			return WORKSPACE_PATH + "/doctorincomestatistics";
		}
		return "";
	}

	@RequestMapping(value = "getDoctorTable", method = RequestMethod.GET)
	public String getDoctorTable(Model model, SdbsPayOrderItem payOrderItem, HttpServletRequest request) {

		Integer employeeId = (Integer) request.getSession().getAttribute("employeeId");
		SdbsSalesOrder salesOrder = new SdbsSalesOrder();
		SdcomEmployee key = employeeService.selectByPrimaryKey(employeeId);
		salesOrder.setClubId(key.getClubId());
		payOrderItem.setSalesOrder(salesOrder);

		if (payOrderItem.getStartDate() == null && payOrderItem.getEndDate() == null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
			Date nowDate = new Date();
			String date = dateFormat.format(nowDate);
			payOrderItem.setStartDate(date);
		}

		try {
			SdbsSalesOrderItem salesOrderItem = new SdbsSalesOrderItem();
			salesOrderItem.setProductType(ProductCategory.Liliao.name());
			salesOrderItem.setDeleted(0);
			payOrderItem.setSalesOrderItem(salesOrderItem);
			SdcomEmployee employee = new SdcomEmployee();
			employee.setClubId(key.getClubId());
			employee.setPosition(Position.Regulate.getText());
			SdbsProduct product = new SdbsProduct();
			product.setCategory(ProductCategory.Liliao);
			// product.setGetAllProduct("notEmpty");
			product.setOnSale(1);
			Map<Object, Object> doctorProductMap = new LinkedHashMap<>();

			List<SdcomEmployee> employees = employeeService.list(employee);
			model.addAttribute("doctorList", employees);
			model.addAttribute("productList", sdbsProductService.list(product));
			model.addAttribute("payOrderItem", payOrderItem);

			for (SdcomEmployee employee2 : employees) {
				EmployeeVO employeeVO = new EmployeeVO();
				employeeVO.setId(employee2.getId());
				employeeVO.setName(employee2.getName());
				employee.setId(employee2.getId());
				payOrderItem.setEmployee(employee);
				payOrderItem.setNoSalesEmployee("notEmpty");
				doctorProductMap.put(employeeVO, payOrderItemService.getDoctorProduct(payOrderItem));
				employee.setId(null);
			}
			// 理疗师的理疗项目map集合
			model.addAttribute("doctorProductMap", doctorProductMap);

			payOrderItem.setGetDoctorProduct("notEmpty");
			model.addAttribute("doctorTime", payOrderItemService.getDoctorProduct(payOrderItem));
			payOrderItem.setGetDoctorProduct(null);

			payOrderItem.setGetProduct("notEmpty");
			model.addAttribute("productTime", payOrderItemService.getDoctorProduct(payOrderItem));
			payOrderItem.setGetProduct(null);

			payOrderItem.setGetAllTime("notEmpty");
			List<SdbsPayOrderItem> list = payOrderItemService.getDoctorProduct(payOrderItem);
			if (list.size() != 0) {
				model.addAttribute("totalUnit", list.get(0).getProductTime());
			}
			payOrderItem.setGetAllTime(null);
		} catch (ShidaoException e) {
			e.printStackTrace();
		}

		return WORKSPACE_PATH + "/doctorservicereport";
	}

	// 客户资料模态框页
	@RequestMapping(value = "/customerInfo/cardInfo")
	public String customerInfoModal(HttpServletRequest request, Model model, @RequestParam String operation,
			@RequestParam ServiceCardType cardType, @RequestParam Integer customerId) {
		model.addAttribute("operation", operation);
		model.addAttribute("customerId", customerId);
		model.addAttribute("cardType", cardType);
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Integer clubId = sm.getClubId();
			SdbsServiceCard condition = new SdbsServiceCard();
			condition.setType(cardType);
			condition.setEnabled(1);
			condition.setClubId(clubId);
			List<SdbsServiceCard> serviceCards = serviceCardService.list(condition);
			if (operation.equals("Upgrade"))
				model.addAttribute("customerCard", customerMembershipService.getCustomerTCM(customerId, clubId));
			model.addAttribute("serviceCardList", serviceCards);
			// 责任医生和销售员
			model.addAttribute("doctors", employeeService.list(clubId, Position.Doctor));
			model.addAttribute("salesEmployees", employeeService.getResponsibleEmployee(clubId));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return WORKSPACE_PATH + "/customerInfo/opening" + cardType.name();
	}

	@RequestMapping(value = "/quit")
	public String quit(HttpServletRequest request) {
		request.getSession().invalidate();
		return WORKSPACE_PATH + "/login";
	}

	@RequestMapping(value = "maizhenyiHistory/list", method = RequestMethod.GET)
	public String maizhenyiHistories(Model model, MzMaizhenyiHistory condition, Integer pageNum, Integer pageSize,
			HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setClubId(sm.getClubId());
			model.addAttribute("result", maizhenyiHistoryService.list(condition, pageNum, pageSize));
			model.addAttribute("condition", condition);
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "/mzyHistoryList";
	}

	// 库存列表
	@RequestMapping(value = "/inventory/list/medicine", method = RequestMethod.GET)
	public String inventoryList(Model model, WmsInventory condition, Integer pageNum, Integer pageSize,
			HttpServletRequest request) {
		try {
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession());
			// 该门店仓库对应的供应商列表
			Integer warehouseId = sManager.getWarehouseId();
			SdbsVendor vendor = new SdbsVendor();
			vendor.setWarehouseId(warehouseId);
			vendor.setDeleted(0);
			List<SdbsVendor> vendors = sdbsVendorService.list(vendor);
			model.addAttribute("vendorList", vendors);
			condition.setWarehouseId(warehouseId);
			if (condition.getProduct() == null) {
				condition.setProduct(new SdbsProduct());
			}
			ListResult<WmsInventory> inventoriers = inventoryService.listSimpleByCondition(condition, pageNum,
					pageSize);
			model.addAttribute("result", inventoriers);
			// 搜索条件
			model.addAttribute("inventory", condition);
			model.addAttribute("requestURI", request.getRequestURL());
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return WORKSPACE_PATH + "inventoryList";
	}

	// 采购列表
	@RequestMapping(value = "/wms_purchase_list", method = RequestMethod.GET)
	public String purchaseOrderList(Model model, WmsPurchaseOrder purchaseOrder, Integer pageNum, Integer pageSize,
			HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			WmsRelationClubWarehouse relationClubWarehouse = new WmsRelationClubWarehouse();
			relationClubWarehouse.setEnabled((byte) 1);
			relationClubWarehouse.setClubId(sm.getClubId());

			purchaseOrder.setRelationClubWarehouse(relationClubWarehouse);
			purchaseOrder.setOrderBys("created_date desc");
			Map<String, Object> purchaseOrderList = purchaseOrderService.listByCondition(purchaseOrder, pageNum,
					pageSize);
			Map<String, String> map = new HashMap<>();
			for (StatusType statusType : StatusType.values()) {
				map.put(statusType.getText(), statusType.name());
			}
			model.addAttribute("statusType", map);
			model.addAllAttributes(purchaseOrderList);
			model.addAttribute("purchaseOrderList", purchaseOrderList.get("list"));
			model.addAttribute("purchaseOrder", purchaseOrder);
			// 仓库列表

			List<WmsRelationClubWarehouse> warehouseList = relationClubWarehouseService.list(relationClubWarehouse);
			model.addAttribute("warehouseList", warehouseList);
			// 该门店仓库对应的供应商列表
			SdbsVendor vendor = new SdbsVendor();
			vendor.setWarehouseId(sm.getWarehouseId());
			vendor.setDeleted(0);
			List<SdbsVendor> vendors = sdbsVendorService.list(vendor);
			model.addAttribute("vendorList", vendors);
		} catch (ShidaoException e) {
			e.printStackTrace();
		}

		return WORKSPACE_PATH + "/wms_purchase_list";
	}

	// 采购详情列表
	@RequestMapping(value = "/wms_purchase", method = RequestMethod.GET)
	public String purchaseOrderDetailList(Model model, WmsPurchaseOrderDetail purchaseOrderDetail, Integer pageNum,
			Integer pageSize) {
		try {
			List<WmsPurchaseOrderDetail> purchaseOrderDetailList = purchaseOrderDetailService.list(purchaseOrderDetail);
			model.addAttribute("purchaseOrderDetailList", purchaseOrderDetailList);
			// 采购单信息
			model.addAttribute("purchaseOrder",
					purchaseOrderService.selectByPrimaryKey(purchaseOrderDetail.getPurchaseOrderId()));
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return WORKSPACE_PATH + "/wms_purchase";
	}

	// 入库列表
	@RequestMapping(value = "/wms_warehouse_voucher", method = RequestMethod.GET)
	public String warehouseVoucherList(Model model, WmsWarehouseVoucher warehouseVoucher, Integer pageNum,
			Integer pageSize, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Integer warehouseId = sm.getWarehouseId();
			// 该门店仓库对应的供应商列表
			SdbsVendor vendor = new SdbsVendor();
			vendor.setWarehouseId(sm.getWarehouseId());
			vendor.setDeleted(0);
			List<SdbsVendor> vendors = sdbsVendorService.list(vendor);
			model.addAttribute("vendorList", vendors);
			warehouseVoucher.setOrderBys("created_date desc");
			warehouseVoucher.setWarehouseId(warehouseId);
			Map<String, Object> warehouseVoucherList = warehouseVoucherService.listByCondition(warehouseVoucher,
					pageNum, pageSize);
			// 审核状态大全
			Map<String, String> map = new HashMap<>();
			for (StatusType statusType : StatusType.values()) {
				map.put(statusType.getText(), statusType.name());
			}
			// 入库类型大全
			model.addAttribute("WarehouseVoucherCategory", WarehouseVoucherCategory.values());

			model.addAttribute("statusType", map);
			model.addAllAttributes(warehouseVoucherList);
			model.addAttribute("warehouseVoucherList", warehouseVoucherList.get("list"));
			model.addAttribute("warehouseVoucher", warehouseVoucher);
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return WORKSPACE_PATH + "/wms_warehouse_voucher";
	}

	@RequestMapping(value = "/wms_warehouse_voucher_detail", method = RequestMethod.GET)
	public String warehouseVoucherDetailList(Model model, WmsWarehouseVoucherDetail warehouseVoucherDetail,
			Integer pageNum, Integer pageSize) {
		try {
			List<WmsWarehouseVoucherDetail> warehouseVoucherDetailList = warehouseVoucherDetailService
					.list(warehouseVoucherDetail);
			// 入库单信息
			WmsWarehouseVoucher key = warehouseVoucherService.selectByPrimaryKey(warehouseVoucherDetail.getOrderId());
			model.addAttribute("warehouseVoucherDetailList", warehouseVoucherDetailList);
			model.addAttribute("warehouseVoucherDetail", warehouseVoucherDetail);
			model.addAttribute("warehouseVoucher", key);
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return WORKSPACE_PATH + "/wms_warehouse_voucher_detail";
	}

	// 仓库列表
	@RequestMapping(value = "wms_warehouse", method = RequestMethod.GET)
	public String relationClubWarehouseList(WmsRelationClubWarehouse relationClubWarehouse, HttpServletRequest request,
			Model model, Integer pageNum, Integer pageSize) {
		try {
			relationClubWarehouse.setEnabled((byte) 1);
			SdcomEmployee key = employeeService
					.selectByPrimaryKey((Integer) request.getSession().getAttribute("employeeId"));
			relationClubWarehouse.setClubId(key.getClubId());
			List<WmsRelationClubWarehouse> warehouseList = relationClubWarehouseService.list(relationClubWarehouse);
			model.addAttribute("warehouseList", warehouseList);
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return WORKSPACE_PATH + "wms_warehouse";
	}

	// 盘货单列表
	@RequestMapping(value = "wms_check_order", method = RequestMethod.GET)
	public String checkOrderList(WmsCheckOrder wmsCheckOrder, HttpServletRequest request, Model model, Integer pageNum,
			Integer pageSize) {
		try {
			// 该门店仓库
			Integer warehouseId = relationClubWarehouseService
					.getWarehouseIdByClubId((Integer) request.getSession().getAttribute("clubId"));

			SdcomEmployee key = employeeService
					.selectByPrimaryKey((Integer) request.getSession().getAttribute("employeeId"));
			WmsRelationClubWarehouse relationClubWarehouse = new WmsRelationClubWarehouse();
			relationClubWarehouse.setEnabled((byte) 1);
			relationClubWarehouse.setClubId(key.getClubId());
			wmsCheckOrder.setRelationClubWarehouse(relationClubWarehouse);
			wmsCheckOrder.setOrderBys("created_date desc");
			wmsCheckOrder.setWarehouseId(warehouseId);
			Map<String, Object> checkOrderList = checkOrderService.listByCondition(wmsCheckOrder, pageNum, pageSize);
			// 仓库列表
			List<WmsRelationClubWarehouse> warehouseList = relationClubWarehouseService.list(relationClubWarehouse);
			model.addAllAttributes(checkOrderList);
			model.addAttribute("checkOrderList", checkOrderList.get("list"));
			model.addAttribute("warehouseList", warehouseList);
			model.addAttribute("wmsCheckOrder", wmsCheckOrder);
			// 盘货状态
			model.addAttribute("CheckStatus", CheckStatus.values());
		} catch (ShidaoException e) {
			e.printStackTrace();
		}

		return WORKSPACE_PATH + "wms_check_order";
	}

	// 盘货单详情列表
	@RequestMapping(value = "wms_check_order_detail", method = RequestMethod.GET)
	public String checkOrderDetailList(WmsCheckOrderDetail wmsCheckOrderDetail, HttpServletRequest request, Model model,
			Integer pageNum, Integer pageSize) {
		try {
			List<WmsCheckOrderDetail> checkOrderDetailList = checkOrderDetailService.list(wmsCheckOrderDetail);
			// 所有的供应商
			model.addAttribute("vendorList", sdbsVendorService.list());
			model.addAttribute("checkOrderDetailList", checkOrderDetailList);
			model.addAttribute("checkOrder",
					checkOrderService.selectByPrimaryKey(wmsCheckOrderDetail.getCheckOrderId()));
			model.addAttribute("wmsCheckOrderDetail", wmsCheckOrderDetail);
		} catch (ShidaoException e) {
			e.printStackTrace();
		}

		return WORKSPACE_PATH + "wms_check_order_detail";
	}

	// 设置
	@RequestMapping(value = "wms_setting", method = RequestMethod.GET)
	public String settingList(Model model, WmsSetting setting) {
		try {
			model.addAttribute("warehouseId", setting.getWarehouseId());
			List<WmsSetting> settingList = settingService.list(setting);
			WmsWarehouse warehouse = new WmsWarehouse();
			warehouse.setEnabled(1);
			model.addAttribute("settingList", settingList);
			model.addAttribute("setting", setting);
			model.addAttribute("category", SettingCategory.values());
			// 所有的仓库
			model.addAttribute("warehouseList", warehouseService.list(warehouse));

		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return WORKSPACE_PATH + "wms_setting";
	}

	// 出库列表
	@RequestMapping(value = "wms_delivery_voucher", method = RequestMethod.GET)
	public String deliveryVoucherList(Model model, WmsDeliveryVoucher deliveryVoucher, Integer pageNum,
			Integer pageSize, HttpServletRequest request) {
		try {

			// 该门店仓库
			Integer warehouseId = relationClubWarehouseService
					.getWarehouseIdByClubId((Integer) request.getSession().getAttribute("clubId"));

			deliveryVoucher.setNotPrescribe(DeliveryVoucherCategory.Prescribe.name());
			deliveryVoucher.setOrderBys("created_date desc");
			deliveryVoucher.setCancelled(0);
			deliveryVoucher.setWarehouseId(warehouseId);
			deliveryVoucher.setCategories(DeliveryVoucherCategory.NoXDF);
			Map<String, Object> deliveryVoucherList = deliveryVoucherService.listByCondition(deliveryVoucher, pageNum,
					pageSize);
			// 仓库列表
			SdcomEmployee key = employeeService
					.selectByPrimaryKey((Integer) request.getSession().getAttribute("employeeId"));
			WmsRelationClubWarehouse relationClubWarehouse = new WmsRelationClubWarehouse();
			relationClubWarehouse.setEnabled((byte) 1);
			relationClubWarehouse.setClubId(key.getClubId());
			List<WmsRelationClubWarehouse> warehouseList = relationClubWarehouseService.list(relationClubWarehouse);
			model.addAttribute("warehouseList", warehouseList);
			// 出库类型
			model.addAttribute("category", DeliveryVoucherCategory.values());
			// 审核状态
			model.addAttribute("status", com.shidao.model.WmsDeliveryVoucher.StatusType.values());
			model.addAllAttributes(deliveryVoucherList);
			model.addAttribute("deliveryVoucherList", deliveryVoucherList.get("list"));
			model.addAttribute("deliveryVoucher", deliveryVoucher);
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return WORKSPACE_PATH + "wms_delivery_voucher";
	}

	// 出库列表详情
	@RequestMapping(value = "wms_delivery_voucher_detail", method = RequestMethod.GET)
	public String deliveryVoucherDetailList(Model model, WmsDeliveryVoucherDetail deliveryVoucherDetail) {
		try {
			BigDecimal totalDeliveryPrice = BigDecimal.ZERO;
			List<WmsDeliveryVoucherDetail> deliveryVoucherDetailList = deliveryVoucherDetailService
					.list(deliveryVoucherDetail);
			if (deliveryVoucherDetailList != null && !deliveryVoucherDetailList.isEmpty()) {
				WmsDeliveryVoucher key = deliveryVoucherService
						.selectByPrimaryKey(deliveryVoucherDetailList.get(0).getDeliveryVoucherId());
				totalDeliveryPrice = key.getTotal();
			}
			model.addAttribute("deliveryVoucherDetailList", deliveryVoucherDetailList);
			model.addAttribute("deliveryVoucherDetail", deliveryVoucherDetail);
			model.addAttribute("deliveryVoucher",
					deliveryVoucherService.selectByPrimaryKey(deliveryVoucherDetail.getDeliveryVoucherId()));
			model.addAttribute("totalDeliveryPrice", totalDeliveryPrice);
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return WORKSPACE_PATH + "wms_delivery_voucher_detail";
	}

	// 处方打印：客户管理搜索tcm_medical 为主
	@RequestMapping(value = "prescription/list", method = RequestMethod.GET)
	public String mdedicalRecordList(Model model, TcmMedicalRecord condition, Integer pageNum, Integer pageSize,
			HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());

			if (condition.getAppointment() == null) {
				condition.setAppointment(new SdbsAppointment());
			}

			if (sm.getPositionEnum() == Position.PharmacyManager || sm.getPositionEnum() == Position.Pharmacy) {
				condition.setWarehouseId(sm.getWarehouseId());
			}
			condition.getAppointment().setClubId(sm.getClubId());
			condition.setStatus(MedicalRecordStatus.Completed);
			// 存在药方
			condition.addCategoriesParams(MRPrescriptionCategory.medicine);
			model.addAttribute("employees", employeeService.list(sm.getClubId(), Position.Doctor));
			model.addAttribute("result", tcmmedicalrecordService.listPrescription(condition, pageNum, pageSize));
			model.addAttribute("condition", condition);
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e);
		}
		return WORKSPACE_PATH + "prescriptionList";
	}

	@RequestMapping(value = "sdcom_club", method = RequestMethod.GET)
	public String clubList(Model model, SdcomClub club, Integer pageSize, Integer pageNum, HttpServletRequest request) {
		try {
			// club.setType(ClubType.SubPartner);
			club.setEnabled(1);
			Map<String, Object> map = clubService.listByCondition(club, pageNum, pageSize);
			model.addAttribute("clubList", map.get("list"));
			model.addAllAttributes(map);
			// 门店类型
			model.addAttribute("clubType", ClubType.values());
			model.addAttribute("club", club);
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return WORKSPACE_PATH + "branchManagementList";
	}

	// 本分支机构详情
	@RequestMapping(value = "/myClubDetail", method = RequestMethod.GET)
	public String clubDetailList(Model model, HttpServletRequest request) {
		Integer clubId = null;
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			clubId = sm.getClubId();
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
//		return WORKSPACE_PATH + "branchManagementEdit";
		return "redirect:/centerAdmin/club/detail?id=" + clubId;
	}

	// 点击显示使用详细列表
	@RequestMapping(value = "sdcom_club_operation_history", method = RequestMethod.GET)
	public String operationHistory(Model model, String uuid) {
		try {
			if (uuid == null) {
				model.addAttribute("clubType", ClubType.values());
				return WORKSPACE_PATH + "branchManagementEdit";
			}
			SdcomClub club = new SdcomClub();
			club.setUuid(uuid);
			List<SdcomClub> list = clubService.list(club);
			SdcomClubMembership clubMembership = new SdcomClubMembership();
			clubMembership.setClubId(list.get(0).getId());
			clubMembership.setEnabled(1);
			List<SdcomClubMembership> clubDetailList = membershipService.list(clubMembership);

			model.addAttribute("clubInfo", list.get(0));
			model.addAttribute("clubDetailList", clubDetailList);
			// 操作详情
			model.addAttribute("operationCategory", OperationCategory.values());
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return WORKSPACE_PATH + "branchManagementEdit";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月7日 功能:当日顾客所有结算列表
	 * @param model
	 * @param pageNum
	 * @param pageSize
	 * @param customerName
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/salesorders/list/{date}", method = RequestMethod.GET)
	public String todaySalesOrders(HttpServletRequest request, Model model, @PathVariable(name = "date") String date,
			Integer pageNum, Integer pageSize, SalesOrderVO condition) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (date.equals("today"))
				condition.setDate(new Date());
			condition.setClubId(sm.getClubId());
			condition.addParameter("categories", SalesOrderCategory.allProduct);
			model.addAttribute("condition", condition);
			model.addAttribute("result", salesOrderService.listSalesOrder(condition, pageNum, pageSize));
			model.addAttribute("date", date);
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e);
		}
		return WORKSPACE_PATH + "salesOrderList";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月2日 功能:结算详情
	 * @param customerUUID
	 * @param model
	 * @param date
	 * @return
	 */
	@RequestMapping(value = "/salesorders/{customerUUID}", method = RequestMethod.GET)
	public String salesorders4Customer(HttpServletRequest request, Model model,
			@PathVariable("customerUUID") String customerUUID,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
			String membershipUuid, String anotherMembershipUuid, Integer settling, Boolean useMembership) {
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			model.addAttribute("useMembership", useMembership);
			Integer clubId = employeeSessionManager.getClubId();
			if (date == null)
				date = LocalDate.now();
			LocalDate today = LocalDate.now();
			salesOrderService.applyMembership(customerUUID, DateUtil.LocalDateToUdate(date), membershipUuid, clubId,
					useMembership);
			// 0. 保留参数
			model.addAttribute("customerUUID", customerUUID);
			model.addAttribute("orderDate", date);
			// 1. 获取消费人信息
			// 如果没有指定客户编号，则寻找自己是否有会员卡
			// 2 .获取卡主信息
			SalesOrder4CustomerInformation so4cInfor = new SalesOrder4CustomerInformation(customerUUID, membershipUuid,
					clubId, anotherMembershipUuid, useMembership);
			model.addAllAttributes(so4cInfor.getInformationMap());
			
			// 3. 获取订单信息
			final LocalDate finalDate = date;
			CompletableFuture<Map<String, Object>> salesorders4CustomerFuture = CompletableFuture.supplyAsync(() -> {
				try {
					return salesOrderItemService.salesorders4Customer(
							so4cInfor.getPayInformation().getCustomer().getId(), DateUtil.LocalDateToUdate(finalDate),
							clubId);
				} catch (ShidaoException e) {
					model.addAttribute("errorMsg", e);
				}
				return null;
			});
			SdbsSalesOrder soCondition = new SdbsSalesOrder();
			soCondition.setClubId(clubId);
			soCondition.setCustomerId(so4cInfor.getPayInformation().getCustomer().getId());
			soCondition.setCreatedDate(date);
			model.addAttribute("cards", salesOrderService.listCardRelated4Confirmation(soCondition));
			model.addAttribute("summary", salesOrderService.getSalesOrdersSummary(soCondition));
			// 4. 获取病历相关信息

			// 5. 获取界面列表信息
			model.addAttribute("validServiceCards", ClubSetting.getSetting(clubId).getValidServiceCards());
			model.addAttribute("categorys", SalesOrderCategory.notMedicineAndDoctorFee);
			model.addAttribute("products", sdbsProductService.getProductOfCategory(clubId));
			// 客户来源
			SdbsCustomerSource customerSource = customerSourceService.selectByCustomerUuid(customerUUID, clubId);
			model.addAttribute("customerSource", customerSource);
			if (customerSource != null)
				model.addAttribute("isFirstVisit",
						customerSourceService.isFirstVisit(customerSource.getFirstVisitDate(), date));
			// 快递是否必须
			ClubSetting clubTypeSetting = ClubSetting.getSetting(employeeSessionManager.getClubId());
			model.addAttribute("requestKuaidi", clubTypeSetting.getRequestKuaidi());
			// 操作按钮
			model.addAttribute("canAdd", date.isEqual(today));
			model.addAttribute("canEdit", date.isEqual(today)
					&& customerService.isFirstVisit(customerUUID, date));

			model.addAttribute("settling", settling);
			if (settling != null && settling == 1) {
				model.addAttribute("isPay", 1);
				SdbsSalesOrder salesOrder = new SdbsSalesOrder();
				salesOrder.addCategoriesParams(SalesOrderCategory.allProduct);
				if (StringUtil.isNullOrEmpty(salesOrderService.getSalesOrderIds(salesOrder))) {
					model.addAttribute("settlementStatus", "starting");
				}
			}
			model.addAllAttributes(serviceCardService.getHaveTCMLocalAndChain(clubId));
			model.addAttribute("isToday", date.isEqual(LocalDate.now()));
			try {
				model.addAllAttributes(salesorders4CustomerFuture.get());
			} catch (Exception e) {
				model.addAttribute("errorMsg", e);
			}
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e);
		}
		return WORKSPACE_PATH + "salesordersOfCustomer";
//		return WORKSPACE_PATH + "salesorders4Customer";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月21日 功能:快递列表
	 * @param customerName
	 * @param model
	 * @param date
	 * @param request
	 * @param status
	 * @return
	 */
	@RequestMapping(value = "/delivery/list", method = RequestMethod.GET)
	public String deliveryList(String customerName, Model model, HttpServletRequest request, DeliveryStatus status,
			Integer pageNum, Integer pageSize) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (status == null)
				status = DeliveryStatus.DeliveryRequested;
			Integer warehouseId = null;
			Integer clubId = null;
			Boolean haveDelivery = false;
			if (sm.getPosition().equals("药房主管")) {
				warehouseId = sm.getWarehouseId();
				haveDelivery = true;
			} else
				clubId = sm.getClubId();
			model.addAllAttributes(
					deliveryService.deliverySettlement(customerName, clubId, warehouseId, status, pageNum, pageSize));
			model.addAttribute("deliveryStatus", DeliveryStatus.noCreated);
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("customerName", customerName);
			model.addAttribute("status", status);
			model.addAttribute("haveDelivery", haveDelivery);
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e);
		}
		return WORKSPACE_PATH + "deliveryList";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月8日 功能:结算/打印模态框
	 * @param isSettlement
	 * @param customerUUID
	 * @param model
	 * @param date
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/settlementModalBox/{type}/{customerUUID}", method = RequestMethod.GET)
	public String settlementModalbox(@PathVariable("type") SalesOrder4CustomerOperation type,
			@PathVariable("customerUUID") String customerUUID, Model model, Date date, String membershipUuid,
			Boolean useMembership, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Integer clubId = sm.getClubId();
			// save parameters
			model.addAttribute("customerUUID", customerUUID);
			model.addAttribute("date", date);
			model.addAttribute("type", type);
			// 客户信息
			SdbsCustomer customer = customerService.selectByUuid(customerUUID);
			model.addAttribute("customerName", customer.getName());
			// 订单列表
			SdbsSalesOrder salesOrder = new SdbsSalesOrder();
			salesOrder.setCustomerId(customer.getId());
			salesOrder.setCreatedDate(date);
			salesOrder.setClubId(sm.getClubId());
			if (type != SalesOrder4CustomerOperation.confirmation) {
				salesOrder.setPayed(type == SalesOrder4CustomerOperation.settlement ? false : true);
			}
			salesOrder.addCategoriesParams(SalesOrderCategory.allProduct);
			TimeRecord tRecord = TimeRecord.start();
			tRecord.record("Begin");
			List<SdbsSalesOrder> list = salesOrderService.listSalesOrderAboutDelivery(salesOrder);
			tRecord.record("Done");
			tRecord.show();
			model.addAttribute("list", list);
			// get medicine Ids

			// 结算
			switch (type) {
			case settlement:
				SalesOrder4CustomerInformation so4cInfo = new SalesOrder4CustomerInformation(customerUUID,
						membershipUuid, clubId, null, useMembership);
				model.addAttribute("payInfor", so4cInfo.getPayInformation());
				String salesOrderIds = salesOrderService.getSalesOrderIds(salesOrder);
				// 判断是否是会中医合作店，如果是则返回另一个页面并输出快递费
				ClubSetting clubTypeSetting = ClubSetting.getSetting(sm.getClubId());
				if (clubTypeSetting.getRequestKuaidi()) {
					salesOrder.setAppointmentId(0);
					String medicineIds = salesOrderService.getSalesOrderIds(salesOrder);
					Integer deliveryId = deliveryService.getDeliveryIdRelation(null, medicineIds);
					model.addAttribute("haveDelivery", deliveryId == null ? false : true);
					if (deliveryId != null) {
						String orderIds = deliveryService.selectByPrimaryKey(deliveryId).getSalesOrderIds();
						SdbsSalesOrder condition = new SdbsSalesOrder();
						condition.setSalesOrderIds(orderIds);
						condition.setCategory(SalesOrderCategory.DeliveryFee);
						String deliverySalesOrderIds = salesOrderService.getSalesOrderIds(condition);
						SdbsSalesOrder order = new SdbsSalesOrder();
						order.setSalesOrderIds(deliverySalesOrderIds);
						list.addAll(salesOrderService.listSalesOrderAboutDelivery(order));
					}
					return WORKSPACE_PATH + "modal/settlementModalBox4HZY";
				}

				// 该门店的除会员卡的支付方式
				SdepPayCategory payCategory = new SdepPayCategory();
				payCategory.setClubId(clubId);
				payCategory.addParameter("supportPayTypes",
						Arrays.asList(SupportPayType.Nonmembership, SupportPayType.Both));
				payCategory.addParameter("enabled", 1);
				List<String> validPayMethods4NonMemberships = new ArrayList<String>();
				List<String> validPayMethods4Generals = new ArrayList<String>();
				payCategoryService.list(payCategory).forEach(p -> {
					p.getMethods().forEach(m -> {
						switch (p.getSupportPayType()) {
						case Nonmembership:
							validPayMethods4NonMemberships.add(m.getName());
							break;
						case Both:
							validPayMethods4Generals.add(m.getName());
							break;
						default:
							break;
						}
					});
				});
				if (so4cInfo.getPayInformation().getPayMethod() == PayMethod.valueOf("现金")) {
					model.addAttribute("validPayMethods4NonMembership", validPayMethods4NonMemberships);
				}
				// 免费和赠送
				model.addAttribute("validPayMethods4General", validPayMethods4Generals);
				model.addAttribute("total", salesOrderService.calculateAmount(salesOrderIds));
				model.addAttribute("jfAmount", salesOrderService.getJfAmount(salesOrderIds));
				break;
			case confirmation:
				SdbsSalesOrder condition = new SdbsSalesOrder();
				condition.setCustomerId(customer.getId());
				condition.setCreatedDate(date);
				condition.setClubId(clubId);
				model.addAttribute("cards", salesOrderService.listCardRelated4Confirmation(condition));
				break;

			default:
				break;
			}

		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e);
		}
		return WORKSPACE_PATH + "modal/settlementModalBox";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月16日 功能:今日结算的快递单
	 * @param customerUUID
	 * @param date
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deliveryModalBox/{customerUUID}", method = RequestMethod.GET)
	public String deliveryModalBox(@PathVariable("customerUUID") String customerUUID, Date date, Model model,
			HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (date == null)
				throw new ShidaoException("请输入日期");
			Integer customerId = customerService.selectByUuid(customerUUID).getId();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			model.addAttribute("date", format.format(date));
			model.addAttribute("customerUUID", customerUUID);
			ClubSetting clubTypeSetting = ClubSetting.getSetting(sm.getClubId());
			if (clubTypeSetting.getRequestKuaidi()) {
				SdbsSalesOrder salesOrder = new SdbsSalesOrder();
				salesOrder.setCustomerId(customerId);
				salesOrder.setCreatedDate(date);
				salesOrder.addCategoriesParams(SalesOrderCategory.notDoctorFee);
				salesOrder.setClubId(sm.getClubId());
				String salesOrderIds = salesOrderService.getSalesOrderIds(salesOrder);
				model.addAttribute("medicines", salesOrderItemService.listGroupMedicine(salesOrderIds));
				model.addAttribute("salesOrderIds", salesOrderIds);
				salesOrder.addCategoriesParams(SalesOrderCategory.medicine);
				String medicines = salesOrderService.getSalesOrderIds(salesOrder);
				SdbsSalesOrderItem salesOrderItem = new SdbsSalesOrderItem();
				salesOrderItem.setSalesOrderIds(medicines);
				List<SdbsSalesOrderItem> list = salesOrderItemService.list(salesOrderItem);
				model.addAttribute("medicineWeight",
						StreamUtil.sumBigDecimalOfObj(list, SdbsSalesOrderItem::getQuantity)
								.multiply(new BigDecimal("1.5")).setScale(2, BigDecimal.ROUND_HALF_UP));
				// list.stream().mapToDouble(a->a.getQuantity()).sum()*1.5));
				return WORKSPACE_PATH + "modal/deliveryModalBox4HZY";
			}
			model.addAllAttributes(deliveryService.deliverySettlement(customerId, sm.getClubId(), date, true));
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e);
		}
		return WORKSPACE_PATH + "modal/deliveryModalBox";
	}

	@RequestMapping(value = "/cloudClinic/payDemandNote/{appointmentId}", method = RequestMethod.GET)
	public String cloudClinicPayDemandNote(HttpServletRequest request, Model model,
			@PathVariable(value = "appointmentId") Integer appointmentId) {
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			SdbsSalesOrder salesOrderCondition = new SdbsSalesOrder();
			salesOrderCondition.setAppointmentId(appointmentId);
			List<SdbsSalesOrder> salesOrders = salesOrderService.list(salesOrderCondition);
			if (salesOrders == null || salesOrders.isEmpty()) {
				throw new ShidaoException("订单不存在");
			}
			Integer clubId = employeeSessionManager.getClubId();
			Integer salesOrderId = salesOrders.get(0).getId();
			// 基本信息
			Map<String, Object> map = salesOrderItemService.getMainCustomerInfoAndPayMethod(salesOrderId, clubId, null);
			// 药费列表
			model.addAttribute("medicineList", salesOrderItemService.listMedicineTotal(salesOrderId, 1));
			// 得到该订单里理疗列表自营产品的列表和诊费
//			model.addAllAttributes(salesOrderItemService.listProductBySalesOrderId(salesOrderId));
			model.addAllAttributes(map);
			model.addAllAttributes(salesOrderItemService.getAddFee(salesOrderId));
			model.addAttribute("dispenseStatus", salesOrderService.getDispenseStatus(salesOrderId));
			// model.addAttribute("cloudClinicPayMethod", PayMethod.CloudClinicPayMethod);
			model.addAttribute("delivery", salesOrderService.selectDeliveryBySalesOrderId(salesOrderId));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return WORKSPACE_PATH + "cloudClinic/settlement";
	}

	// 代付
	@RequestMapping(value = "/customer/daifus", method = RequestMethod.GET)
	public String customerDaifus(Model model, Integer salesOrderId, String customerUUID, Date date) {
		model.addAttribute("salesOrderId", salesOrderId);
		model.addAttribute("customerUUID", customerUUID);
		model.addAttribute("date", date);
		return WORKSPACE_PATH + "daifuList";
	}

	// 医生统计里的会员统计
	@RequestMapping(value = "/doctor/serviceCardStatistics", method = RequestMethod.GET)
	public String doctorServiceCardStatistics(Model model, Integer doctorId) {
		model.addAttribute("info", payOrderItemService.listserviceCardStatisticsByDoctorId(doctorId));
		return WORKSPACE_PATH + "/";
	}

	// V空间信息查看
	// 查看客户信息
	@RequestMapping(value = "/list/customer/info", method = RequestMethod.GET)
	public String listCustomerInfo(Model model, Integer customerId) {
		model.addAttribute("customerInfo", wsMessageService.listCustomerInfo(customerId));
		return WORKSPACE_PATH + "/";
	}

	// 查看客户未查看的客户系统信息
	@RequestMapping(value = "/list/customer/notice", method = RequestMethod.GET)
	public String listCustomerNotice(Model model, Integer customerId) {
		CrmCustomerNotice customerNotice = new CrmCustomerNotice();
		customerNotice.setCustomerId(customerId);
		try {
			model.addAttribute("info", customerNoticeService.list(customerNotice));
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return WORKSPACE_PATH + "/";
	}

	// 仓储系统
	@RequestMapping(value = "/wms_warehouse/edit_warehouse", method = RequestMethod.GET)
	public String wmsWarehouseEdit(HttpServletRequest request, Model model, Integer warehouseId) {
		try {
			if (warehouseId != null) {
				// 所有的供应商
				model.addAttribute("vendorList", sdbsVendorService.list());
				model.addAttribute("warehouse", warehouseService.selectByPrimaryKey(warehouseId));
				SdbsVendor vendor = new SdbsVendor();
				vendor.setWarehouseId(warehouseId);
				vendor.setDeleted(0);
				model.addAttribute("haveVendorList", sdbsVendorService.list(vendor));
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "/wms_warehouse/edit_warehouse";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年9月6日
	 * @param uuid
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/pharmacy/Details/{uuid}", method = RequestMethod.GET)
	public String pharmacyDetails(@PathVariable(value = "uuid") String uuid, Model model, HttpServletRequest request) {
		try {
			model.addAttribute("result", deliveryVoucherService.getPrescriptionDetails(uuid));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "pharmacyDetails";
	}

	// 根据日期范围、供应商、草药编码(开始编码、结束编码)，查询产品的期初、入库、出库、期末信息
	@RequestMapping(value = "/financeInventory", method = RequestMethod.GET)
	public String financeInventory(Model model, WmsFinancialStatement condition, Date startDate, Date endDate,
			Integer pageNum, Integer pageSize, HttpServletRequest request) {
		try {
			// 该门店仓库对应的供应商列表
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession());
			Integer warehouseId = sManager.getWarehouseId();
			SdbsVendor vendor = new SdbsVendor();
			vendor.setWarehouseId(warehouseId);
			vendor.setDeleted(0);
			List<SdbsVendor> vendors = sdbsVendorService.list(vendor);
			model.addAttribute("vendorList", vendors);
			model.addAttribute("startDate", startDate);
			model.addAttribute("endDate", endDate);
			model.addAttribute("condition", condition);
			if (startDate == null || endDate == null) {
				return WORKSPACE_PATH + "financeInventory";
			}
			if (endDate.before(startDate)) {
				throw new ShidaoException("结束日期不能大约开始日期");
			}
			condition.setDateInterval(startDate + "---" + endDate);
			condition.setOrderBys("medicine_code asc");
			Map<String, Object> map = new HashMap<>();
			map = financialStatementService.listByCondition(condition, pageNum, pageSize);
			if ((Integer) map.get("currentCount") == 0) {
				financialStatementService.insertFinanceInventory(startDate, endDate, condition.getCategory());
				map = financialStatementService.listByCondition(condition, pageNum, pageSize);
			}
			WmsFinancialStatement total = financialStatementService.getTotal(condition);
			model.addAttribute("allStartTotal", com.shidao.util.StringUtil.getBigDecimal(total.getAllStartTotal()));
			model.addAttribute("allEndTotal", com.shidao.util.StringUtil.getBigDecimal(total.getAllEndTotal()));
			model.addAllAttributes(map);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "financeInventory";
	}

	// 根据productId ，输出该药所选择日期段所有进出库情况
	@RequestMapping(value = "/financeInventory/product/{productId}", method = RequestMethod.GET)
	public String financeInventoryDetail(Model model, @PathVariable(value = "productId") Integer id,
			HttpServletRequest request) {
		try {
			Date startDate = (Date) request.getSession().getAttribute("startDate");
			Date endDate = (Date) request.getSession().getAttribute("endDate");
			Map<String, Object> result = JsonUtil.succeedJson();
			result.put("list", warehouseVoucherDetailService.getWarehouseDeliveryDetailList(id, startDate, endDate));
			model.addAllAttributes(result);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "financeInventory";
	}

	// 根据批次，输出该批次所有进出货详情
	@RequestMapping(value = "/financeInventory/turnover/{batchNumber}", method = RequestMethod.GET)
	public String financeInventoryDetail(Model model, @PathVariable(value = "batchNumber") String batchNumber,
			String type) {
		try {
			Map<String, Object> result = JsonUtil.succeedJson();
			result.put("type", type);
			if ("入库".equals(type))
				result.put("list",
						warehouseVoucherDetailService.getWarehouseVoucherDetailListByBatchNumber(batchNumber));
			if ("出库".equals(type))
				result.put("list", deliveryVoucherDetailService.getDeliveryDetailListByBatchNumber(batchNumber));
			model.addAllAttributes(result);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "financeInventory";
	}

	@RequestMapping(value = "/customerInfo/recharge/{id}")
	public String getRecharge(@PathVariable(value = "id") Integer id, Model model, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Integer clubId = sm.getClubId();
			// 责任医生和销售员
			model.addAttribute("doctors", employeeService.list(clubId, Position.Doctor));
			model.addAttribute("salesEmployees", employeeService.getResponsibleEmployee(clubId));
			model.addAttribute("id", id);
		} catch (Exception e) {
			model.addAttribute("errMessage", e.getMessage());
		}
		return WORKSPACE_PATH + "/customerInfo/recharge";
	}

	/**
	 * 处方提醒列表
	 * 
	 * @param request
	 * @param condition
	 * @param model
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/medicalRemind/list")
	public String medicalRemindList(HttpServletRequest request, SdbsCustomerCare condition, Model model,
			Integer pageNum, Integer pageSize) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setCarerId(sm.getEmployeeId());
			SdbsAppointment appointment = new SdbsAppointment();
			appointment.setClubId(sm.getClubId());
			condition.setAppointment(appointment);
			condition.setType(CustomerCareType.MedicalRemind);
			condition.setFeedback("未提醒");
			model.addAttribute("condition", condition);
			model.addAttribute("medicalRemindList",
					customerCareService.listMedicalRemind(condition, pageNum, pageSize));
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return WORKSPACE_PATH + "/medicalRemindList";
	}

	// 看诊系统里的咨询列表
	@RequestMapping(value = "/consult/list")
	public String consultList(SdepConsultation condition, Model model, Integer pageNum, Integer pageSize,
			HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			ListResult<SdepConsultation> result = null;
			String type = condition.getType();
			if (StringUtil.isNullOrEmpty(type))
				throw new ShidaoException("没有权限查看!");
			switch (type) {
			case "本人咨询管理":
				condition.setEmployeeId(sm.getEmployeeId());
				result = consultationService.listConsultAboutEmployee(condition, pageNum, pageSize);
				break;
			case "本店咨询管理":
				condition.setClubId(sm.getClubId());
				result = consultationService.listConsultAboutClub(condition, pageNum, pageSize);
				break;
			default:
				break;
			}
			model.addAttribute("condition", condition);
			model.addAttribute("result", result);
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "/consultManageList";
	}

	// 咨询管理详情
	@RequestMapping(value = "/consult/detail/{customerId}/{employeeId}", method = RequestMethod.GET)
	public String consultDetail(@PathVariable("customerId") Integer customerId,
			@PathVariable("employeeId") Integer employeeId, Model model, Integer pageNum, Integer pageSize,
			HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("customerId", customerId);
			model.addAttribute("customer", customerService.selectByPrimaryKey(customerId));
			if (!sm.getEmployeeId().equals(employeeId)) {
				model.addAttribute("employee", employeeService.selectByPrimaryKey(employeeId));
			}
			model.addAttribute("result",
					consultationService.getDetailAboutEmployee(customerId, employeeId, pageNum, -1));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "consultManageDetail";
	}

	@RequestMapping(value = "/cloudClinic/payment/list", method = RequestMethod.GET)
	public String getYunClinicPayment(Model model, Integer pageNum, Integer pageSize, PayMethod payMethod,
			HttpServletRequest request) {
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			model.addAttribute("paymentList", salesOrderService.getYunClinicPayment(pageNum, pageSize, payMethod,
					employeeSessionManager.getClubId()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}

		return WORKSPACE_PATH + "cloudClinicPaymentList";
	}

	@RequestMapping(value = "yunClinicYBPay/{customerId}", method = RequestMethod.GET)
	public String getYunClinicYBPay(@PathVariable(value = "customerId") Integer customerId, Model model) {
		try {
			SdbsCustomer customer = customerService.selectByPrimaryKey(customerId);
			model.addAttribute("customer", customer);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "yunClinicYBPay";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年6月12日 功能:点击退卡，弹出模态框显示卡项信息
	 * @param cardId
	 * @param model
	 * @return
	 */
	@GetMapping("/refundcard/{cardId}")
	public String refundcard(@PathVariable(value = "cardId") Integer cardId, Model model) {
		try {
			model.addAttribute("cardInfo", customerMembershipService.selectByPrimaryKey(cardId));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "refundcardModal";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年9月6日 功能:客户基本信息
	 * @param uuid
	 * @param model
	 * @return
	 */
	@GetMapping("/customer/simpleEditor/{uuid}")
	public String customerSimpleEditor(@PathVariable(value = "uuid") String uuid, Model model) {
		try {
			model.addAttribute("customer", customerService.selectByUuid(uuid));
			// 职业列表
			model.addAttribute("profession", professioService.list());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "customerSimpleEditor";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年9月7日
	 * @param model
	 * @return
	 */
	@GetMapping("/prescription/AI")
	public String prescriptionAi(Model model, String pulses, Integer medicalRecordId,
			PDFPrescriptionCategory pdfCategory) {
		String handlePulses = tcmmedicalrecordService.handlePulses(pulses);
		YunmaiUI yunmaiUI = new YunmaiUI(handlePulses);
		yunmaiUI.setPulse(handlePulses);
		model.addAttribute("medicalRecordId", medicalRecordId);
		model.addAttribute("pdfCategory", pdfCategory);
		model.addAttribute("yunmaiUI", yunmaiUI);
		if (!StringUtil.isNullOrEmpty(handlePulses))
			model.addAttribute("pulses", Arrays.asList(handlePulses.split(",")));
		return WORKSPACE_PATH + "prescriptionAI";
	}

	/**
	 * @author 创建人:liupengyuan<br>
	 *         时间:2018年12月7日<br>
	 *         功能:获取该门店该医生本日各个时间段的顾客信息
	 * @param model
	 * @param date
	 * @param request
	 * @return
	 */
	@GetMapping("/doctor/work/detail")
	public String doctorDaily(Model model,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Integer clubId,
			String clubName, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Map<String, Object> condition = new HashMap<>();
			date = date == null ? LocalDate.now() : date;
			List<SdbsPeriod> doctorDaily = appointmentService.getDoctorDaily(sm.getEmployeeId(), date, clubId);
			model.addAttribute("doctorDailies", doctorDaily);
			model.addAttribute("customerNum",
					doctorDaily.stream().filter(a -> a.getAppointments() != null && !a.getAppointments().isEmpty())
							.map(SdbsPeriod::getAppointments).collect(Collectors.toList()).size());
			condition.put("date", date);
			condition.put("clubName", clubName);
			condition.put("clubId", clubId);
			model.addAttribute("condition", condition);
			model.addAttribute("isToday", date.isEqual(LocalDate.now()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "doctorWorkDetail";
	}

	/**
	 * @author 创建人:liupengyuan<br>
	 *         时间:2018年12月7日<br>
	 *         功能:该月各个天所预约的人数
	 * @param model
	 * @param monthDate
	 * @param request
	 * @return
	 */
	@GetMapping("/doctor/works")
	public String doctorWorks(Model model, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAllAttributes(appointmentService.getDoctorWorks(sm.getEmployeeId()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "doctorWorks";
	}

	/**
	 * 按结算方式查看结算单
	 * 
	 * @param request
	 * @param model   @return, Created at 2019年8月27日
	 * @auto yzl
	 *
	 */
	@GetMapping("/salesorder/listByCategory")
	public String getSalesOrderListByPayCategory(HttpServletRequest request, Model model, SdbsSalesOrder condition) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (sm.hasPermission(PermissionEnum.ClubTypeManagement)) {
				model.addAttribute("clubIds", clubService.list(sm.getClubType()));
			} else {
				condition.setClubId(sm.getClubId());
			}
			model.addAttribute("condition", condition);
			if (condition.hasDatePeriodOfListParameters()) {
				model.addAttribute("salesOrders", salesOrderService.listByPayCategory(condition));
			}
			SdepPayCategory pCategory = new SdepPayCategory().setClubId(sm.getClubId())
					.setName(condition.getListParameterValue("payCategory"));
			SdepPayMethod payMethod = new SdepPayMethod().setPayCategory(pCategory).setEnabled(true);

			model.addAttribute("payMethods", payMethodService.list(payMethod));
			pCategory.setName(PayCategory.现金流);
			model.addAttribute("cashPayMethods", payMethodService.list(payMethod));

		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_PATH + "/salesOrdersOfPayMethod";
	}

}
