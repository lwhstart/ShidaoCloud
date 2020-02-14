/**
 * TcmDiagnosisReportController.java
 * Created by :yzl. Created date: 2019年10月17日
 */
package com.shidao.controller;

import java.util.EnumSet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.TreatmentEffect;
import com.shidao.model.TcmDiagnosisReportItem;
import com.shidao.model.TcmDiagnosisReportItemDetail;
import com.shidao.model.TcmDiagnosisSetting;
import com.shidao.service.TcmDiagnosisReportItemDetailService;
import com.shidao.service.TcmDiagnosisReportItemService;
import com.shidao.service.TcmDiagnosisReportService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

/**
 * @author yzl 2019年10月17日
 * 
 */
@RestController
@RequestMapping("/diagnosisReport")
public class TcmDiagnosisReportController extends BaseController {

	@Autowired
	private TcmDiagnosisReportService reportService;

	@Autowired
	private TcmDiagnosisReportItemService itemService;
	
	@Autowired
	private TcmDiagnosisReportItemDetailService detailService;

	/**
	 * 获取疾病或者症候群对应的附加信息
	 * 
	 * @param request
	 * @param 主要是projectId和category
	 * @return
	 * @author yzl , Created at 2019年10月17日
	 *
	 */
	@GetMapping("/settings")
	public JSONObject getSettings(HttpServletRequest request, TcmDiagnosisSetting condition) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			return JsonUtil.succeedJson(reportService.listSettings(condition));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	
	/**
	 * 获取诊断条目，包含详情的设置信息。
	 *	@param request
	 *	@param uuid 条目的uuid
	 *	@return
	 * @author yzl , Created at 2019年12月7日
	 *
	 */
	@GetMapping("/item/withSetting")
	public JSONObject selectWithSetting(HttpServletRequest request, String uuid) {
		try {
			return JsonUtil.succeedJson(itemService.selectWithSettingByUuid(uuid));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 获取诊断条目，包含详情的设置信息。
	 *	@param request
	 *	@param uuid 条目的uuid
	 *	@return
	 * @author yzl , Created at 2019年12月7日
	 *
	 */
	@GetMapping("/item/withDetails")
	public JSONObject selectDetailsByUuid(HttpServletRequest request, String uuid) {
		try {
			return JsonUtil.succeedJson(itemService.selectDetailsByUuid(uuid));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 插入诊断报告的条目，包含条目下具体的附加信息。
	 * 
	 * @param request
	 * @param reportUuid
	 * @param item
	 * @return
	 * @author yzl , Created at 2019年10月17日
	 *
	 */
	@PostMapping("/item/insert")
	public JSONObject insertItem(HttpServletRequest request, String reportUuid, String prescriptionUuid, TcmDiagnosisReportItem item) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			item.setCreatorId(es.getEmployeeId());
			itemService.insertWithRelation(item,reportUuid,prescriptionUuid);
			return JsonUtil.succeedJson(item);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 插入诊断报告的条目，包含条目下具体的附加信息。
	 * 
	 * @param request
	 * @param reportUuid
	 * @param item
	 * @return
	 * @author yzl , Created at 2019年10月17日
	 *
	 */
	@PostMapping("/item/delete")
	public JSONObject deleteItem(HttpServletRequest request, @RequestParam("itemUuid") String itemUuid) {
		try {
			new EmployeeSessionManager(request.getSession());
			itemService.deleteByUuid(itemUuid);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@PostMapping("/item/update")
	public JSONObject updateItem(HttpServletRequest request, TcmDiagnosisReportItem item) {
		try {
			new EmployeeSessionManager(request.getSession());
			itemService.updateByPrimaryKeySelective(item);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 把诊断条目和处方关联起来。
	 *	@param request
	 *	@param prescriptionUuid 处方编号
	 *	@param itemUuid 诊断条目编号
	 *	@param related 是否关联
	 *	@return
	 * @author yzl , Created at 2019年12月4日
	 *
	 */
	@PostMapping(value = "/item/relate/prescription")
	public JSONObject relateDiagnosisItem(HttpServletRequest request,@RequestParam("prescriptionUuid") String prescriptionUuid,  
			@RequestParam("itemUuid")String itemUuid, @RequestParam("related")boolean related) {
		try {
			itemService.relateToPrescription(prescriptionUuid, itemUuid, related);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping("/item/detail/insert")
	public JSONObject insertDetail(HttpServletRequest request, TcmDiagnosisReportItemDetail item, String itemUuid) {
		try {
			EmployeeSessionManager es =  new EmployeeSessionManager(request.getSession());
			item.setCreatorId(es.getEmployeeId());
			detailService.insertSelective(item);
			return JsonUtil.succeedJson(item);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping("/item/detail/update")
	public JSONObject updateDetail(HttpServletRequest request, TcmDiagnosisReportItemDetail item) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());			
			detailService.updateByPrimaryKeySelective(item);
			return JsonUtil.succeedJson(item.getUuid());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping("/listComparison")
	public JSONObject listCoparison(HttpServletRequest request, String medicalRecordUuid) {
		try {		
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());

			JSONObject result =  JsonUtil.succeedJson();
			result.put("difference", reportService.listComparison(medicalRecordUuid, es.getEmployeeId()));
			result.put("treatmentEffects", EnumSet.allOf(TreatmentEffect.class));
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	} 
}
