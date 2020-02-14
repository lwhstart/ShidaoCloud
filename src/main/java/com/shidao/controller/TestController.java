package com.shidao.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.AlgorithmName;
import com.shidao.enums.ClubType;
import com.shidao.enums.DiagnosisCategory;
import com.shidao.enums.ExtSettingCategory;
import com.shidao.enums.Gender;
import com.shidao.enums.MedicineProcessingMethod;
import com.shidao.enums.PDFPrescriptionCategory;
import com.shidao.enums.Position;
import com.shidao.enums.MRPrescriptionCategory;
import com.shidao.enums.SupportPayType;
import com.shidao.enums.ProductCategory;
import com.shidao.enums.YbMembership;
import com.shidao.model.DataAnalysis;
import com.shidao.model.DatePeriodParameter;
import com.shidao.model.ExtConsumptionOrder;
import com.shidao.model.SdbsPeriod;
import com.shidao.model.SdbsVendor;
import com.shidao.model.SdepPayCategory;
import com.shidao.model.TcmMedicalRecord;
import com.shidao.model.TcmMedicalRecordPrescription;
import com.shidao.model.TcmMedicine;
import com.shidao.model.WmsInventory;
import com.shidao.model.WmsInventoryWarningSetting;
import com.shidao.service.AutoLearnService;
import com.shidao.service.DataAnalysisService;
import com.shidao.service.ExtConsumptionOrderService;
import com.shidao.service.ExtSettingService;
import com.shidao.service.SdbsAppointmentService;
import com.shidao.service.SdbsServiceCardService;
import com.shidao.service.SdbsVendorService;
import com.shidao.service.SdcomClubService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.service.SdepAlgorithmDescriptionService;
import com.shidao.service.SdepPayCategoryService;
import com.shidao.service.StatisticsService;
import com.shidao.service.TcmDiagnosisReportService;
import com.shidao.service.TcmDiseaseService;
import com.shidao.service.TcmMedicalRecordPrescriptionService;
import com.shidao.service.TcmMedicalRecordService;
import com.shidao.service.TcmSyndromeService;
import com.shidao.service.WmsInventoryService;
import com.shidao.service.WmsInventoryWarningSettingService;
import com.shidao.service.YbService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.vo.FuZhenCondition;
import com.shidao.vo.PrescriptionProfitDetailVO;

@RestController
@RequestMapping("/TEST")
public class TestController extends BaseController {
	@Autowired
	private StatisticsService statisticsService;
	
	@Autowired
	private DataAnalysisService dataAnalysisService;
	
	@Autowired
	private SdcomClubService clubService;
	
	@Autowired
	private SdepAlgorithmDescriptionService descriptionService;
	
	@Autowired
	private SdbsVendorService vendorService;
	
	@Autowired
	private SdbsAppointmentService appointmentService;
	
	@Autowired
	private WmsInventoryWarningSettingService warningSettingService;
	
	@Autowired
	private WmsInventoryService inventoryService;
	
	@Autowired
	private YbService ybService;
	
	@Autowired
	private TcmDiseaseService diseaseService;
	
	@Autowired
	private SdepPayCategoryService payCategoryService;
	
	@Autowired
	private TcmDiagnosisReportService reportService;
	
	@Autowired
	private TcmSyndromeService syndromeService;
	
	@Autowired
	ExtConsumptionOrderService extConsumptionOrderService;
	
	@Autowired
	SdcomEmployeeService employeeservice;
	
	@Autowired
	ExtSettingService extSettingService;
	
	@GetMapping("/consumption/order")
	public JSONObject getOrders(HttpServletRequest request,ExtConsumptionOrder condition) {
		try {

			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			condition.setClubId(es.getClubId());
			List<ExtConsumptionOrder> orders = extConsumptionOrderService.list(condition);
			JSONObject result =JsonUtil.succeedJson();
			result.put("orders", orders);
			result.put("assistants",employeeservice.listByPosition(es.getClubId(), Position.Assistant));
			result.put("jiesuanFangshiList",extSettingService.list(es.getClubId(), ExtSettingCategory.结算方式));
			result.put("kaZhongList",extSettingService.list(es.getClubId(), ExtSettingCategory.卡种));
			result.put("kuaidiDizhiList",extSettingService.list(es.getClubId(), ExtSettingCategory.快递地址));
			result.put("lianxiFangshi",extSettingService.list(es.getClubId(), ExtSettingCategory.联系方式));
			result.put("genders",Gender.GENDER_LIST);
			return result; //  JsonUtil.succeedJson(request);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@GetMapping(value="/dinggao")
	public JSONObject dinggao(DatePeriodParameter datePeriodParameter,Integer clubId) {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("dinggao", statisticsService.selectByPayDate(datePeriodParameter, clubId));
			map.put("clubs", clubService.list(ClubType.ShidaoClinic));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		
		}
	}
	
	@GetMapping(value="/customerConsumptionAnalysis")
	public JSONObject getCustomerConsumptionAnalysis(DataAnalysis dataAnalysis) {
		try {
			return JsonUtil.succeedJson(dataAnalysisService.getCustomerConsumptionAnalysis(dataAnalysis));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/doctorDataAnalysis")
	public JSONObject getDoctorDataAnalysis(DataAnalysis dataAnalysis) {
		try {
			return JsonUtil.succeedJson(dataAnalysisService.getDoctorDataAnalysis(dataAnalysis));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/productAnalysis")
	public JSONObject getProductAnalysis(DataAnalysis dataAnalysis) {
		try {
			return JsonUtil.succeedJson(dataAnalysisService.getProductAnalysis(dataAnalysis));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/liliaoDataAnalysis")
	public JSONObject getLiliaoDataAnalysis(DataAnalysis dataAnalysis) {
		try {
//			Map<String, Object> map = dataAnalysisService.getLiliaoDataAnalysis(dataAnalysis);
//			map.put("analysisMethods", AnalysisMethod.analysisMethods);
//			return JsonUtil.succeedJson(map);
			return null;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
//	@GetMapping(value="/prescriptionSummary")
//	public JSONObject getPrescriptionSummary(DataAnalysis dataAnalysis) {
//		try {
//			Map<String, Object> map = new HashMap<>();
//			map.put("prescriptionSummary", dataAnalysisService.getPrescriptionSummary(dataAnalysis));
//			return JsonUtil.succeedJson(map);
//		} catch (Exception e) {
//			return JsonUtil.errjson(e);
//		}
//	}
	
//	@GetMapping(value="/prescriptionAnalysis")
//	public JSONObject getPrescriptionAnalysis(DataAnalysis dataAnalysis) {
//		try {
//			Map<String, Object> map = dataAnalysisService.getPrescriptionAnalysis(dataAnalysis);
//			map.put("analysisMethods", AnalysisMethod.analysisMethods);
//			return JsonUtil.succeedJson(map);
//		} catch (Exception e) {
//			return JsonUtil.errjson(e);
//		}
//	}
	
//	@GetMapping(value="/diseaseRatioAnalysis")
//	public JSONObject getDiseaseRatioAnalysis(DataAnalysis dataAnalysis) {
//		try {
//			return JsonUtil.succeedJson(dataAnalysisService.getDiseaseRatioAnalysis(dataAnalysis));
//		} catch (Exception e) {
//			return JsonUtil.errjson(e);
//		}
//	}
	
	@GetMapping(value="/prescription/amount")
	public JSONObject getPrescriptionAmount(DataAnalysis dataAnalysis){
		try {
			return JsonUtil.succeedJson(statisticsService.getPrescriptionAmount(dataAnalysis));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/description")
	public JSONObject description(AlgorithmName name){
		try {
			return JsonUtil.succeedJson(descriptionService.getDescription(name));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@Autowired
	private SdbsServiceCardService serviceCardService;
	
	@GetMapping(value="/select/serviceCard")
	public JSONObject selectServiceCarsd(Integer id){
		try {
			return JsonUtil.succeedJson(serviceCardService.selectByPrimaryKey(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/listPrescriptionProfit")
	public JSONObject listPrescriptionProfit(Integer clubId,Date date){
		try {
			Map<String, Object> map = new HashMap<>();
			Map<String, Object> condition = new HashMap<>();
			map.put("clubs", clubService.list(ClubType.ShidaoClinic));
			map.put("prescriptionProfit", dataAnalysisService.listPrescriptionProfit(clubId, date));
			condition.put("clubId", clubId);
			condition.put("date", date);
			map.put("condition", condition);
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/prescriptionProfitDetail")
	public JSONObject listPrescriptionProfit(Integer prescriptionId,HttpServletRequest request){
		try {
			Map<String, Object> map = new HashMap<>();
			Map<String, Object> condition = new HashMap<>();
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Map<String, Object> reMap = dataAnalysisService.getPrescriptionProfitDetail(prescriptionId);
			PrescriptionProfitDetailVO vo = (PrescriptionProfitDetailVO)reMap.get("analysisDetail");
			SdbsVendor vendor = new SdbsVendor();
			vendor.setWarehouseId(clubService.getWarehouseIdByCategoryAndClubId(sm.getClubId(), ProductCategory.parse(vo.getCategory())));
			map.put("vendors", vendorService.list(vendor));
			map.putAll(reMap);
			condition.put("prescriptionId", prescriptionId);
			map.put("condition", condition);
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/prescriptionProfitSummary")
	public JSONObject prescriptionProfitSummary(Date startDate,Date endDate,Integer clubId){
		try {
			Map<String, Object> map = new HashMap<>();
			Map<String, Object> condition = new HashMap<>();
			if (startDate == null)
				startDate = new Date();
			if (endDate == null)
				endDate = new Date();
			map.put("clubs", clubService.list(ClubType.ShidaoClinic));
			map.put("summary",dataAnalysisService.getprescriptionProfitSummary(startDate, endDate, clubId));
			condition.put("startDate", startDate);
			condition.put("endDate", endDate);
			condition.put("clubId", clubId);
			map.put("condition", condition);
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/medicineProfitComparison")
	public JSONObject medicineProfitComparison(Integer medicineId,Integer clubId){
		try {
			Map<String, Object> map = new HashMap<>();
			Integer warehouseId = clubService.getWarehouseIdByCategoryAndClubId(clubId, ProductCategory.Keliji);
			map.put("comparisonDetail", dataAnalysisService.getMedicineProfitComparison(medicineId, warehouseId));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/doctor/work/detail")
	public JSONObject doctorDaily(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,Integer clubId,String clubName,
			HttpServletRequest request){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Map<String, Object> map = new HashMap<>();
			Map<String, Object> condition = new HashMap<>();
			date = date == null?LocalDate.now():date;
			List<SdbsPeriod> doctorDaily = appointmentService.getDoctorDaily(sm.getEmployeeId(), date, clubId);
			map.put("doctorDailies", doctorDaily);
			map.put("customerNum", doctorDaily.stream().filter(a->a.getAppointments() != null && !a.getAppointments().isEmpty()).map(SdbsPeriod::getAppointments).collect(Collectors.toList()).size());
			condition.put("date", date);
			condition.put("clubName", clubName);
			condition.put("clubId", clubId);
			map.put("condition", condition);
			map.put("isToday", date.isEqual(LocalDate.now()));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/doctor/works")
	public JSONObject doctorWorks(HttpServletRequest request){
		try {
			Map<String, Object> map = new HashMap<>();
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			map.putAll(appointmentService.getDoctorWorks(sm.getEmployeeId()));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/inventory/warning")
	public JSONObject inventoryWarning(HttpServletRequest request,WmsInventoryWarningSetting condition){
		try {
			Map<String, Object> map = new HashMap<>();
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (condition.getCategory() == null) {
				condition.setCategory(ProductCategory.Keliji);
			}
			map.put("condition", condition);
			map.put("categories", Arrays.asList(ProductCategory.Keliji,ProductCategory.Caoyao));
			/*map.put("requestURI", request.getRequestURI());*/
			condition.setWarehouseId(sm.getWarehouseId());
			condition.addTypeWarningParams();
			map.put("warningList",warningSettingService.list(condition));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/medicine/inventory/amount")
	public JSONObject medicineInventoryAmount(HttpServletRequest request,Integer medicineId,String medicineName,ProductCategory category){
		try {
			Map<String, Object> map = new HashMap<>();
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Map<String, Object> condition = new HashMap<>();
			condition.put("medicineName", medicineName);
			List<WmsInventory> list = inventoryService.listProductInventory(medicineId, sm.getWarehouseId(),category);
			map.put("inventoryList", list);
			map.put("condition", condition);
			map.put("total", list.stream().filter(a->a.getAmount()>0).mapToDouble(b->b.getAmount()).sum());
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/inventory/warning/setting")
	public JSONObject inventoryWarningSetting(HttpServletRequest request,String name,Boolean haveSetting,ProductCategory category){
		try {
			Map<String, Object> map = new HashMap<>();
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			TcmMedicine condition = new TcmMedicine();
			condition.setName(name);
			condition.addHaveSettingParams(haveSetting);
			condition.setWarningSetting(new WmsInventoryWarningSetting());
			condition.getWarningSetting().setCategory(category);
			condition.getWarningSetting().setWarehouseId(sm.getWarehouseId());
			List<TcmMedicine> list = warningSettingService.listInventoryWarning(condition);
			map.put("warningSettingList", list);
			map.put("count", list.size());
			map.put("condition", condition);
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/fuzhenlv4Disease/{source}")
	public JSONObject fuzhenlv4Disease(HttpServletRequest request,@PathVariable("source") YbMembership source,FuZhenCondition condition){
		try {
			Map<String, Object> map = new HashMap<>();
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setSource(source);
			map.putAll(dataAnalysisService.getFuzhenlv4Disease(source,sm.getClubId(),condition.getMonthStart(),condition.getMonthEnd()));
			map.put("condition",condition);
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/fuzhenlist/{source}")
	public JSONObject fuzhenlist(HttpServletRequest request,@PathVariable("source") YbMembership source,FuZhenCondition condition){
		try {
			Map<String, Object> map = new HashMap<>();
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setSource(source);
			condition.setClubId(sm.getClubId());
			map.put("condition",condition);
			map.putAll(dataAnalysisService.getFuzhenData(condition));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/medicalRecords")
	public JSONObject medicalRecords(HttpServletRequest request,FuZhenCondition condition){
		try {
			Map<String, Object> map = new HashMap<>();
			EmployeeSessionManager.checkLogin(request.getSession());
			map.put("condition", condition);
			map.put("medicalRecord", ybService.getYbMedicalRecordList(condition));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/payCategorys")
	public JSONObject payCategorys(SdepPayCategory condition) {
		try {
			condition.addParameter("enabled", 1);
			condition.addParameter("supportPayTypes", Arrays.asList(SupportPayType.Nonmembership,SupportPayType.Both));
			return JsonUtil.succeedJson(payCategoryService.list(condition));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value = "payCategory/management")
	public JSONObject getManagement(Integer club_id,HttpServletRequest request) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			SdepPayCategory condition = new SdepPayCategory();	
			condition.setClubId(club_id);
			map.put("categories", payCategoryService.list(condition,1,Integer.MAX_VALUE).getList());
			JSONObject result = JsonUtil.succeedJson();
			result.putAll(map);
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value = "/medicalRecord/relate/diseases")
	public JSONObject relatedDiseases(String uuid) {
		try {
			JSONObject result = JsonUtil.succeedJson();
			result.put("bingzhongs",diseaseService.listBingzhong());
			result.put("report", reportService.selectAllByMedicalRecordUuid(uuid, DiagnosisCategory.疾病));
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value = "/medicalRecord/relate/syndromes")
	public JSONObject relatedSyndromes(String uuid) {
		try {
			JSONObject result = JsonUtil.succeedJson();
			result.put("qimais",syndromeService.listQimaiZangfu());
			result.put("report", reportService.selectAllByMedicalRecordUuid(uuid, DiagnosisCategory.症候群));
			result.put("record",mrService.selectSimpleByUuid(uuid));
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@Autowired
	private TcmMedicalRecordService recordService;
	@GetMapping(value = "/medicalRecord/edit/{uuid}")
	public JSONObject getMedicalRecordinfo(@PathVariable("uuid") String uuid) {
		try {
			TcmMedicalRecord record = recordService.selectByUuid(uuid);
			JSONObject result = JsonUtil.succeedJson();
			List<TcmMedicalRecordPrescription> prescriptions = record.getPrescriptions();
			Map<MRPrescriptionCategory, Object> notMedicinePrescription = new LinkedHashMap<>();
			for (MRPrescriptionCategory category : MRPrescriptionCategory.notMedicine) {
				notMedicinePrescription.put(category,
						prescriptions.stream().filter(a -> a.getCategory() == category).findFirst().orElse(null));
				prescriptions.removeIf(a -> a.getCategory() == category);
			}
			result.put("notMedicinePrescription", notMedicinePrescription);
			
			result.put("report", reportService.selectAllByMedicalRecordUuid(uuid, null));
			result.put("lastDiagnosisInfo", mrService.selectLastDiagnosisReportInfor(uuid));
			
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@Autowired
	private TcmMedicalRecordService mrService;
	@GetMapping(value = "/relate/medicalrecord")
	public JSONObject relateMedicalRecord(Integer medicalRecordId) {
		try {
			return JsonUtil.succeedJson(mrService.insertDiagnosisReport4MedicalRecord(medicalRecordId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping("/medicalRecord/{medicalRecordUuid}/createPrescription")
	public JSONObject relatePrescriptoinWithDiagnosisReport(HttpServletRequest request, Model model,
			@PathVariable("medicalRecordUuid") String medicalRecordUuid,PDFPrescriptionCategory pdfCategory,
			MedicineProcessingMethod method) {
		try {
			JSONObject result = JsonUtil.succeedJson();
			result.put("report", reportService.selectAllByMedicalRecordUuid(medicalRecordUuid,null));
			result.put("record", mrService.selectSimpleByIdOrUuid(null,medicalRecordUuid));
			result.put("pdfCategories", EnumSet.allOf(PDFPrescriptionCategory.class));
			result.put("methods", EnumSet.allOf(MedicineProcessingMethod.class));
			result.put("pdfCategory", pdfCategory==null?PDFPrescriptionCategory.Manual : pdfCategory);
			result.put("method", method==null? MedicineProcessingMethod.Keliji: method);
			
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@Autowired
	private TcmMedicalRecordPrescriptionService mrPrescriptionService;
	
	@Autowired
	private TcmMedicalRecordPrescriptionService prescriptionService;
	
	@GetMapping("/medicalRecord/prescription/{prescriptionUuid}/diagnosisInfo")
	public JSONObject managePrescriptionDiagnosisInfo(HttpServletRequest request,
			@PathVariable("prescriptionUuid") String prescriptionUuid) {
		try {
			JSONObject result = JsonUtil.succeedJson();
			
			result.put("report", mrPrescriptionService.getDiagnosisInfo(prescriptionUuid,true));
			result.put("record", mrPrescriptionService.selectMedicalRecord(prescriptionUuid));
			result.put("pdfCategories", EnumSet.allOf(PDFPrescriptionCategory.class));
			result.put("methods", EnumSet.allOf(MedicineProcessingMethod.class));			
			result.put("prescription", prescriptionService.selectByUuid(prescriptionUuid));
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@Autowired
	private AutoLearnService autoLearnService;
	
	@GetMapping("/autoLearn/prescription")
	public JSONObject generatePrescription(Integer[] diseases, Integer[] syndromes) {
		try {
			return JsonUtil.succeedJson(autoLearnService.listPrescription(diseases, syndromes));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
	@GetMapping("/medicalRecord/prescription/{prescriptionUuid}/editWithAutoLearn")
	public JSONObject generatePrescription(@PathVariable("prescriptionUuid") String prescriptionUuid) {
		try {
			JSONObject result = JsonUtil.succeedJson();
			TcmMedicalRecordPrescription prescription = mrPrescriptionService.selectByUuid(prescriptionUuid);
			result.put("prescription", prescription);
			result.put("autoLearnItems",autoLearnService.listPrescription4MRPrescription(prescriptionUuid));
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
