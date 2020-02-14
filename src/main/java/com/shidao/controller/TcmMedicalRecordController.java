package com.shidao.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.DiagnosisCategory;
import com.shidao.enums.MRPrescriptionCategory;
import com.shidao.enums.MedicalRecordStatus;
import com.shidao.enums.PDFPrescriptionCategory;
import com.shidao.model.SdbsCustomer;
import com.shidao.model.TcmExpertPrescriptionDisease;
import com.shidao.model.TcmHealthAssessment;
import com.shidao.model.TcmMedicalRecord;
import com.shidao.model.TcmMedicalRecordPrescription;
import com.shidao.service.SdbsCustomerService;
import com.shidao.service.TcmDiagnosisReportItemService;
import com.shidao.service.TcmExpertPrescriptionDiseaseService;
import com.shidao.service.TcmHealthAssessmentService;
import com.shidao.service.TcmMaizhenService;
import com.shidao.service.TcmMedicalRecordPrescriptionService;
import com.shidao.service.TcmMedicalRecordService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ListResult;
import com.shidao.util.NotEnoughOperationCountException;
import com.shidao.util.ShidaoException;
import com.shidao.util.StringUtil;
import com.shidao.vo.YunmaiUI;

@RestController
@RequestMapping(value = "/tcmMedicalRecord")

public class TcmMedicalRecordController extends BaseController {
	@Autowired
	private TcmMedicalRecordService tcmMedicalRecordService;

	@Autowired	
	private TcmMaizhenService maizhenService;

	@Autowired
	private TcmExpertPrescriptionDiseaseService expertPrescriptionDiseaseService;

	@Autowired
	private TcmHealthAssessmentService healthAssessmentService;
	
	@Autowired
	private TcmMedicalRecordPrescriptionService medicalRecordPrescriptionService;
	
	@Autowired
	private SdbsCustomerService customerService;
	
	@Autowired
	private TcmDiagnosisReportItemService diagnosisReportItemService;

	/*
	 * 获取病历列表，，可以使用以下参数: customerId, appointmentId等等
	 * 
	 * pageNum： 第几页，如没有，则默认为1 pageSize：每页几条数据，若不设置，则默认为1.
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(TcmMedicalRecord condition, Integer pageNum, Integer pageSize) {
		try {
			if (pageNum == null)
				pageNum = 1;
			if (pageSize == null) {
				pageSize = 1;
			}
			return JsonUtil.succeedJson(tcmMedicalRecordService.listWithDetail(condition, pageNum, pageSize));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/*
	 * 新增一条病历记录
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(TcmMedicalRecord tcmMedicalRecord) {
		try {
			tcmMedicalRecordService.insertSelective(tcmMedicalRecord);
			return JsonUtil.succeedJson(tcmMedicalRecord.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(TcmMedicalRecord tcmMedicalRecord) {
		try {
			tcmMedicalRecordService.updateByPrimaryKeySelective(tcmMedicalRecord);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 获取最后一次病史记录
	 * 
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/last/{customerId}", method = RequestMethod.GET)
	public JSONObject getLast(@PathVariable Integer customerId) {
		try {
			ListResult<TcmMedicalRecord> last = tcmMedicalRecordService.getLast(customerId);
			return JsonUtil.succeedJson(last.get(0));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 插入 病历
	 * 
	 * @param appointmentId
	 * @return
	 */
	@RequestMapping(value = "/retrieve/{appointmentId}")
	public JSONObject retrieveMedicalReportOfAppointment(@PathVariable Integer appointmentId) {
		try {
			TcmMedicalRecord medicalRecord = tcmMedicalRecordService.retrieveMedicalReportOfAppointment(appointmentId,null);
			return JsonUtil.succeedJson(medicalRecord);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/copy")
	public JSONObject copyLast(@RequestParam(value = "oldUuid") String oldUuid,
			@RequestParam(value = "newUuid") String newUuid) {
		try {
			tcmMedicalRecordService.copyMedicalRecord(oldUuid, newUuid);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping(value = "/selecCopy")
	public JSONObject selectCopyLast(@RequestParam(value = "oldUuid") String oldUuid,
			@RequestParam(value = "newUuid") String newUuid, String prescriptionId, String prescribes) {
		try {
			tcmMedicalRecordService.copySelectMedicalRecord(oldUuid, newUuid,prescriptionId,prescribes);
			return JsonUtil.succeedJson(); 
			} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	// 病历界面导入v空间自我评测和未病智能分析仪检测
	@RequestMapping(value = "/healthAssessmentMaizhenyi/list/{customerId}", method = RequestMethod.GET)
	public JSONObject healthAssessmentMaizhenyiList(@PathVariable("customerId") Integer customerId) {
		try {
			Map<String, Object> result = JsonUtil.succeedJson();
			List<TcmHealthAssessment> list = healthAssessmentService.getHealthAssessmentMaizhenyi(customerId);
			result.put("list", list);
			return JsonUtil.succeedJson(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return JsonUtil.errjson(e);
		}
	}

	// 修改药方
	@RequestMapping(value = "/makePrescriptionUpdatable", method = RequestMethod.POST)
	public JSONObject makePrescriptionUpdatable(@RequestParam Integer id) {
		try {
			tcmMedicalRecordService.makePrescriptionUpdatable(id);
			return JsonUtil.succeedJson(tcmMedicalRecordService.getStatusById(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/{uuid}/{sequence}", method = RequestMethod.GET)
	public JSONObject getCustomerMedicalRecord(@PathVariable(name = "uuid") String uuid,
			@PathVariable(name = "sequence") Integer sequence, Integer exceptId) {
		try {
			TcmMedicalRecord condition = new TcmMedicalRecord();
			condition.setId(exceptId);
			condition.setCustomer(new SdbsCustomer());
			condition.getCustomer().setUuid(uuid);

			ListResult<TcmMedicalRecord> recordList = tcmMedicalRecordService.list(condition, sequence, 1);
			if (recordList.isEmpty())
				return JsonUtil.emptyJson();
			TcmMedicalRecord medicalRecord = tcmMedicalRecordService.selectByPrimaryKey(recordList.get(0).getId());
			List<TcmMedicalRecordPrescription> prescriptions = medicalRecord.getPrescriptions();
			JSONObject result = JsonUtil.succeedJson();
			result.put("navigation", recordList);
			result.put("detail", medicalRecord);
			if (prescriptions.size()>0) {
				Map<MRPrescriptionCategory, Object> notMedicinePrescription = new LinkedHashMap<>();
				for (MRPrescriptionCategory category : MRPrescriptionCategory.notMedicine) {
					notMedicinePrescription.put(category, prescriptions.stream().filter(a->a.getCategory()==category).findFirst().orElse(null));
					prescriptions.removeIf(a->a.getCategory() == category);
				}
				result.put("notMedicinePrescription", notMedicinePrescription);
			}
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/updateByPrimaryKeyDirectly", method = RequestMethod.POST)
	public JSONObject updateByPrimaryKeyDirectly(TcmMedicalRecord medicalRecord) {
		try {
			tcmMedicalRecordService.updateByPrimaryKeyDirectly(medicalRecord);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/status/{id}", method = RequestMethod.GET)
	public JSONObject getStatusById(@PathVariable(value = "id") Integer id) {
		try {
			return JsonUtil.succeedJson(tcmMedicalRecordService.getStatusById(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	

	/**
	 * 使用自动处方开药
	 * @author 袁志林 2018-3-26
	 * @param medicalRecordId 病历编号
	 * @param diseaseIds 疾病列表， 使用“,”分割疾病id，可空
	 * @param yunmai 运脉，可空
	 * @param pdfCategory 预定义处方类型
	 * @param category 病历处方类型
	 * @return
	 */
	@RequestMapping(value = "/prescriptionAI", method = RequestMethod.POST)
	public JSONObject generatePrescriptionAI(
			HttpServletRequest request,
			Integer medicalRecordId,
			String medicalRecordUuid,
			String diseaseIds,
			String yunmai, 
			PDFPrescriptionCategory pdfCategory,
			MRPrescriptionCategory category,
			@RequestParam(required=false) List<String> diagnosisItems) {
		try {
			medicalRecordId = medicalRecordId!= null ? medicalRecordId :
				tcmMedicalRecordService.selectSimpleByUuid(medicalRecordUuid).getId();
			tcmMedicalRecordService.checkPrescriptionEditable(medicalRecordId, null, null);
			JSONObject expertPrescription = JsonUtil.succeedJson();
			diseaseIds = StringUtil.isNullOrEmpty(diseaseIds ) ? 
						diagnosisReportItemService.selectProjectIdsOfUUids(diagnosisItems, DiagnosisCategory.疾病):
						diseaseIds;
			// 检查是否含有特殊疾病（糖尿病），如果有，则支持返回
			if (!StringUtil.isNullOrEmpty(diseaseIds)) {
				for(String diseaseId : diseaseIds.split(",")){
					TcmExpertPrescriptionDisease specialPrescription= expertPrescriptionDiseaseService
							.selectByDiseaseId(Integer.parseInt(diseaseId));
					if(specialPrescription != null){
						expertPrescription.put("expertPrescription", specialPrescription);
						return expertPrescription;
					}
				}
			}
			// 专家处方
			TcmMedicalRecord expert = tcmMedicalRecordService.generatePrescriptionWithAI(medicalRecordId, diseaseIds, yunmai, pdfCategory, category, diagnosisItems);
			expertPrescription.put("prescription", expert.getPrescriptions().get(0));
			expertPrescription.put("expertPrescription", maizhenService.selectPulseSolutionByMai(yunmai));
			expertPrescription.put("amount", medicalRecordPrescriptionService.getAmount(expert.getPrescriptions().get(0).getId()));
			return expertPrescription;
		} catch (NotEnoughOperationCountException e) {
			JSONObject result = JsonUtil.errjson(e);
			result.put("errorType", "NotEnoughOperationCountException");
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 使用自动处方开药
	 * @author 袁志林 2018-3-26
	 * @param medicalRecordId 病历编号
	 * @param diseaseIds 疾病列表， 使用“,”分割疾病id，可空
	 * @param yunmai 运脉，可空
	 * @param pdfCategory 预定义处方类型
	 * @param category 病历处方类型
	 * @return
	 */	
	@RequestMapping(value = "/prescriptionAIFull", method = RequestMethod.POST)
	public JSONObject generatePrescriptionAIFull(
			HttpServletRequest request,
			String medicalRecordUuid,
			String diagnosisItemUuids,
			String yunmai, 
			PDFPrescriptionCategory pdfCategory,
			MRPrescriptionCategory category,
			@RequestParam(required=false) List<String> diagnosisItems) {
		try {
			int medicalRecordId =tcmMedicalRecordService.selectSimpleByUuid(medicalRecordUuid).getId();
			tcmMedicalRecordService.checkPrescriptionEditable(medicalRecordId, null, null);
			JSONObject expertPrescription = JsonUtil.succeedJson();
			
			// 专家处方
			TcmMedicalRecord expert = tcmMedicalRecordService.generatePrescriptionWithAIFull(medicalRecordUuid, diagnosisItemUuids, yunmai, pdfCategory, category, diagnosisItems);
			expertPrescription.put("prescription", expert.getPrescriptions().get(0));
			expertPrescription.put("expertPrescription", maizhenService.selectPulseSolutionByMai(yunmai));
			expertPrescription.put("amount", medicalRecordPrescriptionService.getAmount(expert.getPrescriptions().get(0).getId()));
			return expertPrescription;
		} catch (NotEnoughOperationCountException e) {
			JSONObject result = JsonUtil.errjson(e);
			result.put("errorType", "NotEnoughOperationCountException");
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月11日
	 * 功能:专家操作，发送建议
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/sendSuggest", method = RequestMethod.POST)
	public JSONObject sendSuggest(Integer id) {
		try {
			TcmMedicalRecord condition = new TcmMedicalRecord();
			condition.setId(id);
			condition.setStatus(MedicalRecordStatus.Suggested);
			tcmMedicalRecordService.updateByPrimaryKeySelective(condition);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月11日
	 * 功能:远程终端操作，完善病例
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/sendFill", method = RequestMethod.POST)
	public JSONObject sendFill(Integer id) {
		try {
			TcmMedicalRecord condition = new TcmMedicalRecord();
			condition.setId(id);
			condition.setStatus(MedicalRecordStatus.Filled);
			tcmMedicalRecordService.updateByPrimaryKeySelective(condition);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping(value = "/operate")
	public JSONObject operate(HttpServletRequest request, Integer id, MedicalRecordStatus nextStatus) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			tcmMedicalRecordService.operate(id, nextStatus, esManager.getEmployeeId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 病历进入到下一步状态、
	 *	@param request
	 *	@param uuid 病历的uuid
	 *	@param currentStatus 当前的病历状态，用以验证病历状态是否已经更新了。
	 *	@return
	 * @author yzl , Created at 2019年11月9日
	 *
	 */
	@PostMapping(value = "/stepNext")
	public JSONObject stepNext(HttpServletRequest request, String uuid, MedicalRecordStatus currentStatus) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			tcmMedicalRecordService.stepNext(uuid, currentStatus);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月12日
	 * 功能:
	 * @param request
	 * @param uuid 病历编号
	 * @param excludedUuid 不查找指定的uuid， 用在对比病例中
	 * @param needPager 是否需要分页信息，默认是true
	 * @param customerUuid 指定客户的uuid，表示查找客户的最新病历
	 * 
	 * @return
	 */
	@GetMapping(value = "/detail/{uuid}")
	public JSONObject getDetailByUuid(HttpServletRequest request, @PathVariable("uuid")String uuid,
			String excludedUuid, Boolean needPager,String customerUuid) {
		try {
//			EmployeeSessionManager.checkLogin(request.getSession());
			
			if(!StringUtil.isNullOrEmpty(customerUuid)) {
				SdbsCustomer customer = customerService.selectByUuid(customerUuid);
				if(customer == null) {
					throw new ShidaoException("无效的客户uuid。");
				}
				uuid = tcmMedicalRecordService.getLastUuidOfCustomer(customer.getId(),null);
			}
			JSONObject result = JsonUtil.succeedJson();
			result.put("detail", tcmMedicalRecordService.selectByUuid(uuid));
			if(needPager == null || needPager) {
				result.putAll(tcmMedicalRecordService.getPagerInfo(uuid, excludedUuid));
			}
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 获得运脉的列表
	 *	@param request
	 *	@param yunmai
	 *	@return
	 * @author yzl , Created at 2019年11月30日
	 *
	 */
	@GetMapping(value = "/yunmai/ui")
	public JSONObject getYunaiUI(HttpServletRequest request, String yunmai) {
		try {
			String handlePulses = tcmMedicalRecordService.handlePulses(yunmai);
			return JsonUtil.succeedJson(new YunmaiUI(handlePulses));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}