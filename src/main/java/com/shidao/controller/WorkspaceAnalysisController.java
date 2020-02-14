package com.shidao.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shidao.enums.AnalysisMethod;
import com.shidao.enums.Position;
import com.shidao.enums.ProductCategory;
import com.shidao.enums.YbMembership;
import com.shidao.model.DataAnalysis;
import com.shidao.model.SdbsVendor;
import com.shidao.model.SdcomClub;
import com.shidao.service.DataAnalysisService;
import com.shidao.service.SdbsVendorService;
import com.shidao.service.SdcomClubService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.PermissionEnum;
import com.shidao.util.ShidaoException;
import com.shidao.vo.FuZhenCondition;
import com.shidao.vo.PrescriptionProfitDetailVO;

@Controller
@RequestMapping("/workspace/analysis")
public class WorkspaceAnalysisController extends BaseController {
	private static final String ANALYSIS_PATH = "/display/workspace/analysis/";
	
	@Autowired
	private DataAnalysisService dataAnalysisService;
	
	@Autowired
	private SdcomClubService clubService;
	
	@Autowired
	private SdbsVendorService vendorService;

	/**
	 * 一、客户消费分析
	 * zzp 2018-11-1
	 * @param model
	 * @param dataAnalysis
	 * @param request
	 * @return
	 */
	@GetMapping("/customerConsumptionAnalysis")
	public String getCustomerConsumptionAnalysis(Model model,DataAnalysis dataAnalysis,HttpServletRequest request) {
		try {
			this.getRootAndMonth(model, dataAnalysis, request);
			model.addAllAttributes(dataAnalysisService.getCustomerConsumptionAnalysis(dataAnalysis));
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e);
		}
		return ANALYSIS_PATH + "customerConsumptionAnalysis";
	}
	
	/**
	 * 二、医生数据分析
	 * zzp 2018-11-1
	 * @param model
	 * @param dataAnalysis
	 * @param request
	 * @return
	 */
	@GetMapping(value="/doctorDataAnalysis")
	public String getDoctorDataAnalysis(Model model,DataAnalysis dataAnalysis,HttpServletRequest request) {
		try {
			 this.getRootAndMonth(model, dataAnalysis, request);
			 model.addAttribute("positions", Position.zhiliaoPositions);
			 model.addAttribute("doctorDataAnalysis", dataAnalysisService.getDoctorDataAnalysis(dataAnalysis));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return ANALYSIS_PATH + "doctorDataAnalysis";
	}
	
	/**
	 * 三、产品分析
	 * zzp 2018-11-1
	 * @param model
	 * @param dataAnalysis
	 * @param request
	 * @return
	 */
	@GetMapping(value="/productAnalysis")
	public String getProductAnalysis(Model model,DataAnalysis dataAnalysis,HttpServletRequest request) {
		try {
			this.getRootAndMonth(model, dataAnalysis, request);
			List<Position> positions = Arrays.asList(Position.Assistant,Position.Therapist,Position.Doctor,Position.ClubManager);
			model.addAttribute("productAnalysis", dataAnalysisService.getProductAnalysis(dataAnalysis));
			model.addAttribute("positions", positions);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return ANALYSIS_PATH + "productAnalysis";
	}
	
	/**
	 * 四、理疗分析
	 * zzp 2018-11-2
	 * @param model
	 * @param dataAnalysis
	 * @param request
	 * @return
	 */
	@GetMapping(value="/liliaoDataAnalysis")
	public String getLiliaoDataAnalysis(Model model,DataAnalysis dataAnalysis,YbMembership ybMembership,HttpServletRequest request) {
		try {
			this.getRootAndMonth(model, dataAnalysis, request);
			model.addAttribute("ybMembership", ybMembership);
			model.addAllAttributes(dataAnalysisService.getLiliaoDataAnalysis(dataAnalysis,ybMembership));
			model.addAttribute("analysisMethods", AnalysisMethod.analysisMethods);
			model.addAttribute("ybMemberships", YbMembership.list);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return ANALYSIS_PATH + "liliaoDataAnalysis";
	}
	
	/**
	 * 五、治疗数据分析
	 * @param model
	 * @param dataAnalysis
	 * @param request
	 * @return
	 */
	@GetMapping(value="/prescriptionSummary")
	public String getPrescriptionSummary(Model model,DataAnalysis dataAnalysis,HttpServletRequest request) {
		try {
			this.getRootAndMonth(model, dataAnalysis, request);
			model.addAllAttributes(dataAnalysisService.getPrescriptionSummary(dataAnalysis));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return ANALYSIS_PATH + "prescriptionSummary";
	}
	
	/**
	 * 六、1.处方针对年龄/性别分析
	 * @param model
	 * @param dataAnalysis
	 * @param request
	 * @return
	 */
	@GetMapping(value="/prescriptionAnalysis")
	public String getPrescriptionAnalysis(Model model,DataAnalysis dataAnalysis,YbMembership ybMembership,HttpServletRequest request) {
		try {
			this.getRootAndMonth(model, dataAnalysis, request);
			model.addAllAttributes(dataAnalysisService.getPrescriptionAnalysis(dataAnalysis,ybMembership));
			model.addAttribute("analysisMethods", AnalysisMethod.analysisMethods);
			model.addAttribute("ybMemberships", YbMembership.list);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return ANALYSIS_PATH + "prescriptionAnalysis";
	}
	
	/**
	 * 六、2.年龄段或性别十大疾病
	 * @param model
	 * @param dataAnalysis
	 * @param request
	 * @return
	 */
	@GetMapping(value="/diseaseRatioAnalysis")
	public String getDiseaseRatioAnalysis(Model model,DataAnalysis dataAnalysis,HttpServletRequest request, YbMembership ybMembership) {
		try {
			this.getRootAndMonth(model, dataAnalysis, request);
			if(ybMembership == null) {
				ybMembership = YbMembership.MembershipCard;
			}
			model.addAttribute("ybMembership", ybMembership);
			model.addAllAttributes(dataAnalysisService.getDiseaseRatioAnalysis(dataAnalysis, ybMembership));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return ANALYSIS_PATH + "diseaseRatioAnalysis";
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
	public void getRootAndMonth(Model model,DataAnalysis dataAnalysis,HttpServletRequest request,List<String> annotation) throws ShidaoException{
		if (annotation != null) {
			model.addAttribute("annotation", annotation);
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
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月23日
	 * 功能:处方利润率 
	 * @param model
	 * @param clubId
	 * @param date
	 * @param request
	 * @return
	 */
	@GetMapping(value="/prescriptionProfitListPerday")
	public String prescriptionProfitListPerday(Model model,Integer clubId,Date date,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (sm.hasPermission(PermissionEnum.PrescriptionDataAnalysis)) {
				if(sm.hasPermission(PermissionEnum.CHAIN_PERMISSION)) {
					model.addAttribute("clubs", clubService.list(sm.getClubType()));
				}else if (sm.hasPermission(PermissionEnum.CLUB_PERMISSION)) {
					clubId = sm.getClubId();
				}else {
					throw new ShidaoException("没有权限");
				}
			}
			Map<String, Object> map = new HashMap<>();
			if (date == null)
				date = new Date();
			map.put("date", date);
			map.put("clubId", clubId);
			model.addAttribute("prescriptionProfit", dataAnalysisService.listPrescriptionProfit(clubId, date));
			model.addAttribute("condition", map);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return ANALYSIS_PATH + "prescriptionProfitListPerday";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月26日
	 * 功能:处方利润率详情
	 * @param model
	 * @param prescriptionId
	 * @param request
	 * @return
	 */
	@GetMapping(value="/prescriptionProfitDetail")
	public String prescriptionProfitDetail(Model model,Integer prescriptionId,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			Map<String, Object> condition = new HashMap<>();
			condition.put("prescriptionId", prescriptionId);
			Map<String, Object> map = dataAnalysisService.getPrescriptionProfitDetail(prescriptionId);
			PrescriptionProfitDetailVO vo = (PrescriptionProfitDetailVO)map.get("analysisDetail");
			SdbsVendor vendor = new SdbsVendor();
			vendor.setWarehouseId(clubService.getWarehouseIdByCategoryAndClubId(vo.getClubId(), ProductCategory.parse(vo.getCategory())));
			model.addAttribute("vendors", vendorService.list(vendor));
			model.addAllAttributes(map);
			model.addAttribute("condition", condition);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return ANALYSIS_PATH + "prescriptionProfitDetail";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月26日
	 * 功能:利润率统计
	 * @param model
	 * @param startDate
	 * @param endDate
	 * @param request
	 * @return
	 */
	@GetMapping(value="/prescriptionProfitSummary")
	public String prescriptionProfitSummary(Model model,Date startDate,Date endDate,Integer clubId,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (sm.hasPermission(PermissionEnum.PrescriptionDataAnalysis)) {
				if(sm.hasPermission(PermissionEnum.CHAIN_PERMISSION)) {
					List<SdcomClub> clubs = clubService.list(sm.getClubType());
					model.addAttribute("clubs", clubs);
					if (clubId == null) {
						clubId = clubs.get(0).getId();
					}
				}else if (sm.hasPermission(PermissionEnum.CLUB_PERMISSION)) {
					clubId = sm.getClubId();
				}else {
					throw new ShidaoException("没有权限");
				}
			}
			Map<String, Object> condition = new HashMap<>();
			if (startDate == null)
				startDate = new Date();
			if (endDate == null)
				endDate = new Date();
			condition.put("startDate", startDate);
			condition.put("endDate", endDate);
			condition.put("clubId", clubId);
			model.addAttribute("summary",dataAnalysisService.getprescriptionProfitSummary(startDate, endDate, clubId));
			model.addAttribute("condition", condition);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return ANALYSIS_PATH + "prescriptionProfitSummary";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月4日<br>
	 * 功能:单个药对比
	 * @param model
	 * @param medicineId
	 * @param clubId
	 * @param request
	 * @return
	 */
	@GetMapping(value="/medicineProfitComparison")
	public String medicineProfitComparison(Model model,Integer medicineId,Integer clubId,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			Integer warehouseId = clubService.getWarehouseIdByCategoryAndClubId(clubId, ProductCategory.Keliji);
			model.addAttribute("comparisonDetail",dataAnalysisService.getMedicineProfitComparison(medicineId, warehouseId));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return ANALYSIS_PATH + "medicineProfitComparison";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月27日<br>
	 * 功能:复诊率列表
	 * @param model
	 * @param request
	 * @param source
	 * @param condition
	 * @return
	 */
	@GetMapping(value="/fuzhenlv4Disease/{source}")
	public String fuzhenlv4Disease(Model model,HttpServletRequest request,@PathVariable("source") YbMembership source,FuZhenCondition condition) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setSource(source);
			model.addAllAttributes(dataAnalysisService.getFuzhenlv4Disease(source,sm.getClubId(),condition.getMonthStart(),condition.getMonthEnd()));
			model.addAttribute("condition",condition);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return ANALYSIS_PATH + "fuzhenlv4Disease";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月27日<br>
	 * 功能:复诊客户列表
	 * @param model
	 * @param request
	 * @param source
	 * @param condition
	 * @return
	 */
	@GetMapping(value="/fuzhenlist/{source}")
	public String fuzhenlist(Model model,HttpServletRequest request,@PathVariable("source") YbMembership source,FuZhenCondition condition) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setSource(source);
			condition.setClubId(sm.getClubId());
			model.addAttribute("condition",condition);
			model.addAllAttributes(dataAnalysisService.getFuzhenData(condition));
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return ANALYSIS_PATH + "fuzhenlist";
	}
}
