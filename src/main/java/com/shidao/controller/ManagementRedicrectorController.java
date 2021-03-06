package com.shidao.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.shidao.enums.IncomeProjectCategory;
import com.shidao.enums.MembershipOperation;
import com.shidao.enums.Position;
import com.shidao.enums.ProductCategory;
import com.shidao.enums.SalesOrderCategory;
import com.shidao.enums.ServiceCardType;
import com.shidao.enums.SupportPayType;
import com.shidao.model.SdbsAppointment;
import com.shidao.model.SdbsCustomer;
import com.shidao.model.SdbsCustomerMembership;
import com.shidao.model.SdbsCustomerServiceCardChangeHistory;
import com.shidao.model.SdbsCustomerServiceCardChangeHistory.CustomerServiceCardChangeHistoryCategory;
import com.shidao.model.SdbsProduct;
import com.shidao.model.SdbsProfession;
import com.shidao.model.SdbsServiceCard;
import com.shidao.model.SdcomEmployee;
import com.shidao.model.SdepDrawSetting;
import com.shidao.model.SdepPayCategory;
import com.shidao.model.SdepPayMethod;
import com.shidao.model.TcmMedicalRecord;
import com.shidao.service.ManagementService;
import com.shidao.service.MzMaizhenyiHistoryService;
import com.shidao.service.SdbsAppointmentService;
import com.shidao.service.SdbsCustomerCareService;
import com.shidao.service.SdbsCustomerMembershipService;
import com.shidao.service.SdbsCustomerService;
import com.shidao.service.SdbsCustomerServiceCardChangeHistoryService;
import com.shidao.service.SdbsCustomerServiceGroupService;
import com.shidao.service.SdbsCustomerSourceService;
import com.shidao.service.SdbsPayOrderService;
import com.shidao.service.SdbsProductService;
import com.shidao.service.SdbsProfessioService;
import com.shidao.service.SdbsServiceCardService;
import com.shidao.service.SdcomClubService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.service.SdepDrawSettingService;
import com.shidao.service.SdepPayCategoryService;
import com.shidao.service.SdepPayMethodService;
import com.shidao.service.TcmMedicalRecordService;
import com.shidao.setting.ClubSetting;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.ListResult;
import com.shidao.util.PermissionEnum;
import com.shidao.util.ShidaoException;
import com.shidao.util.StringUtil;
import com.shidao.util.ValidList;
import com.shidao.vo.CustomerActivityVO;

@Controller
@RequestMapping("/workspace/management")
public class ManagementRedicrectorController extends BaseController {
	private static final String MANAGEMENT_PATH = "/display/management/";

	@Autowired
	private SdcomEmployeeService employeeService;

	@Autowired
	private SdbsProductService sdbsProductService;

	@Autowired
	private ManagementService managementService;

	@Autowired
	private TcmMedicalRecordService medicalRecordService;

	@Autowired
	private SdbsCustomerService customerService;

	@Autowired
	private SdbsServiceCardService serviceCardService;

	@Autowired
	private SdbsPayOrderService payOrderService;

	@Autowired
	private SdbsCustomerMembershipService customerMembershipService;

	@Autowired
	private MzMaizhenyiHistoryService maizhenyiHistoryService;

	@Autowired
	private SdbsProfessioService professioService;

	@Autowired
	SdbsCustomerCareService customerCareService;
	
	@Autowired
	private SdbsCustomerServiceGroupService customerServiceGroupService;
	
	@Autowired
	private SdbsCustomerServiceCardChangeHistoryService customerServiceCardChangeHistoryService;
	
	@Autowired
	private SdbsCustomerSourceService customerSourceService;
	
	@Autowired
	private SdcomClubService clubService;
	
	@Autowired
	private SdbsAppointmentService appointmentService;
	
	@Autowired
	private SdepPayMethodService payMethodService;
	
	@Autowired
	private SdepPayCategoryService payCategoryService;
	
	@Autowired
	SdepDrawSettingService drawSettingService;

	/* Employee------ ----------------------------------- */
	/**
	 * 员工管理
	 * @author yzl 2018年6月21日
	 * @param request
	 * @param model
	 * @param condition
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/employee/list", method = RequestMethod.GET)
	public String employees(HttpServletRequest request, Model model, SdcomEmployee condition, Integer pageNum,
			Integer pageSize) {
		model.addAttribute("condition", condition);
		model.addAttribute("requestURI",request.getRequestURI());
		try {
			EmployeeSessionManager eSessionManager = new EmployeeSessionManager(request.getSession());
			Integer clubId = eSessionManager.getClubId();
			condition.setClubId(clubId);						
			model.addAttribute("result", employeeService.list(condition, pageNum, pageSize));
			model.addAttribute("positions", clubService.getValidPositions(clubId));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MANAGEMENT_PATH + "employeeList";
	}

	@RequestMapping(value = "/employee/detail", method = RequestMethod.GET)
	public String employeesEdit(HttpServletRequest request, Model model, String uuid)
	{
		try {			
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());

			Integer clubId = esManager.getClubId();
			SdcomEmployee employee = employeeService.getByClubId(uuid,clubId);
			model.addAttribute("employee", employee);
			model.addAttribute("positions", clubService.getValidPositions(clubId));			
			if(employee.getId() == null || employee.getPositionEnum() == Position.Doctor) {				
				model.addAttribute("assistantList", employeeService.list(clubId, Position.Assistant));				
			}
			model.addAttribute("genders", employeeService.getGenders());
			model.addAttribute("departments", employeeService.getDepartments());
			model.addAttribute("workingState", employeeService.getWorkingState());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getClass()== NullPointerException.class ? "系统黑洞。。" : e.getMessage());
		}
		return MANAGEMENT_PATH + "employeeDetail";
	}
	
	@GetMapping(value="/employee/introduce")
	public String employeesIntroduce(HttpServletRequest request, Model model, String uuid) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			Integer clubId = esManager.getClubId();
			SdcomEmployee employee = employeeService.getByClubId(uuid,clubId);
			model.addAttribute("employee", employee);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getClass()== NullPointerException.class ? "系统黑洞。。" : e.getMessage());
		}
		return MANAGEMENT_PATH + "employeeIntroduce";
	}

	@RequestMapping(value = "/clubGeneral", method = RequestMethod.GET)
	public String clubGeneral(Model model, HttpServletRequest request) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			model.addAllAttributes(managementService.getClubGeneralStatistics(esManager.getClubId()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MANAGEMENT_PATH + "clubGeneral";
	}

	@RequestMapping(value = "/customerCare/list")
	public String getCustomerCareList(Model model, Integer carerId, String customerName, Date startDate, Date endDate,
			TcmMedicalRecord medicalRecord, Integer pageNum, Integer pageSize) {
		try {
			model.addAttribute("customerCareList", medicalRecordService.getCustomerCareList(carerId, customerName,
					startDate, endDate, pageNum, pageSize));
		} catch (Exception e) {
			model.addAttribute("errMessage", e.getMessage());
		}
		return MANAGEMENT_PATH + "customerCareList";
	}

	@RequestMapping(value = "/customerCare/medicalRecord/{medicalRecordId}")
	public String getCustomerCareMedicalRecord(Model model,
			@PathVariable(value = "medicalRecordId") Integer medicalRecordId) {
		try {
			model.addAttribute("customerCareMedicalRecord",
					medicalRecordService.getCustomerCareMedicalRecord(medicalRecordId));
		} catch (Exception e) {
			model.addAttribute("ereMessage", e.getMessage());
		}
		return MANAGEMENT_PATH + "customerCareDetail";
	}

	/*-------------------------------------------
	 * 客户管理
	 ------------------------------------------*/
	/**
	 * 客户列表，
	 * @author yzl 2018年8月29日
	 * @param condition 查询条件
	 * @param pageNum
	 * @param pageSize
	 * @param model
	 * @param request
	 * @param type 客户类型， membership（会员）or nonMembership（非会员） or all(所有)
	 * @return
	 */
	@RequestMapping(value = "/customer/list/{type}", method = RequestMethod.GET)
	public String customerList(SdbsCustomer condition, Integer pageNum, Integer pageSize, Model model,
			HttpServletRequest request, @PathVariable String type) {
		try {
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession());
			int clubId = sManager.getClubId();
			condition.addClubIdParam(clubId);
			condition.addTypeParam(type);
			ListResult<SdbsCustomer> result = customerService.list(condition, pageNum, pageSize);
			// add model
			model.addAttribute("result", result);
			model.addAttribute("condition", condition);
			if(type.equalsIgnoreCase("membership")) {
				model.addAttribute("serviceCardTypes", serviceCardService.list(ServiceCardType.TCM, clubId));
			}
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MANAGEMENT_PATH + "/customerList";
	}

	@RequestMapping(value = "/customer/detail/{uuid}", method = RequestMethod.GET)
	public String customerDetailInfo(@PathVariable("uuid") String uuid, Model model, HttpServletRequest request) {
		try {
			int defaultCount = 5;
			// 鑾峰彇clubId
			Integer clubId = new EmployeeSessionManager(request.getSession()).getClubId();
			SdbsCustomer customer = customerService.selectByUuid(uuid, clubId);
			if (customer == null) {
				throw new ShidaoException("用户不存在或已删除");
			}
			Integer customerId = customer.getId();
			model.addAttribute("customer", customer);
			
			//服务组
			model.addAllAttributes(customerServiceGroupService.getCustomerServiceGroups(customerId,clubId));
			//服务组服务的员工
			model.addAllAttributes(employeeService.getServiceGroups(clubId,customerId));
			
			model.addAllAttributes(payOrderService.listByCustomerId(customerId, defaultCount));
			// 病历列表
			model.addAttribute("records", medicalRecordService.listByCustomerId(customerId, defaultCount));
			// 脉诊列表
			model.addAttribute("mzHistories", maizhenyiHistoryService.getByCustomerId(customerId, defaultCount));
			// 职业列表
			model.addAttribute("profession", professioService.list(new SdbsProfession()));
			// 客情关怀信息
			model.addAttribute("cares", customerCareService.getCareOfCutomer(customerId, defaultCount));
			// 服务组
			model.addAttribute("serviceGroup", customerService.getServiceGroup(customerId));
			//若该门店存在套餐卡则查询套餐卡列表
			List<String> validServiceCards = ClubSetting.getSetting(clubId).getValidServiceCards();
			model.addAttribute("validServiceCards", validServiceCards);
			if (validServiceCards.contains("TCK")) {
				SdbsCustomerMembership customerMembership = new SdbsCustomerMembership();
				customerMembership.setCustomerId(customerId);
				customerMembership.setServiceCardType(ServiceCardType.TCK);
				customerMembership.setClubId(clubId);
				List<SdbsCustomerMembership> customerMemberships = customerMembershipService.list(customerMembership);
				model.addAttribute("tckmemberships", customerMemberships);
			}
			if (validServiceCards.contains("JFK")) {
				SdbsCustomerMembership customerMembership = new SdbsCustomerMembership();
				customerMembership.setCustomerId(customerId);
				customerMembership.setServiceCardType(ServiceCardType.JFK);
				customerMembership.setClubId(clubId);
				List<SdbsCustomerMembership> customerMemberships = customerMembershipService.list(customerMembership);
				model.addAttribute("jfkmemberships", customerMemberships);
			}
			if (validServiceCards.contains("CPZKK")) {
				SdbsCustomerMembership customerMembership = new SdbsCustomerMembership();
				customerMembership.setCustomerId(customerId);
				customerMembership.setServiceCardType(ServiceCardType.CPZKK);
				customerMembership.setClubId(clubId);
				List<SdbsCustomerMembership> customerMemberships = customerMembershipService.list(customerMembership);
				model.addAttribute("cpzkkmemberships", customerMemberships);
			}
			if (validServiceCards.contains("XMK")) {
				//项目卡
				model.addAttribute("XMKList", customerService.listXMKById(customerId,clubId));
				//本门店的项目卡
				SdbsProduct product = new SdbsProduct();
				product.setCategory(ProductCategory.XMK);
				product.setClubId(clubId);
				model.addAttribute("XMK", sdbsProductService.list(product));
			}
			if (validServiceCards.contains("TCM")) {
				//家属列表
				List<SdbsCustomerMembership> memberships = customer.getMemberships();
				if (memberships!= null &&memberships.size()>0 ){
					List<SdbsCustomer> relations = customerService.listCustomerRelation(customerId);
					model.addAttribute("customerRelation", relations);
					Object object = memberships.stream().anyMatch(m->m.getNumberOfUsers() == null || m.getNumberOfUsers()==-1) ?
							"无限制" :
								memberships.stream().mapToInt(SdbsCustomerMembership::getNumberOfUsers).sum() ;	
						
					model.addAttribute("num", relations.size()+"/"+object);
					model.addAttribute("persons", object);
				}else if (customer.getMainId() != null){
					// 主卡信息
					model.addAttribute("mainMembership", customerService.selectByPrimaryKey(customer.getMainId()));
				}
			}
			//客户来源
			model.addAttribute("customerSource", customerSourceService.selectByCustomerUuid(uuid,clubId));
			//获取本店是否有会员卡和本连锁店会员卡
			model.addAllAttributes(serviceCardService.getHaveTCMLocalAndChain(clubId));
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MANAGEMENT_PATH + "/customerInfo";
	}

	@RequestMapping(value = "/liliao/list", method = RequestMethod.GET)
	public String liliaoList(HttpServletRequest request, Model model, SdbsProduct condition, Integer pageNum,
			Integer pageSize) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setCategory(ProductCategory.Liliao);
			condition.setClubId(sm.getClubId());
			model.addAttribute("result",sdbsProductService.list(condition, pageNum, pageSize));
			model.addAttribute("condition", condition);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MANAGEMENT_PATH + "/liliaoList";
	}
	
	// 理疗产品详情
	@RequestMapping(value = "/liliao/detail/{productId}", method = RequestMethod.GET)
	public String liliaoDetail(HttpServletRequest request, Model model,
			@PathVariable(name = "productId") Integer productId) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (sm.getClubId() == null) 
				throw new ShidaoException("请登录!");
			if (productId == null) 
				return MANAGEMENT_PATH + "/liliaoDetail";
			SdbsProduct key = sdbsProductService.selectByPrimaryKey(productId);
			if (key.getClubId() != sm.getClubId()) 
				throw new ShidaoException("本店没有该理疗项目!");
			model.addAttribute("productId", productId);
			model.addAttribute("liliaoDetail", sdbsProductService.selectByPrimaryKey(productId));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MANAGEMENT_PATH + "/liliaoDetail";
	}
	
		@RequestMapping(value = "/liliao/create", method = RequestMethod.GET)
		public String createLiliaoProduct(HttpServletRequest request, Model model) {
			try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			// 供应商列表
			if (sm.getClubId() == null) 
				throw new ShidaoException("请登录!");
			} catch (Exception e) {
				model.addAttribute("errorMsg", e.getMessage());
			}
			return MANAGEMENT_PATH + "/liliaoDetail";
		}
		
	// 客流详单
	@RequestMapping(value = "/customerActivity", method = RequestMethod.GET)
	public String customerActivity(HttpServletRequest request, Model model, CustomerActivityVO condition,
			Integer pageNum, Integer pageSize) {
		try {
			/*if (pageSize == null) {
				pageSize=-1;
			}*/
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setClubId(sm.getClubId());
			model.addAttribute("condition", condition);

			if (condition.getDateFrom() != null && condition.getDateTo() != null)
				model.addAttribute("result", customerService.listCustomerActivity(condition, pageSize,pageNum));
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e);
		}

		return MANAGEMENT_PATH + "/customerActivity";
	}

	// 一卡多用 管理家庭成员
	@RequestMapping(value = "/membership/family", method = RequestMethod.GET)
	public String membershipFfamily(Model model,Integer mainCustomerId, String familyName,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			List<String> validServiceCards = ClubSetting.getSetting(sm.getClubId()).getValidServiceCards();
			//若该门店存在套餐卡则查询套餐卡列表
			if (validServiceCards.contains("TCM")) {
				SdbsCustomer key = customerService.selectByPrimaryKey(mainCustomerId,sm.getClubId());
				model.addAttribute("mainInfo", key);
				List<SdbsCustomer> relations = customerService.listCustomerRelation(mainCustomerId);
					//家属列表
					List<SdbsCustomerMembership> memberships = key.getMemberships();
					if (memberships!= null &&memberships.size()>0 ){
						Object object = memberships.stream().anyMatch(m->m.getNumberOfUsers()==-1) ?
								"无限制" :
									memberships.stream().mapToInt(SdbsCustomerMembership::getNumberOfUsers).sum() ;	
						model.addAttribute("num", relations.size()+"/"+object);
						model.addAttribute("persons", object);
					}
				//家属信息
				model.addAttribute("customerRelation",relations);
				//可添加的顾客信息
				if (familyName != null){
					SdbsCustomer familyCondition = new SdbsCustomer();
					familyCondition.setMainId(-1);
					familyCondition.setName(familyName);
					model.addAttribute("customers", customerService.list(familyCondition));
				}
				model.addAttribute("mainCustomerId", mainCustomerId);
			}
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MANAGEMENT_PATH + "/membershipFamiliy";
	}
	
	@RequestMapping(value="/mergeCustomer/{oldCustomerUuid}")
	public String mergeCustomer(@PathVariable String oldCustomerUuid,Model model){
		try {
			model.addAttribute("customer", customerService.selectByUuid(oldCustomerUuid));
		} catch (Exception e) {
			model.addAttribute("errMessage", e.getMessage());
		}
		return MANAGEMENT_PATH + "/mergeCustomer";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月1日
	 * 功能:检测项目列表和辟谷项目列表
	 * @param condition
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="product/list",method=RequestMethod.GET)
	public String jianceOrBiguList(SdbsProduct condition,Model model,
			HttpServletRequest request,Integer pageNum,Integer pageSize){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (condition.getCategory() == null)
				condition.setCategory(ProductCategory.SelfProduct);
			condition.setClubId(sm.getClubId());
			model.addAttribute("condition", condition);
			model.addAttribute("result", sdbsProductService.list(condition, pageNum, pageSize));
			model.addAttribute("requestURI",request.getRequestURI());
			model.addAttribute("categories", ProductCategory.SALES_CATEGORIES);
		} catch (Exception e) {
			model.addAttribute("errMessage", e.getMessage());
		}
		return MANAGEMENT_PATH + "/jianceOrBiguList";
	}
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月1日
	 * 功能:会员购冲记录
	 * @param model
	 * @param request
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value="membership/history",method=RequestMethod.GET)
	public String membershipHistory(SdbsCustomerServiceCardChangeHistory condition,Model model,HttpServletRequest request,Integer pageNum,Integer pageSize){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setClubId(sm.getClubId());
			model.addAttribute("condition", condition);
			condition.setCategories(CustomerServiceCardChangeHistoryCategory.historyCategorieList);
			model.addAttribute("result", customerServiceCardChangeHistoryService.list(condition, pageNum, pageSize));
			model.addAttribute("categorys", CustomerServiceCardChangeHistoryCategory.historyCategorieList);
			model.addAttribute("requestURI",request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errMessage", e.getMessage());
		}
		return MANAGEMENT_PATH + "/membershipHistory";
	}
	
	@GetMapping("/serviceCard/{type}/detail")
	public String tcmCardCreation(HttpServletRequest request, Model model,
			@PathVariable(name = "type") ServiceCardType type, String insertTCMType, Integer id, Integer tcmId) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			Integer clubId = esManager.getClubId();
			if(type == ServiceCardType.CXK && tcmId != null) {
				 SdbsServiceCard cxkCard = serviceCardService.getCXKByTCMId(tcmId,clubId);
				 if(cxkCard != null) {
					 return "redirect:detail?id=" + cxkCard.getId();
				 }
			}
			boolean canEdit = (id==null);
			boolean canDelete = false;
			// 新增
			if (id == null) {
				if(insertTCMType != null) {
					model.addAttribute("insertTCMType", insertTCMType);
				}
				if(tcmId != null) {
					type=ServiceCardType.CXK;
					model.addAttribute("tcmCard", serviceCardService.selectByPrimaryKey(tcmId));
				}
			} else {// 查看修改
				model.addAttribute("id", id);
				SdbsServiceCard card = serviceCardService.selectByPrimaryKey(id);
				if(card == null) {
					throw new ShidaoException("不存在的会员卡。");
				}
				type = card.getType();
				if(type != ServiceCardType.CXK) {
					model.addAttribute("customers", customerService.listCustomerOfServiceCard(id, clubId));
				}
				model.addAttribute("serviceCard", card);
				// 如果不是本店的卡，则不能修改
				if(card.getClubId() != null && !card.getClubId().equals(esManager.getClubId())) {
					canEdit = canDelete = false;
				}else {//查看权限
					canEdit = esManager.hasPermission(PermissionEnum.ServiceCardEdit);
					canDelete = esManager.hasPermission(PermissionEnum.ServiceCardDelete);
					if(type == ServiceCardType.TCM) {
						boolean isOwner = card == null ||
								(card.getClubId() != null && card.getClubId() == esManager.getClubId())||
								(card.getClubType() != null
								&& clubService.selectByPrimaryKey(clubId).getIsTypeMaster() == 1);
						canEdit = isOwner && canEdit;
						canDelete = isOwner && canDelete;
					}
				}
			}// end of if(id==null)
			model.addAttribute("type", type);
			model.addAttribute("canEdit", canEdit);
			model.addAttribute("canDelete", canDelete);
			// 增加 设置的基础数据
			if(canEdit) {
				switch (type) {
				case TCM:
				case JFK:
					model.addAttribute("categories", SalesOrderCategory.discountableCategories);
					break;
				case TCK:
					model.addAttribute("products", sdbsProductService.list(ProductCategory.Liliao,clubId));
					break;
				case CPZKK:
					model.addAttribute("products", sdbsProductService.list(ProductCategory.SelfProduct, clubId));
					break;
				case CXK:
					model.addAttribute("tckCards", serviceCardService.list(ServiceCardType.TCK, clubId));
					model.addAttribute("cpzkkCards", serviceCardService.list(ServiceCardType.CPZKK, clubId));
					model.addAttribute("jfkCards", serviceCardService.list(ServiceCardType.JFK, clubId));
					break;
				default:
					throw new ShidaoException("未处理的类型:"+type.getText());
				}
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", StringUtil.isNullOrEmpty(e.getMessage()) ? "程序黑洞" : e.getMessage());
		}
		
		return MANAGEMENT_PATH +( type==ServiceCardType.CXK? "serviceCardCXKDetail": "serviceCardDetail");
	}

	
	/**
	 * 查询所有可用的会员卡
	 * 
	 * @param salesOrderItemId
	 * @return
	 */
	@GetMapping("/serviceCard/list")
	public String selectSalesOrderItemById(SdbsServiceCard condition, Integer pageNum, Integer pageSize, Model model,
			HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			List<String> list = ClubSetting.getSetting(sm.getClubId()).getValidServiceCards();
			List<ServiceCardType> types = new ArrayList<>();
			if (list != null && list.size()>0) {
				list.forEach(a->types.add(Enum.valueOf(ServiceCardType.class, a)));
			}else{
				throw new ShidaoException("不存在卡项管理");
			}
			if (condition.getType() == null) {
				condition.setType(ServiceCardType.TCM);
			}
			model.addAttribute("condition", condition);
			condition.setClubId(sm.getClubId());
			ListResult<SdbsServiceCard> result = serviceCardService.list(condition, pageNum, pageSize);
			model.addAttribute("result", result);
			model.addAttribute("isTypeMaster",clubService.selectByPrimaryKey(sm.getClubId()).getIsTypeMaster());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("types", types);
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MANAGEMENT_PATH + "serviceCardList";
	}
	
	/**
	 * 卡项的各种操作
	 * @author yzl 2018年8月24日
	 * @param request
	 * @param model
	 * @param operation 操作
	 * @param type 会员卡类型
	 * @param customerUuid 客户编号，购买的时候私用
	 * @param membershipUuid 会员编号，调整的时候使用
	 * @return
	 */
	@GetMapping(value = "/membership/{type}/{operation}")
	public String customerInfoModal(HttpServletRequest request, Model model,
			@PathVariable("operation") MembershipOperation operation,
			@PathVariable("type") ServiceCardType type,
			String customerUuid,
			String membershipUuid,
			Boolean isLocalClub) {
		model.addAttribute("operation", operation);
		model.addAttribute("customerUuid", customerUuid);
		model.addAttribute("type", type);
		model.addAttribute("membershipUuid", membershipUuid);
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Integer clubId = sm.getClubId();
			SdbsCustomerMembership membership = customerMembershipService.selectByUuid(membershipUuid);
			if (isLocalClub == null)
				isLocalClub = (membership != null && membership.getClubId() != null);
			List<SdbsServiceCard> serviceCards = new ArrayList<>();
			switch (type) {
			case TCK:
			case JFK:
			case CPZKK:
				serviceCards = serviceCardService.list(type,clubId);
				break;
			case TCM:
				serviceCards = serviceCardService.listTCM(clubId,isLocalClub);
				break;
			default:
				break;
			}
			model.addAttribute("serviceCardList", serviceCards);
			model.addAttribute("membership", membership);
			// 责任医生和销售员
			model.addAttribute("doctors", employeeService.listByPosition(clubId, Position.Doctor, Position.Therapist));
			model.addAttribute("salesEmployees", employeeService.getResponsibleEmployee(clubId));
			//获取除会员卡外的支付方式
			SdepPayCategory payCategory = new SdepPayCategory();
			payCategory.setClubId(clubId);
			payCategory.setSupportPayType(SupportPayType.Nonmembership);
			payCategory.setName("现金流");
			SdepPayMethod payMethod = new SdepPayMethod();
			payMethod.setPayCategory(payCategory);
			payMethod.setEnabled(true);
			payMethodService.list(payMethod);
			model.addAttribute("validPayMethods4NonMembership", payMethodService.list(payMethod));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return MANAGEMENT_PATH + "/membershipOperation";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年10月15日
	 * 功能:店长修改预约助理页面
	 * @param request
	 * @param model
	 * @param condition
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping(value = "/appointment/edit")
	public String appointmentEdit(HttpServletRequest request, Model model, SdbsAppointment condition, Integer pageNum,Integer pageSize) {
		try {
			if (condition.getDate() == null)
				condition.setDate();
			model.addAttribute("condition", condition);
			/*
			 * 1. 设置基本信息:门诊列表，
			 */
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			// 2.保留条件
			condition.addParameter("TodayAndFuture", true);
			condition.setClubId(sm.getClubId());
			// 3. 获取预约记录
			ListResult<SdbsAppointment> appointments = appointmentService.list(condition, pageNum, pageSize);
			model.addAttribute("result", appointments);
			model.addAttribute("assistants", employeeService.list(sm.getClubId(), Position.Assistant));
			// 4. 分页所需信息
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MANAGEMENT_PATH + "appointmentEdit";
	}
	

	/**
	 * 查看最后访问日期在一定时间段内的客户情况
	 * @param request
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	@GetMapping("/customerLastVisitInfo")
	public String listLastVisitInfo(HttpServletRequest request,Model model,
			@RequestParam(required=false) LocalDate dateStart, @RequestParam(required=false) LocalDate dateEnd, Integer doctorId) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			if(es.hasPermission(PermissionEnum.ClubManagement)) {
				model.addAttribute("doctors", employeeService.list(es.getClubId(), Position.Doctor));
			}else if (es.getPositionEnum() == Position.Doctor) {
				doctorId = es.getEmployeeId();
			}
			model.addAttribute("visitInfo", customerService.listLastVisitInfo(es.getClubId(), dateStart, dateEnd, doctorId));
			model.addAttribute("doctorId", es.getEmployeeId());
			model.addAttribute("dateStart", dateStart);
			model.addAttribute("dateEnd", dateEnd);
			model.addAttribute("lostReasons", ValidList.getLostReasons());
			
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
	
		return MANAGEMENT_PATH + "customerLastVisitInfo";
	}
	
	@GetMapping(value="/usedHistory/{membershipCardId}")
	public String usedHistory(HttpServletRequest request, Model model, @PathVariable(value="membershipCardId") Integer membershipCardId) {
		try {
			model.addAttribute("history", customerMembershipService.listUsedHistory(membershipCardId));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MANAGEMENT_PATH + "membershipCardUsedHistory";
	}
	
	/**
	 * 查询支付类型以及支付类型下面的支付方法
	 * @param club_id
	 * @param request
	 * @return
	 * @throws ShidaoException
	 * @author yjj 2019年9月27日
	 */
	@GetMapping(value = "/payCategory/management")
	public String queryPayCategory(HttpServletRequest request,Model model){
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			SdepPayCategory condition = new SdepPayCategory();
			condition.setClubId(es.getClubId());		
		    model.addAttribute("categories", payCategoryService.list(condition,1,Integer.MAX_VALUE).getList());
		}catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MANAGEMENT_PATH + "managementpayCategory";
	}

	/**
	 * 查询提成信息
	 * @param model
	 * @param category
	 * @param position
	 * @param projectName
	 * @param request
	 * @return
	 */

	@GetMapping("/consumption/drawSetting/list")
	public String listSettingAll(Model model,SdepDrawSetting drawSetting,HttpServletRequest request,Integer pageNum,Integer pageSize) {
		try {
			
		    EmployeeSessionManager.checkLogin(request.getSession());
			model.addAttribute("drawSettings", drawSettingService.list(drawSetting,pageNum,pageSize));
			model.addAttribute("drawSetting", drawSetting);
			model.addAttribute("employees", employeeService.listByInServiceStaff());
			model.addAttribute("projectCategorys",IncomeProjectCategory.values());
			model.addAttribute("yibaoCategorys",IncomeProjectCategory.YIBAO_CATEGORIES);
			model.addAttribute("huizhongyiCategorys",IncomeProjectCategory.HUIZHONGYI_CATEGORIES);
			model.addAttribute("requestURI",request.getRequestURL());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MANAGEMENT_PATH+"drawSettingList";
	}

//	@GetMapping("/consumption/drawSetting/list")
//	public String listSettingAll(Model model,SdepDrawSetting drawSetting,HttpServletRequest request,Integer pageNum,Integer pageSize) {
//		try {
//			
//		    EmployeeSessionManager.checkLogin(request.getSession());
//			model.addAttribute("drawSettings", drawSettingService.list(drawSetting,pageNum,pageSize));
//			model.addAttribute("drawSetting", drawSetting);
//			model.addAttribute("employees", employeeService.listByInServiceWorkingState());
//			model.addAttribute("projectCategorys",IncomeProjectCategory.values());
//			model.addAttribute("requestURI",request.getRequestURL());
//		} catch (Exception e) {
//			model.addAttribute("errorMsg", e.getMessage());
//		}
//		return MANAGEMENT_PATH+"drawSettingList";
//	}

}