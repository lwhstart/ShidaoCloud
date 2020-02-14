/**
 * 
 */
package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.TcmHealthAssessment;
import com.shidao.service.TcmHealthAssessmentService;
import com.shidao.util.JsonUtil;
import com.shidao.util.ListResult;
import com.shidao.util.ShidaoException;

/**
 * @author zhangzhuping
 *
 */
@RestController
@RequestMapping(value = "/tcmHealthAssessment")
public class TcmHealthAssessmentController extends BaseController {

	@Autowired
	private TcmHealthAssessmentService tcmHealthAssessmentService;

	/**
	 * 插入测试数据返回id和TcmResult()
	 * 
	 * @param tcmHealthAssessment
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(TcmHealthAssessment tcmHealthAssessment) {
		try {
			tcmHealthAssessmentService.insertSelective(tcmHealthAssessment);
			Integer id = tcmHealthAssessment.getId();
			JSONObject json = JsonUtil.succeedJson(id);
			json.put("tcmResult", tcmHealthAssessmentService.selectByPrimaryKey(id).getTcmResult());
			return json;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 评测记录列表
	 * 
	 * @param tcmHealthAssessment
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(TcmHealthAssessment tcmHealthAssessment, Integer pageSize) {
		try {
			Integer customerId = tcmHealthAssessment.getCustomerId();
			if (customerId == null)
				throw new ShidaoException("请输入用户id");

			if (pageSize == null)
				pageSize = 2;

			tcmHealthAssessment.setDescending(true);

			ListResult<TcmHealthAssessment> tcmHealthAssessments = tcmHealthAssessmentService.list(tcmHealthAssessment,
					1, pageSize);
			return JsonUtil.succeedJson(tcmHealthAssessments);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 获取最近一次病和症的评测选择
	 * 
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/last/assessmentRecords/{customerId}", method = RequestMethod.GET)
	public JSONObject getLastAssessmentRecords(@PathVariable Integer customerId) {
		try {
			return JsonUtil.succeedJson(tcmHealthAssessmentService.getLastAssessmentRecords(customerId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 注册完成插入之前评测好的记录
	 * 
	 * @param tcmHealthAssessment(id,customerId)
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(TcmHealthAssessment tcmHealthAssessment) {
		try {
			tcmHealthAssessmentService.updateByPrimaryKeySelective(tcmHealthAssessment);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/{assessmentId}/tizhiManagement", method = RequestMethod.GET)
	public JSONObject getTizhiManagement(@PathVariable Integer assessmentId) {
		try {
			return JsonUtil.succeedJson(tcmHealthAssessmentService.getTizhiManagement(assessmentId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 
	 * @param assessmentId
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public JSONObject getDisease(@PathVariable Integer id) {
		try {
			return JsonUtil.succeedJson(tcmHealthAssessmentService.selectByPrimaryKey(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/diseases/{customerId}", method = RequestMethod.GET)
	public JSONObject getDiseasaae(@PathVariable Integer customerId) {
		try {		
			return JsonUtil.succeedJson(tcmHealthAssessmentService.getDiseasesOfCustomer(customerId));
			
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}

	}

	@RequestMapping(value = "/{assessmentId}/solutions", method = RequestMethod.GET)
	public JSONObject getSolutions(@PathVariable Integer assessmentId) {
		try {
			return JsonUtil.succeedJson(tcmHealthAssessmentService.getSolutions(assessmentId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
}
