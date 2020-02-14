package com.shidao.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.MRPrescriptionCategory;
import com.shidao.enums.PDFPrescriptionCategory;
import com.shidao.model.TcmMedicalRecordPrescription;
import com.shidao.service.DataAnalysisService;
import com.shidao.service.TcmMedicalRecordPrescriptionService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/tcmMedicalRecordPrescription")
public class TcmMedicalRecordPrescriptionController extends BaseController {
	@Autowired
	private TcmMedicalRecordPrescriptionService medicalRecordPrescriptionService;
	
	@Autowired
	private DataAnalysisService dataAnalysisService;

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(TcmMedicalRecordPrescription medicalRecordPrescription, @RequestParam(required=false) List<String> diagnosisItems, PDFPrescriptionCategory pdfCategory) {
		try {
			if(medicalRecordPrescription.getCategory() == MRPrescriptionCategory.Gaobu) {
				return JsonUtil.succeedJson(medicalRecordPrescriptionService.insertGaofang(medicalRecordPrescription.getMedicalRecordId()));
			}
			else {
				medicalRecordPrescriptionService.insertWithDiagnosis(medicalRecordPrescription,diagnosisItems);				
				return JsonUtil.succeedJson(medicalRecordPrescription);
			}
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id) {
		try {
			medicalRecordPrescriptionService.deleteByPrimaryKey(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@PostMapping(value = "/update")
	public JSONObject update(TcmMedicalRecordPrescription medicalRecordPrescription) {
		try {
			Map<String, Object> map = new HashMap<>();
			medicalRecordPrescriptionService.updateByPrimaryKeySelective(medicalRecordPrescription);
			map.put("amount", medicalRecordPrescriptionService.getAmount(medicalRecordPrescription.getId()));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 增加膏方，同时增加方部和个膏部，暂时支持一个膏方
	 * 
	 * @param medicalRecordId
	 * @return
	 */
	@RequestMapping(value = "/addGaofang", method = RequestMethod.POST)
	public JSONObject insertGaofang(Integer medicalRecordId) {
		try {
			return JsonUtil.succeedJson(medicalRecordPrescriptionService.insertGaofang(medicalRecordId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/deleteGaofang", method = RequestMethod.POST)
	public JSONObject deleteGaofang(Integer medicalRecordId) {
		try {
			medicalRecordPrescriptionService.deleteGaofang(medicalRecordId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月26日
	 * 功能:对比供应商药方详情
	 * @param prescriptionId
	 * @param comparisonVendorId
	 * @return
	 */
	@RequestMapping(value = "/getComparisonVendorPrescription", method = RequestMethod.GET)
	public JSONObject getComparisonVendorPrescription(Integer prescriptionId,Integer comparisonVendorId) {
		try {
			return JsonUtil.succeedJson(dataAnalysisService.getComparisonVendorPrescription(prescriptionId, comparisonVendorId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping(value = "/generateWithDiagnosis")
	public JSONObject generateWithDiagnosis(Integer prescriptionId,Integer comparisonVendorId) {
		try {
			return JsonUtil.succeedJson(dataAnalysisService.getComparisonVendorPrescription(prescriptionId, comparisonVendorId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 复制处方到新的病历， 
	 * 如果目标已经有膏方了，不能复制膏方
	 * 非药品类处方，因为一个病例只有一个，所以只能把源项目拼接到新病例的对应处方中。
	 *	@param request
	 *	@param srcPUuid 源处方uuid
	 *	@param destMRUuid 目标病历uuid
	 *	@return
	 * @author yzl , Created at 2019年11月21日
	 *
	 */
	@PostMapping(value = "/copy")
	public JSONObject copyPrescription(HttpServletRequest request, String srcPUuid, String destMRUuid) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			medicalRecordPrescriptionService.copyPrescription2Medical(srcPUuid, destMRUuid);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
}