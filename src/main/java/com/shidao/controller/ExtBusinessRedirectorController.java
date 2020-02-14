package com.shidao.controller;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.shidao.enums.ExtBusinessCategory;
import com.shidao.enums.ExtDaijianDaipei;
import com.shidao.enums.ExtSettingCategory;
import com.shidao.enums.Gender;
import com.shidao.enums.Position;
import com.shidao.model.ExtConsumptionOrderDailySummary;
import com.shidao.model.ExtCustomer;
import com.shidao.model.ExtConsumptionOrder;
import com.shidao.model.ExtConsumptionOrderDailyStatus;
import com.shidao.model.ExtSetting;
import com.shidao.service.ExtBusinessStatisicsService;
import com.shidao.service.ExtConsumptionOrderDailyStatusService;
import com.shidao.service.ExtConsumptionOrderDailySummaryService;
import com.shidao.service.ExtConsumptionOrderFinalizationService;
import com.shidao.service.ExtConsumptionOrderItemService;
import com.shidao.service.ExtConsumptionOrderService;
import com.shidao.service.ExtCustomerService;
import com.shidao.service.ExtSettingService;
import com.shidao.service.ExtTreatmentDistributionService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.ShidaoException;


@Controller
@RequestMapping("/ext/business")

public class ExtBusinessRedirectorController extends BaseController {

	private static final String EXT_BUSINESS_PATH = "/display/extBusiness/";

	/* private static final String EXT_BUSTNESS_PATHS="" */

	@Autowired
	ExtConsumptionOrderService extConsumptionOrderService;

	@Autowired
	private ExtConsumptionOrderItemService itemService;
	
	@Autowired
	SdcomEmployeeService employeeservice;

	@Autowired
	ExtSettingService extSettingService;

	@Autowired
	ExtBusinessStatisicsService statisicsService;
	
	@Autowired
	ExtConsumptionOrderFinalizationService finalizationService;
	
	@Autowired
	ExtTreatmentDistributionService distributionService;
	
	@Autowired
	ExtConsumptionOrderDailySummaryService dailySummaryService;
	
	@Autowired
	ExtConsumptionOrderDailyStatusService dailyStatusService;
	
	@Autowired
	ExtCustomerService customerService;


	/**
	 * 
	 * @param date 订单所属日期
	 * @return
	 */
	
	@GetMapping("/consumption/order/list")
	public String listConsumptionOrders(HttpServletRequest request, Model model, ExtConsumptionOrder condition ) {
		try {
		    
		    //根据日期查询状态表返回信息
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			condition.setClubId(es.getClubId());
			List<ExtConsumptionOrder> orders = extConsumptionOrderService.list(condition);
			model.addAttribute("dailyStatus",dailyStatusService.selectDailyStatus(new ExtConsumptionOrderDailyStatus(condition.getDate(), condition.getCategory(),es.getClubId())));
			model.addAttribute("orders", orders);
			model.addAttribute("condition", condition);
			model.addAttribute("assistants", employeeservice.listByPosition(es.getClubId(), Position.Assistant));
			model.addAttribute("doctors", employeeservice.listByPosition(es.getClubId(),Position.zhiliaoPositions));
			model.addAttribute("jiesuanFangshiList", extSettingService.list(es.getClubId(), ExtSettingCategory.结算方式));
			model.addAttribute("kaZhongList", extSettingService.list(es.getClubId(), ExtSettingCategory.卡种));
			model.addAttribute("dizhiList", extSettingService.list(es.getClubId(), ExtSettingCategory.快递地址));
			model.addAttribute("kehulaiyuan", extSettingService.list(es.getClubId(), ExtSettingCategory.客户来源));
			model.addAttribute("lianxiFangshi", extSettingService.list(es.getClubId(), ExtSettingCategory.联系方式));
			model.addAttribute("yibaoYisheng", extSettingService.list(es.getClubId(), ExtSettingCategory.医保医生));
			model.addAttribute("genders", Gender.GENDER_LIST);
			model.addAttribute("daijianDaipeiEums", EnumSet.allOf(ExtDaijianDaipei.class));
			
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return EXT_BUSINESS_PATH + "consumptionOrder";
	}

	@GetMapping("/consumption/dinggao/full")
	public String listFullDinggao(HttpServletRequest request, Model model,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
			ExtBusinessCategory category,boolean showErrorOnly) {
		try {
			
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			model.addAttribute("yibaolist", statisicsService.listFullDinggao(dateFrom, dateTo, es.getClubId(), category));
			model.addAttribute("showErrorOnly", showErrorOnly);
			model.addAttribute("huizong", statisicsService.getSummarizing(dateFrom, dateTo, es.getClubId(), category));
			model.addAttribute("dateFrom", dateFrom);
			model.addAttribute("dateTo", dateTo);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return EXT_BUSINESS_PATH + "fullDinggao";
	}
	
	@GetMapping("/consumption/order/summary")
	public String listConsumptionOrderSummaryByDate(HttpServletRequest request, Model model,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
			ExtBusinessCategory category) {
		try {
		    LocalDate endDate=LocalDate.now();
	        LocalDate startDate = endDate.with(TemporalAdjusters.firstDayOfMonth());
	        int  cycleIndex=0;
	        if (dateFrom==null && dateTo==null) {
	           dateFrom=startDate;
	           dateTo=endDate;
	           cycleIndex=(int)(dateTo.toEpochDay()-dateFrom.toEpochDay()+1);
	        }else if (dateFrom==null || dateTo==null) {
	            cycleIndex=1;
	        }else {
	            cycleIndex=(int)(dateTo.toEpochDay()-dateFrom.toEpochDay()+1);
	        }
			
	        EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			model.addAttribute("dinggaolist",
					extConsumptionOrderService.listConsumptionOrderSummaryByDate(dateFrom, dateTo, category,cycleIndex,es.getClubId()));
			model.addAttribute("dateFrom", dateFrom);
			model.addAttribute("dateTo", dateTo);
			model.addAttribute("category", category);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return EXT_BUSINESS_PATH + "consumptionOrderList";
	}

	/*
	 * 最终定稿查看
	 */
	@GetMapping("/consumption/order/finalization")
	public String listFinalization(String currentStatus,ExtConsumptionOrder order, HttpServletRequest request, Model model) {
		try {
			
			 EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			 order.setClubId(es.getClubId());
			 model.addAttribute("order",order);
			 model.addAttribute("orders",extConsumptionOrderService.listWithFinalization(order));
			 model.addAttribute("employees", employeeservice.list(""));
			 model.addAttribute("currentStatus", currentStatus);
		}  catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return EXT_BUSINESS_PATH + "consumptionOrderFinalization";
	}

	
	@GetMapping("/setting/list")
	public String listSettings(ExtSetting condition,HttpServletRequest request,Model model,Integer pageNum,Integer pageSize) {
		try {
			EmployeeSessionManager es=new EmployeeSessionManager(request.getSession());
			condition.setClubId(es.getClubId());
			model.addAttribute("settings",extSettingService.list(condition,pageNum,pageSize));
			model.addAttribute("categories", ExtSettingCategory.values());
			model.addAttribute("condition",condition);
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg",e.getMessage());
		}
		return EXT_BUSINESS_PATH + "setting";
	}

	
	/*
	 * 手工分配的查询和调整
	 */
	
	@GetMapping("/consumption/order/item/treatment/distribution")
	public String treatmentDistribution(HttpServletRequest request, Model model, String itemUuid) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			Map<String, Object> itemInfo = itemService.selectItem4Shougong(itemUuid);
			if (itemInfo == null || itemInfo.size() == 0) {
				throw new ShidaoException("没有该记录。");
			} else if (Float.parseFloat(String.valueOf(itemInfo.get("zhiliaoFee"))) == 0.0) {
				throw new ShidaoException("没有治疗费。");
			}
			model.addAttribute("clubName", es.getClubName());
			model.addAttribute("itemInfo", itemInfo);
			model.addAttribute("distributions", distributionService.listByItemUuid(itemUuid));
			model.addAttribute("doctors", employeeservice.listByPosition(es.getClubId(), Position.zhiliaoPositions));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return EXT_BUSINESS_PATH + "treatmentDistribution";
	}
	
 	
	@GetMapping("/shougong/list")
	public String listTreatmentByDate(HttpServletRequest request, Model model,ExtConsumptionOrder order) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			order.setClubId(es.getClubId());
			model.addAttribute("treatments", distributionService.list(order));
			model.addAttribute("order", order);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return EXT_BUSINESS_PATH + "handworkTreatmentList";
	}
	 
	@GetMapping("/consumptionDailySummary")
	public String consumptionDailySummary(HttpServletRequest request,Model model,@RequestParam(required = true) LocalDate date,
			@RequestParam(required = true) ExtBusinessCategory category) {
		try {
			EmployeeSessionManager es=new EmployeeSessionManager(request.getSession());

			model.addAttribute("dailyStatus",dailyStatusService.selectDailyStatus(new ExtConsumptionOrderDailyStatus(date, category, es.getClubId())));
			List<ExtConsumptionOrderDailySummary> summaries=dailySummaryService.list(es.getClubId(), date, category);
			model.addAttribute("dinggaoShuju",summaries.stream().filter(t -> t.getSource().equals("定稿数据")).collect(Collectors.toList()) );
			model.addAttribute("yibaoXitong",summaries.stream().filter(t -> t.getSource().equals("医保系统")).collect(Collectors.toList()) );
			model.addAttribute("date", date);
			model.addAttribute("category", category);
			model.addAttribute("dailySummary",dailySummaryService.list(es.getClubId(), date, category));
		}
		catch (Exception e) {
			model.addAttribute("errorMsg",e.getMessage());
		}
		return EXT_BUSINESS_PATH + "consumptionDailySummary";
	}

	@GetMapping("/customerManagement")
	public String customerManagement(HttpServletRequest request,Model model, ExtCustomer condition, 
			Integer pageSize, Integer pageNum) {
		try {
			EmployeeSessionManager es=new EmployeeSessionManager(request.getSession());
			condition.setClubId(es.getClubId());
			model.addAttribute("condition", condition);
			model.addAttribute("customers", customerService.list(condition, pageNum, pageSize));
			model.addAttribute("requestURI",request.getRequestURI());

		} catch (Exception e) {
			model.addAttribute("errorMsg",e.getMessage());
		}
		
		return EXT_BUSINESS_PATH + "customerManagement";
	}

}
