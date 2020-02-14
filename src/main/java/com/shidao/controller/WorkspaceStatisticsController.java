package com.shidao.controller;

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

import com.shidao.enums.AlgorithmName;
import com.shidao.enums.ClubType;
import com.shidao.enums.MaintainceCategory;
import com.shidao.enums.PDFPrescriptionCategory;
import com.shidao.enums.Position;
import com.shidao.enums.ProductCategory;
import com.shidao.enums.SalesOrderCategory;
import com.shidao.model.CrmCustomerMaintaince;
import com.shidao.model.DataAnalysis;
import com.shidao.model.DatePeriodParameter;
import com.shidao.model.SdcomClubMembership.OperationCategory;
import com.shidao.model.WmsInventory;
import com.shidao.service.CrmCustomerMaintainceService;
import com.shidao.service.SdbsCustomerCareService;
import com.shidao.service.SdbsCustomerService;
import com.shidao.service.SdbsProductService;
import com.shidao.service.SdcomClubOperationHistoryService;
import com.shidao.service.SdcomClubService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.service.SdepAlgorithmDescriptionService;
import com.shidao.service.StatisticsService;
import com.shidao.service.TcmDiseaseService;
import com.shidao.service.WmsInventoryService;
import com.shidao.service.WmsWarehouseService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.ListResult;
import com.shidao.util.PermissionEnum;
import com.shidao.util.ShidaoException;
import com.shidao.util.Statistics;
import com.shidao.util.StringUtil;
import com.shidao.vo.ClubOperationStaticsVO;
import com.shidao.vo.CwDinggaoVo;
import com.shidao.vo.PrescriptionUsageDetailVO;

@Controller
@RequestMapping("/workspace/statistics")
public class WorkspaceStatisticsController extends BaseController {

	private static final String STATISTICS_PATH = "/display/workspace/statistics/";

	@Autowired
	private StatisticsService statisticsService;;

	@Autowired
	private SdcomClubService clubService;

	@Autowired
	private SdbsCustomerCareService customerCareService;

	@Autowired
	private SdcomClubOperationHistoryService clubOperationHistoryService;

	@Autowired
	private SdbsCustomerService customerService;
	
	@Autowired
	private SdcomEmployeeService employeeService;
	
	@Autowired
	private TcmDiseaseService diseaseService;
	
	@Autowired
	private SdbsProductService productService;
	
	@Autowired
	private CrmCustomerMaintainceService customerMaintainceService;
	
	@Autowired
	private SdepAlgorithmDescriptionService algorithmDescriptionService;
	
	@Autowired
	private WmsInventoryService inventoryService;
	
	@Autowired
	private WmsWarehouseService warehouseService;

	@RequestMapping(value = "/doctorProfit", method = RequestMethod.GET)
	public String getDoctorProfit(HttpServletRequest request, Model model, 
			Integer clubId,
			Integer doctorId,
			Date dateFrom,
			Date dateTo) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("clubId", clubId);
			model.addAttribute("doctorId", doctorId);
			model.addAttribute("dateFrom", dateFrom);
			model.addAttribute("dateTo", dateTo);
			if (sm.hasPermission(PermissionEnum.STATS_ALL_DOCTOR))
				model.addAttribute("clubs", clubService.list(ClubType.ShidaoClinic));
			else if (sm.hasPermission(PermissionEnum.STATS_CLUB_DOCTOR)) {
				model.addAttribute("doctors", employeeService.list(sm.getClubId(),Position.Doctor));
				clubId = sm.getClubId();
			} else if (sm.hasPermission(PermissionEnum.STATS_CURRENT_DOCTOR)) {
				doctorId = sm.getEmployeeId();
			} else {
				throw new ShidaoException("没有查看医生统计的权限");
			}
			
			if (dateFrom != null && dateTo != null) {
				model.addAllAttributes(statisticsService.getDoctorProfit( clubId, doctorId,dateFrom, dateTo));
			}
			else {
				throw new ShidaoException("请选择起始日期。");
			}

			
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return STATISTICS_PATH + "doctorStatistics";
	}

	@RequestMapping(value = "/clubOperationStatics", method = RequestMethod.GET)
	public String clubOperationStaticsHistory(Model model, ClubOperationStaticsVO clubOperationStaticsVO) {
		try {
			model.addAttribute("operation", OperationCategory.values());
			model.addAttribute("clubOperationStaticsVO", clubOperationStaticsVO);
			if (clubOperationStaticsVO.getDateStart() != null && clubOperationStaticsVO.getDateEnd() != null) {
				model.addAttribute("clubOperationStaticsHistory",
						clubOperationHistoryService.getStatis(clubOperationStaticsVO));
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}

		return STATISTICS_PATH + "clubOperationStatics";
	}

	@RequestMapping(value = "/afterCare", method = RequestMethod.GET)
	public String afterCare(Model model, Date startDate, Date stopDate) {
		try {
			if (startDate != null && stopDate != null) {
				model.addAttribute("CountCare", customerCareService.getCountCareByDate(startDate, stopDate));
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "afterCare";
	}

	@RequestMapping(value = "/custmerCare", method = RequestMethod.GET)
	public String getCustmerCare(Model model, HttpServletRequest request, DatePeriodParameter date) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("datePeriodParameter", date);
			model.addAllAttributes(statisticsService.getCareStatisticsByDate(sm.getClubId(), date.getDateStart(), date.getDateEnd()));				
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "customerCareStatistics";
	}

	@RequestMapping(value = "/customerConsumption", method = RequestMethod.GET)
	public String getCustomerMember(Model model, HttpServletRequest request, Date dateFrom, Date dateTo) {
		try {
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession());
			Integer clubId = sManager.getClubId();
			model.addAttribute("dateFrom", dateFrom);
			model.addAttribute("dateTo", dateTo);
			model.addAttribute("clubName", sManager.getClubName());

			if (dateFrom != null && dateTo != null) {
				model.addAllAttributes(statisticsService.getCustomerMember(dateFrom, dateTo, clubId));
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "customerConsumption";
	}

	@RequestMapping(value = "/memberConsumption/{categer}", method = RequestMethod.GET)
	public String getCustomerNonMember(Model model, HttpServletRequest request,@PathVariable(value="categer") String categer, DatePeriodParameter datePeriodParameter) {
		try {
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession());
			Integer clubId = sManager.getClubId();
			model.addAttribute("datePeriodParameter", datePeriodParameter);
			model.addAttribute("clubName", sManager.getClubName());
			model.addAttribute("categer", categer);
			model.addAttribute("requestURI",request.getRequestURI());
			
				if(categer.equals("VIP"))
					model.addAllAttributes(statisticsService.getMemberStatistics(datePeriodParameter.getDateStart(), datePeriodParameter.getDateEnd(), clubId));
				else
					model.addAllAttributes(statisticsService.getCustomerMember(datePeriodParameter.getDateStart(), datePeriodParameter.getDateEnd(), clubId));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "customerConsumption";
	}
	
	@RequestMapping(value = "/consumption/flow", method = RequestMethod.GET)
	public String consumptionMember(Model model, HttpServletRequest request, DatePeriodParameter date) {
		try {
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession());
			Integer clubId = sManager.getClubId();
			model.addAttribute("datePeriodParameter", date);
			model.addAttribute("clubName", sManager.getClubName());
			model.addAttribute("list", customerService.listConsumption(clubId, date.getDateStart(), date.getDateEnd()));
			//统计信息
			model.addAttribute("statistics", customerService.getConsumptionrStatistics(clubId, date.getDateStart(), date.getDateEnd()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "consumptionStatistics";
	}
	
	@RequestMapping(value = "/inventory/medicine", method = RequestMethod.GET)
	public String inventoryStats(Model model, HttpServletRequest request,ProductCategory category,
			Date dateFrom, Date dateTo) {
		try {
			if (category == null) {
				category = ProductCategory.Keliji;
			}
			model.addAttribute("categories", ProductCategory.MedicineStatistics);
			model.addAttribute("dateStart", dateFrom);
			model.addAttribute("dateEnd", dateTo);
			model.addAttribute("category", category);
			EmployeeSessionManager esm = new EmployeeSessionManager(request.getSession());
			Integer warehouseId = esm.getWarehouseId();
			model.addAllAttributes(statisticsService.getMedicine4Warehouse(dateFrom, dateTo, warehouseId, category));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "inventoryMedicine";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月7日
	 * 功能:显示门诊收费页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/clubIncome", method = RequestMethod.GET)
	public String clubIncome(Model model,HttpServletRequest request,DatePeriodParameter datePeriodParameter) {
		try{
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAllAttributes(statisticsService.getClubIncome(datePeriodParameter,sm.getClubId()));
		}catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "clubIncome";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月7日
	 * 功能:客户消费页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/customer/consumption/detail", method = RequestMethod.GET)
	public String customerConsumptionDetail(Model model,HttpServletRequest request,DatePeriodParameter datePeriodParameter, Integer salesId) {
		try{
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			Integer clubId = esManager.getClubId();
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAttribute("requestURI",request.getRequestURI());
			model.addAllAttributes(statisticsService.getCustomerConsumptionDetail(datePeriodParameter, clubId,salesId));			
			model.addAttribute("employees", employeeService.listByPosition(clubId, Position.Doctor,Position.Assistant,Position.ClubManager));
			model.addAttribute("salesId", salesId);
		}catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "customerConsumptionDetail";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月7日
	 * 功能:门诊日报表页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/club/dailyReport", method = RequestMethod.GET)
	public String clubDailyReport(Model model,HttpServletRequest request,DatePeriodParameter datePeriodParameter) {
		try{
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAttribute("requestURI",request.getRequestURI());
			model.addAllAttributes(statisticsService.getClubDailyReport(datePeriodParameter, sm.getClubId()));
		}catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "clubDailyReport";
	}
	
	/**
	 * 客户消费额统计
	 * @param model
	 * @param request
	 * @param datePeriodParameter
	 * @param clubId
	 * @return
	 */
	@RequestMapping(value="/customer/consumptionAmount", method = RequestMethod.GET)
	public String customerConsumptionAmount(Model model,HttpServletRequest request,DatePeriodParameter datePeriodParameter) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAttribute("customerConsumptionAmount", statisticsService.getCustomerConsumptionAmount(datePeriodParameter, sm.getClubId()));
		}catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
	   }
		return STATISTICS_PATH + "customerConsumptionAmount";
	}
	
	/**
	 * 一、客户消费比例
	 * @param model
	 * @param request
	 * @param datePeriodParameter
	 * @param clubId
	 * @return
	 */
	@RequestMapping(value="/customer/consumption/ratio", method = RequestMethod.GET)
	public String getCustomerConsumptionRatio(Model model,HttpServletRequest request,DatePeriodParameter datePeriodParameter,Integer clubId) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAllAttributes(statisticsService.getCustomerConsumptionRatio(datePeriodParameter, sm.getClubId()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "customerConsumptionRatio";
	}
	
	/**
	 * 二、医生数据分析
	 * @param model
	 * @param request
	 * @param datePeriodParameter
	 * @param clubId
	 * @return
	 */
	@RequestMapping(value="/doctor/analysis", method = RequestMethod.GET)
	public String getDoctorAnalysis(Model model,HttpServletRequest request,DatePeriodParameter datePeriodParameter) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAllAttributes(statisticsService.getDoctorAnalysis(datePeriodParameter, sm.getClubId()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "doctorAnalysis";
	}
	
	/**
	 * 销售明细，可以是产品，理疗，其他，等
	 * @param model
	 * @param request
	 * @param datePeriodParameter
	 * @param clubId
	 * @return
	 */
	@RequestMapping(value="/{category}/SalesDetail", method = RequestMethod.GET)
	public String getSalesDetail4Cateogry(Model model,HttpServletRequest request,
			@PathVariable SalesOrderCategory category, DatePeriodParameter datePeriodParameter) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("category", category);
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAllAttributes(statisticsService.getSalesDetailOfCategory(datePeriodParameter, sm.getClubId(), category));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "salesDetail4Category";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月28日
	 * 功能:销售明细详情，可以是产品，理疗，其他，等
	 * @param model
	 * @param request
	 * @param category
	 * @param datePeriodParameter
	 * @return
	 */
	@RequestMapping(value="/salesDetail/{productId}", method = RequestMethod.GET)
	public String getSalesDetail4CateogryDetail(Model model,HttpServletRequest request,
			@PathVariable Integer productId, DatePeriodParameter datePeriodParameter) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAllAttributes(statisticsService.getSalesDetailOfCategoryDetail(datePeriodParameter, sm.getClubId(), productId));
			model.addAttribute("productName",productService.selectByPrimaryKey(productId).getName());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "salesDetail4CategoryDetail";
	}
	
	/**
	 * 五、处方流
	 * @param model
	 * @param request
	 * @param datePeriodParameter
	 * @param clubId
	 * @return
	 */
	@RequestMapping(value="/prescription/flow")
	public String getPrescriptionFlow(Model model,HttpServletRequest request,DatePeriodParameter datePeriodParameter) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAllAttributes(statisticsService.getPrescriptionFlow(datePeriodParameter, sm.getClubId()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "prescriptionFlow";
	}
	
	@GetMapping(value="/prescription/amount")
	public String getPrescriptionAmount(Model model,DataAnalysis dataAnalysis,HttpServletRequest request) throws ShidaoException {
		try {
			this.getRootAndMonth(model, dataAnalysis, request);
			model.addAllAttributes(statisticsService.getPrescriptionAmount(dataAnalysis));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return STATISTICS_PATH + "prescriptionAmount";
	}
	
	@GetMapping(value="/prescription/summary")
	public String getPrescriptionSummary(Model model,DataAnalysis dataAnalysis,HttpServletRequest request){
		try {
			this.getRootAndMonth(model, dataAnalysis, request, AlgorithmName.PrescriptionSummary);
			model.addAllAttributes(statisticsService.getPrescriptionSummary(dataAnalysis));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return STATISTICS_PATH + "prescriptionSummary";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月23日
	 * 功能:处方使用统计表
	 * @param model
	 * @param request
	 * @param datePeriodParameter
	 * @return
	 * @throws ShidaoException
	 */
	@RequestMapping(value="/prescriptionUsage")
	public String prescriptionUsage(Model model,HttpServletRequest request,DatePeriodParameter datePeriodParameter) throws ShidaoException {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAllAttributes(statisticsService.getPrescriptionUsage(datePeriodParameter, sm.getClubId()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "prescriptionUsage";
	}
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月23日
	 * 功能:2.系统处方使用总览
	 * @param model
	 * @param request
	 * @param datePeriodParameter
	 * @return
	 * @throws ShidaoException
	 */
	@RequestMapping(value="/sysPrescriptionUsage")
	public String sysPrescriptionUsage(Model model,HttpServletRequest request,DatePeriodParameter datePeriodParameter) throws ShidaoException {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAttribute("list", statisticsService.getSysPrescriptionUsage(datePeriodParameter, sm.getClubId()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "sysrPescriptionUsage";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月28日
	 * 功能:点击系统处方的数量，弹出开方详情 
	 * @param model
	 * @param request
	 * @param datePeriodParameter
	 * @param doctorId
	 * @return
	 * @throws ShidaoException
	 */
	@RequestMapping(value="/sysPrescriptionUsageInfo")
	public String sysPrescriptionUsageInfo(Model model,HttpServletRequest request,DatePeriodParameter datePeriodParameter,Integer doctorId) throws ShidaoException {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAttribute("list", statisticsService.getSysPrescriptionUsageInfo(datePeriodParameter, sm.getClubId(),doctorId));
			model.addAttribute("doctorName", employeeService.selectByPrimaryKey(doctorId).getName());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "sysPrescriptionUsageInfo";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月23日
	 * 功能:3.系统处方调方一览
	 * @param model
	 * @param request
	 * @param datePeriodParameter
	 * @return
	 * @throws ShidaoException
	 */
	@RequestMapping(value="/sysPrescriptionModification")
	public String sysPrescriptionModification(Model model,HttpServletRequest request,DatePeriodParameter datePeriodParameter,
			PDFPrescriptionCategory category) throws ShidaoException {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAttribute("list", statisticsService.getSysrescriptionModification(datePeriodParameter, sm.getClubId(),category));
			model.addAttribute("category", category);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "sysrescriptionModification";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月23日
	 * 功能:自动处方复用统计表
	 * @param model
	 * @param request
	 * @param datePeriodParameter
	 * @return
	 * @throws ShidaoException
	 */
	@RequestMapping(value="/sysPrescriptionReuse")
	public String sysPrescriptionReuse(Model model,HttpServletRequest request,DatePeriodParameter datePeriodParameter) throws ShidaoException {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAllAttributes(statisticsService.getSysPrescriptionReuse(datePeriodParameter, sm.getClubId()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "sysPrescriptionReuse";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月23日
	 * 功能:自动处方使用详情
	 * @param model
	 * @param request
	 * @param datePeriodParameter
	 * @return
	 * @throws ShidaoException
	 */
	@RequestMapping(value="/sysPrescriptionUsageDetail")
	public String sysPrescriptionUsageDetail(Model model,HttpServletRequest request,DatePeriodParameter datePeriodParameter,Integer diseaseId) throws ShidaoException {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAttribute("list", statisticsService.getSysPrescriptionUsageDetail(datePeriodParameter, sm.getClubId(),diseaseId));
			model.addAttribute("diseaseId", diseaseId);
			model.addAttribute("diseaseName", diseaseService.selectByPrimaryKey(diseaseId).getName());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "sysPrescriptionUsageDetail";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月2日
	 * 功能:手动开方详情
	 * @param model
	 * @param request
	 * @param date
	 * @param doctorId
	 * @return
	 * @throws ShidaoException
	 */
	@RequestMapping(value="/manualPrescriptionUsageDetail")
	public String manualPrescriptionUsageDetail(Model model,HttpServletRequest request,
			PrescriptionUsageDetailVO condition,
			Integer pageNum,
			Integer pageSize) throws ShidaoException {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Integer clubId = sm.getClubId();
			DatePeriodParameter date = new DatePeriodParameter();
			date.setDateStart(condition.getDateStart());
			date.setDateEnd(condition.getDateEnd());
			model.addAttribute("datePeriodParameter", date);
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("condition",condition);
			condition.setClubId(clubId);
			ListResult<PrescriptionUsageDetailVO> result = statisticsService.getManualPrescriptionUsageDetail(condition,pageNum,pageSize);
			model.addAttribute("result", result);
			model.addAttribute("sum", result.getList().stream().mapToInt(a->a.getCount()).sum());
			model.addAttribute("doctors", employeeService.list(clubId, Position.Doctor));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return STATISTICS_PATH + "manualPrescriptionUsageDetail";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年6月27日
	 * 功能:脱失统计
	 * @param model
	 * @param request
	 * @return
	 */
	@GetMapping("/lost")
	public String lossList(DatePeriodParameter datePeriodParameter,Model model,HttpServletRequest request){
		try {
			List<CrmCustomerMaintaince> list = customerMaintainceService.getLostStatistics(datePeriodParameter);
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("date", datePeriodParameter);
			model.addAttribute("category", MaintainceCategory.Lost);
			model.addAttribute("list",list);
			model.addAttribute("total", list.stream().mapToInt(a->a.getAmount()).sum());
		} catch (Exception e) {
			model.addAttribute("errMessage", e.getMessage());
		}
		return STATISTICS_PATH + "lost";
	}
	
	/**
	 * 财务定稿
	 * @author yzl 2018年9月28日
	 * @param request
	 * @param model
	 * @param clubId
	 * @param date
	 * @return
	 */
	@GetMapping("/dinggao")
	public String dinggao(HttpServletRequest request, Model model, Integer clubId, DatePeriodParameter datePeriodParameter) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
						
			if(clubId == null) {
				clubId = esManager.getClubId();
			}
			model.addAttribute("clubId", clubId);
			
			if (esManager.getPosition().equals("财务")) {
				model.addAttribute("clubs", clubService.list(ClubType.ShidaoClinic));
			}
			List<CwDinggaoVo> dinggaoVos = statisticsService.selectByPayDate(datePeriodParameter, clubId);
			
			model.addAttribute("dinggao", dinggaoVos);
			model.addAttribute("summary", Statistics.summarizeModel(dinggaoVos));
		} catch (Exception e) {
			model.addAttribute("errorMsg","错误：" + e.getMessage());
		}
		return STATISTICS_PATH + "dinggao";
	}
	
	@GetMapping("/dinggao/byPayMethod")
	public String dinggaoByPayMethod(HttpServletRequest request, Model model, Integer clubId, DatePeriodParameter datePeriodParameter) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			
			if(clubId == null) {
				clubId = esManager.getClubId();
			}
			model.addAllAttributes(statisticsService.selectbyPayMethod(datePeriodParameter, clubId));
		} catch (Exception e) {
			model.addAttribute("errorMsg","错误：" + e.getMessage());
		}
		return STATISTICS_PATH + "dinggaoByPayMethod";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月9日<br>
	 * 功能:处方开药统计
	 * @param request
	 * @param model
	 * @param category
	 * @param datePeriodParameter
	 * @return
	 */
	@GetMapping("/inventory/byMedicine")
	public String inventoryByMedicine(HttpServletRequest request, Model model,ProductCategory category, DatePeriodParameter datePeriodParameter) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			if (category == null) {
				category=ProductCategory.Keliji;
			}
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAttribute("category",category);
			model.addAttribute("inventoryByMedicines", statisticsService.getInventoryByMedicine(datePeriodParameter, esManager.getWarehouseId(), category));
		} catch (Exception e) {
			model.addAttribute("errorMsg","错误：" + e.getMessage());
		}
		return STATISTICS_PATH + "inventoryByMedicine";
	}
	
	@GetMapping("/inventory/medicineDetail")
	public String inventoryMedicineDetail(HttpServletRequest request, Model model,ProductCategory category, DatePeriodParameter datePeriodParameter
			,Integer medicineId,String medicineName) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			if (category == null) {
				category=ProductCategory.Keliji;
			}
			List<WmsInventory> list = inventoryService.listProductInventory(medicineId, esManager.getWarehouseId(),category);
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			model.addAttribute("category",category);
			model.addAttribute("medicineName",medicineName);
			model.addAttribute("medicineId",medicineId);
			model.addAttribute("inventoryList", list);
			model.addAttribute("total", StringUtil.round(list.stream().filter(a->a.getAmount()>0).mapToDouble(b->b.getAmount()).sum()));
			model.addAttribute("caoyaoTotal", StringUtil.round(list.stream().filter(a->a.getAmount()>0).mapToDouble(b->Double.parseDouble(b.getCaoyaoAmount().toString())).sum()));
			model.addAllAttributes(statisticsService.getInventoryMedicineDetail(datePeriodParameter, esManager.getWarehouseId(), category, medicineId));
		} catch (Exception e) {
			model.addAttribute("errorMsg","错误：" + e.getMessage());
		}
		return STATISTICS_PATH + "inventoryMedicineDetail";
	}
	
	@GetMapping("/manager/clubMedicine")
	public String getManager4ClubMedicineAmount(HttpServletRequest request, Model model,ProductCategory category, DatePeriodParameter date) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			if (category == null) {
				category=ProductCategory.Keliji;
			}
			model.addAttribute("datePeriodParameter",date);
			model.addAttribute("category",category);
			model.addAllAttributes(statisticsService.getManager4ClubMedicineAmount(date, esManager.getEmployeeId(), category));
		} catch (Exception e) {
			model.addAttribute("errorMsg","错误：" + e.getMessage());
		}
		return STATISTICS_PATH + "manager4ClubMedicine";
	}
	
	@GetMapping("/manager/clubMedicine/detail")
	public String getManager4ClubMedicineAmountDetail(HttpServletRequest request, Model model,Integer clubId,ProductCategory category, DatePeriodParameter date) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			if (category == null) {
				category=ProductCategory.Keliji;
			}
			model.addAttribute("datePeriodParameter",date);
			model.addAttribute("category",category);
			model.addAttribute("clubName",clubService.selectByPrimaryKey(clubId).getName());
			model.addAllAttributes(statisticsService.getManager4ClubMedicineAmountDetail(date, clubId, category));
		} catch (Exception e) {
			model.addAttribute("errorMsg","错误：" + e.getMessage());
		}
		return STATISTICS_PATH + "manager4ClubMedicineDetail";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月28日<br>
	 * 功能:供应商销售统计
	 * @param request
	 * @param model
	 * @param clubId
	 * @param warehouseId
	 * @param datePeriodParameter
	 * @return
	 */
	@GetMapping("/vendorStatistics")
	public String vendorStatistics(HttpServletRequest request, Model model, Integer clubId,Integer warehouseId, DatePeriodParameter datePeriodParameter) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			if (esManager.hasPermission(PermissionEnum.My_Chain_Pharmacy_Permission)) {
				//根据clubId获取连锁药房
				model.addAttribute("warehouseList", warehouseService.listWarehouseByClubId(esManager.getClubId()));
			}else if (esManager.hasPermission(PermissionEnum.My_Pharmacy_Permission)) {
				warehouseId = esManager.getWarehouseId();
				model.addAttribute("warehouse", warehouseService.selectByPrimaryKey(warehouseId));
			}
			model.addAttribute("clubId", clubId);
			
			model.addAttribute("warehouseId", warehouseId);
			
			if (warehouseId != null) {
				model.addAttribute("clubList",clubService.listRelationClubs(warehouseId));
			}
			if (clubId != null) {
				model.addAllAttributes(statisticsService.getVendorsStatistics(datePeriodParameter, clubId));
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg","错误：" + e.getMessage());
		}
		return STATISTICS_PATH + "vendorStatistics";
	}
	
	
	@GetMapping("/dispensingDetailStatistics")
	public String dispensingDetailStatistics(HttpServletRequest request, Model model, Integer clubId,Integer warehouseId, DatePeriodParameter datePeriodParameter) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter",datePeriodParameter);
			if (esManager.hasPermission(PermissionEnum.My_Chain_Pharmacy_Permission)) {
				//根据clubId获取连锁药房
				model.addAttribute("warehouseList", warehouseService.listWarehouseByClubId(esManager.getClubId()));
			}else if (esManager.hasPermission(PermissionEnum.My_Pharmacy_Permission)) {
				warehouseId = esManager.getWarehouseId();
				model.addAttribute("warehouse", warehouseService.selectByPrimaryKey(warehouseId));
			}
			model.addAttribute("clubId", clubId);
			
			model.addAttribute("warehouseId", warehouseId);
			
			if (warehouseId != null) {
				model.addAttribute("clubList",clubService.listRelationClubs(warehouseId));
			}
			if (clubId != null) {
				model.addAllAttributes(statisticsService.getDispensingDetailStatistics(datePeriodParameter, clubId));
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg","错误：" + e.getMessage());
		}
		return STATISTICS_PATH + "dispensingDetailStatistics";
	}
	
	/**
	 * 数据分析公共方法
	 * 包含 权限使用  连锁店权限返回门店id 自动补充日期功能
	 * zzp 2018-11-1
	 * @param model
	 * @param dataAnalysis
	 * @param request
	 * @throws ShidaoException
	 */
	public void getRootAndMonth(Model model,DataAnalysis dataAnalysis,HttpServletRequest request) throws ShidaoException{
		this.getRootAndMonth(model, dataAnalysis, request, null);
	}
	
	/**
	 * 数据分析公共方法
	 * 包含 权限使用  连锁店权限返回门店id 自动补充日期功能
	 * zzp 2018-11-14
	 * @param model
	 * @param dataAnalysis
	 * @param request
	 * @param annotation
	 * @throws ShidaoException
	 */
	public void getRootAndMonth(Model model,DataAnalysis dataAnalysis,HttpServletRequest request,AlgorithmName algorithmName) throws ShidaoException{
		if (algorithmName != null) {
			model.addAttribute("annotation", algorithmDescriptionService.getDescription(algorithmName));
		}
		EmployeeSessionManager eSessionManager = new EmployeeSessionManager(request.getSession());
		if(eSessionManager.hasPermission(PermissionEnum.CHAIN_PERMISSION)) {
			if (dataAnalysis.getClubId() == null) {
				dataAnalysis.setClubType(eSessionManager.getClubType());
			}
			model.addAttribute("clubs", clubService.list(eSessionManager.getClubType()));
		}else if (eSessionManager.hasPermission(PermissionEnum.CLUB_PERMISSION)) {
			dataAnalysis.setClubId(eSessionManager.getClubId());
		}else {
			throw new ShidaoException("没有权限");
		}
		model.addAttribute("monthStart", dataAnalysis.getMonthStart());
		model.addAttribute("monthEnd", dataAnalysis.getMonthEnd());
		model.addAttribute("dataAnalysis", dataAnalysis);
	}
	
}
