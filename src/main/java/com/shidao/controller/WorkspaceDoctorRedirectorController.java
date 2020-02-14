/**
 * WorkspaceDoctorRedirectorController.java
 * Created by :yzl. Created date: 2019年10月17日
 */
package com.shidao.controller;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import com.shidao.enums.ClubType;
import com.shidao.enums.DiagnosisCategory;
import com.shidao.enums.MRPrescriptionCategory;
import com.shidao.enums.MedicalRecordStatus;
import com.shidao.enums.MedicineProcessingMethod;
import com.shidao.enums.PDFPrescriptionCategory;
import com.shidao.enums.ProductCategory;
import com.shidao.enums.TreatmentEffect;
import com.shidao.model.MzMaizhenyiHistory;
import com.shidao.model.TcmMedicalRecord;
import com.shidao.model.TcmMedicalRecordPrescription;
import com.shidao.service.AutoLearnService;
import com.shidao.service.MzMaizhenyiHistoryService;
import com.shidao.service.SdbsCustomerService;
import com.shidao.service.SdbsProductService;
import com.shidao.service.SdbsProfessioService;
import com.shidao.service.TcmDiagnosisReportService;
import com.shidao.service.TcmDiseaseService;
import com.shidao.service.TcmMedicalRecordPrescriptionService;
import com.shidao.service.TcmMedicalRecordService;
import com.shidao.service.TcmSyndromeService;
import com.shidao.setting.ClubSetting;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.NotLoginException;
import com.shidao.util.ShidaoException;
import com.shidao.vo.YunmaiUI;

/**
 * @author yzl 2019年10月17日
 * 
 */
@Controller
@RequestMapping("/workspace/doctor")
public class WorkspaceDoctorRedirectorController extends BaseController {
	private static final String WORKSPACE_DOCTOR_PATH = "/display/workspace/doctor/";
	private static final String WORKSPACE_PATH = "/display/workspace/";

	@Autowired
	private TcmMedicalRecordService medicalRecordService;
	
	@Autowired
	private TcmMedicalRecordPrescriptionService medicalRecordPrescriptionService;

	@Autowired
	private TcmDiseaseService diseaseService;

	@Autowired
	private TcmDiagnosisReportService diagnosisReportService;

	@Autowired
	private SdbsProductService productService;

	@Autowired
	private SdbsCustomerService customerService;

	@Autowired
	private SdbsProfessioService professionService;

	@Autowired
	private TcmSyndromeService syndromeService;

	@Autowired
	private TcmMedicalRecordPrescriptionService mrPrescriptionService;
	
	@Autowired
	private AutoLearnService autoLearnService;
	
	@Autowired
	private MzMaizhenyiHistoryService mzyService;

	@GetMapping("/medicalRecord/relate/diseases")
	public String relateDiseases4MedicalRecord(HttpServletRequest request, Model model, String uuid) {
		try {
			model.addAttribute("bingzhongs", diseaseService.listBingzhong());
			model.addAttribute("report",
					diagnosisReportService.selectAllByMedicalRecordUuid(uuid, DiagnosisCategory.疾病));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_DOCTOR_PATH + "relateDiseases4MedicalRecord";
	}

	@GetMapping("/medicalRecord/relate/syndromes")
	public String relateSyndromes4MedicalRecord(HttpServletRequest request, Model model, String uuid) {
		try {
			model.addAttribute("qimais", syndromeService.listQimaiZangfu());
			model.addAttribute("report", diagnosisReportService.selectAllByMedicalRecordUuid(uuid, DiagnosisCategory.症候群));
			TcmMedicalRecord record = medicalRecordService.selectSimpleByUuid(uuid);
			model.addAttribute("record", record);
			MzMaizhenyiHistory condition = new MzMaizhenyiHistory();
			condition.setCustomerId( record.getCustomerId());
			condition.setCompletedCondition(true);
			model.addAttribute("mzList", mzyService.list(condition, 1, 8).getList());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_DOCTOR_PATH + "relateSyndromes4MedicalRecord";
	}

	/**
	 * 通过预约的uuid，生成病历，并且转到病历页面。
	 * 
	 * @param request
	 * @param model
	 * @param uuid
	 * @param stepNext
	 * @return
	 * @throws ShidaoException
	 * @author yzl , Created at 2019年10月11日
	 *
	 */
	@GetMapping("/startDiagnosing")
	public String gotoMedicalRecordByAppointmentUuid(HttpServletRequest request, RedirectAttributesModelMap model,
			String appointmentUuid, String medicalRecordUuid) throws ShidaoException {
		try {
			// 判断是否是医生登录
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			medicalRecordUuid = medicalRecordService.startDagnosing(appointmentUuid, medicalRecordUuid, sm.getEmployeeId())
					.getUuid();
		} catch (Exception e) {
			model.addFlashAttribute("errorMsg", e.getMessage());
		}
		return "redirect:/workspace/doctor/medicalRecord/" + medicalRecordUuid;
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月23日 功能:通过病例的UUID显示病例界面
	 * @param request
	 * @param model
	 * @param uuid
	 * @return
	 * @throws ShidaoException
	 */
	@RequestMapping(value = "/medicalRecord/{uuid}", method = RequestMethod.GET)
	public String gotoMedicineRecord(HttpServletRequest request, Model model, @PathVariable(value = "uuid") String uuid,
			Boolean stepNext, MedicalRecordStatus currentStatus) throws ShidaoException {

		EmployeeSessionManager sm = null;
		try {
			if (model.containsAttribute("errorMsg")) {
				throw new ShidaoException(String.valueOf(model.asMap().get("errorMsg")));
			}
			sm = new EmployeeSessionManager(request.getSession());
			TcmMedicalRecord record;
			if(stepNext != null && stepNext && currentStatus != null) {
				try {
					record =  medicalRecordService.stepNext(uuid, currentStatus);
				} catch (Exception e) {
					record = medicalRecordService.selectByUuid(uuid);
				}
			}else {
				record = medicalRecordService.selectByUuid(uuid);
			}
			List<TcmMedicalRecordPrescription> prescriptions = record.getPrescriptions();
			Map<MRPrescriptionCategory, Object> notMedicinePrescription = new LinkedHashMap<>();
			for (MRPrescriptionCategory category : MRPrescriptionCategory.notMedicine) {
				notMedicinePrescription.put(category,
						prescriptions.stream().filter(a -> a.getCategory() == category).findFirst().orElse(null));
				prescriptions.removeIf(a -> a.getCategory() == category);
			}
			model.addAttribute("notMedicinePrescription", notMedicinePrescription);
			model.addAllAttributes(productService.getProductOfCategory(sm.getClubId(), ProductCategory.Liliao,
					ProductCategory.SelfProduct, ProductCategory.Jiance));
			Map<String, LocalDate> map = medicalRecordService.getFirstLastDate(record.getCustomerId(), uuid, null);
			model.addAttribute("profession", professionService.list());
			model.addAttribute("medicalRecord", record.transferFields2Html());
			model.addAttribute("loginClubType", sm.getClubType());
			model.addAllAttributes(map);
			model.addAttribute("clubSetting", ClubSetting.getSetting(record.getClub().getId()));
			model.addAttribute("pageInfor", medicalRecordService.getPagerInfo(uuid, uuid));
			model.addAttribute("membershipName",
					customerService.selectById(record.getCustomerId(), sm.getClubId()).getMemberType());
			model.addAttribute("isChangePrescription",
					medicalRecordService.getIsChangePrescription(record.getId(), null, null));
			model.addAttribute("fieldRequired", sm.getClubType() == ClubType.ShidaoClinic);
			// Map<MRPrescriptionCategory, List<String>> takingMethods = new HashMap<>();
			EnumSet<MRPrescriptionCategory> categories = EnumSet.of(MRPrescriptionCategory.Keliji,
					MRPrescriptionCategory.Caoyao, MRPrescriptionCategory.Fangbu, MRPrescriptionCategory.Gaobu,
					MRPrescriptionCategory.ZCY);
			Map<MRPrescriptionCategory, List<String>> takingMethods = categories.stream()
					.collect(Collectors.toMap(a -> a, a -> a.getTakingMethods()));

			model.addAttribute("takingMethods", takingMethods);
			model.addAttribute("report", diagnosisReportService.selectAllByMedicalRecordUuid(uuid, null));
			// 上次的诊断报告信息。
			// 是否强制显示复诊初诊选择
			boolean needConfirmFuzhen = record.getStatus() == MedicalRecordStatus.Diagnosing
					&& record.getIsFirstVisit() == null && record.getDate().isEqual(LocalDate.now());
			if (needConfirmFuzhen) {
				Map<String, Object> lastDiagnosisInfo = medicalRecordService.selectLastDiagnosisReportInfor(uuid);
				needConfirmFuzhen = needConfirmFuzhen && (lastDiagnosisInfo != null);
				if (needConfirmFuzhen) {
					model.addAttribute("lastDiagnosisInfo", lastDiagnosisInfo);
				}
			}
			model.addAttribute("needConfirmFuzhen", needConfirmFuzhen);

			model.addAttribute("displayViewDifferenct", record.getIsFirstVisit() != null && !record.getIsFirstVisit());

			if (record.getClub().getType() == ClubType.HZYCooperation) {
				return WORKSPACE_PATH + "importedCase4HZY";
			}

		} catch (NotLoginException e) {
			return "redirect:/customer/medicalRecord/detail/" + uuid;
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e.getMessage());
		} catch (Exception e) {
			model.addAttribute("errorMsg", "系统错误：" + e.getMessage());
		}
//		return WORKSPACE_PATH
//				+ (sm != null && sm.getClubType() == ClubType.ShidaoClinic ? "importedCase_ai" : "importedCase");
		return WORKSPACE_PATH + "importedCase_ai";
	}

	/**
	 * 关联两次诊断报告的对比
	 * 
	 * @param request
	 * @param model
	 * @param medicalRecordUuid
	 * @param lastReportUuid
	 * @return
	 * @author yzl , Created at 2019年10月25日
	 *
	 */
	@GetMapping("/medicalRecord/{medicalRecordUuid}/relateDiagnosisReport")
	public String relateMedicalWithDiagnosisReport(HttpServletRequest request, Model model,
			@PathVariable("medicalRecordUuid") String medicalRecordUuid, Boolean mark) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			if(mark != null && mark) {
				TcmMedicalRecord newRecord = new TcmMedicalRecord();
				newRecord.setUuid(medicalRecordUuid);
				newRecord.setIsFirstVisit(false);				
				medicalRecordService.updateByPrimaryKeySelective(newRecord);
			}
			model.addAttribute("difference",
					diagnosisReportService.listComparison(medicalRecordUuid, es.getEmployeeId()));
			model.addAttribute("treatmentEffects", EnumSet.allOf(TreatmentEffect.class));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_DOCTOR_PATH + "relateMedicalRecord2DiagnosisReport";
	}

	/**
	 * 关联两次诊断报告的对比
	 * 
	 * @param request
	 * @param model
	 * @param medicalRecordUuid
	 * @param lastReportUuid
	 * @return
	 * @author yzl , Created at 2019年10月25日
	 *
	 */
	@GetMapping("/medicalRecord/{medicalRecordUuid}/createPrescription")
	public String relatePrescriptoinWithDiagnosisReport(HttpServletRequest request, Model model,
			@PathVariable("medicalRecordUuid") String medicalRecordUuid, PDFPrescriptionCategory pdfCategory,
			MedicineProcessingMethod method) {
		try {
			String handlePulses = medicalRecordService.handlePulses("");
			model.addAttribute("yunmaiUI", new YunmaiUI(handlePulses));
			new EmployeeSessionManager(request.getSession());
			model.addAttribute("report", diagnosisReportService.selectAllByMedicalRecordUuid(medicalRecordUuid, null));
			model.addAttribute("record", medicalRecordService.selectSimpleByIdOrUuid(null, medicalRecordUuid));
			model.addAttribute("pdfCategories", EnumSet.allOf(PDFPrescriptionCategory.class));
			model.addAttribute("methods", EnumSet.allOf(MedicineProcessingMethod.class));
			model.addAttribute("pdfCategory", pdfCategory == null ? PDFPrescriptionCategory.Manual : pdfCategory);
			model.addAttribute("method", method == null ? MedicineProcessingMethod.Keliji : method);

		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_DOCTOR_PATH + "relateItems4Prescription";
	}

	@GetMapping("/medicalRecord/prescription/{prescriptionUuid}/diagnosisInfo")
	public String managePrescriptionDiagnosisInfo(HttpServletRequest request, Model model,
			@PathVariable("prescriptionUuid") String prescriptionUuid) {
		try {
			new EmployeeSessionManager(request.getSession());
			String handlePulses = medicalRecordService.handlePulses("");
			model.addAttribute("yunmaiUI", new YunmaiUI(handlePulses));
			model.addAttribute("report", mrPrescriptionService.getDiagnosisInfo(prescriptionUuid,true));
			model.addAttribute("record", mrPrescriptionService.selectMedicalRecord(prescriptionUuid));
			model.addAttribute("pdfCategories", EnumSet.allOf(PDFPrescriptionCategory.class));
			model.addAttribute("methods", EnumSet.allOf(MedicineProcessingMethod.class));
			model.addAttribute("prescription", medicalRecordPrescriptionService.selectByUuid(prescriptionUuid));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_DOCTOR_PATH + "relateItems4Prescription";
	}
	
	@GetMapping("/medicalRecord/prescription/{prescriptionUuid}/editWithAutoLearn")
	public String createPrescriptionWithAutoLearn(HttpServletRequest request, Model model,
			@PathVariable("prescriptionUuid") String prescriptionUuid) {
		try {
			model.addAttribute("prescription", medicalRecordPrescriptionService.selectByUuid(prescriptionUuid)); 
			model.addAttribute("prescriptionUuid", prescriptionUuid);
			model.addAttribute("autoLearnItems", autoLearnService.listPrescription4MRPrescription(prescriptionUuid));
		} catch (Exception e) {
			 model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACE_DOCTOR_PATH +"editPrescriptionWithAutoLearn";
	}
}
