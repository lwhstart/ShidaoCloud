package com.shidao.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.MzMaizhenyiHistory;
import com.shidao.model.MzMaizhenyiHistoryDetailFzc;
import com.shidao.model.SdbsCustomer;
import com.shidao.service.MzMaizhenyiHistoryDetailFzcService;
import com.shidao.service.MzMaizhenyiHistoryService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ListResult;
import com.shidao.util.MZYNoAvailableCountException;

@RestController
@RequestMapping("/mzMaizhenyiHistory")
public class MzMaizhenyiHistoryController extends BaseController {

	/*
	 * History Main INfo
	 */
	@Autowired
	private MzMaizhenyiHistoryService maizhenyiHistoryService;

	/**
	 * 查询脉诊记录列表
	 * 
	 * @param operatorId
	 * @param customerId
	 * @param customerName
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject getHistoryList(HttpServletRequest request, MzMaizhenyiHistory condition, Integer pageNum, Integer pageSize) {
		try {
			EmployeeSessionManager es  = new EmployeeSessionManager(request.getSession());
			condition.setCompletedCondition(true);
			condition.setClubId(es.getClubId());

			if (pageNum == null)
				pageNum = 1;
			if (pageSize == null) {
				pageSize = 100;
			}
			ListResult<MzMaizhenyiHistory> historyResult = maizhenyiHistoryService.list(condition, pageNum, pageSize);

			return JsonUtil.succeedJson(historyResult.getList());

		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 查询脉诊记录列表
	 * 
	 * @param operatorId
	 * @param customerId
	 * @param customerName
	 * @return
	 */
	@RequestMapping(value = "/last/{customerId}", method = RequestMethod.GET)
	public JSONObject getHistoryList(@PathVariable Integer customerId) {
		try {
			return JsonUtil.succeedJson(maizhenyiHistoryService.getLastOfCustomer(customerId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/customerLastN/{customerUuid}/{count}", method = RequestMethod.GET)
	public JSONObject getHistoryList(@PathVariable(name="customerUuid") String customerId,
			@PathVariable(name="count") Integer count) {
		try {
			MzMaizhenyiHistory condition = new MzMaizhenyiHistory();
			condition.setCustomer(new SdbsCustomer());
			condition.getCustomer().setUuid(customerId);
			return JsonUtil.succeedJson(maizhenyiHistoryService.list(condition, 1, count).getList());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/detail/{historyId}", method = RequestMethod.GET)
	public JSONObject getById(@PathVariable(value = "historyId") Integer historyId) {
		try {
			Map<String, Object> map = JsonUtil.succeedJson();
			MzMaizhenyiHistory history = maizhenyiHistoryService.selectByPrimaryKey(historyId);			
			map.put("history", history);
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public JSONObject getByUuid(@PathVariable(value = "uuid") String uuid, Boolean isSimple) {
		try {
			return JsonUtil.succeedJson(isSimple == null || !isSimple ?
					maizhenyiHistoryService.selectByUuid(uuid) :
					maizhenyiHistoryService.selectSimpleByUuid(uuid)
					);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 插入记录
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/history/last/{customerId}", method = RequestMethod.GET)
	public JSONObject getLastHistory(@PathVariable(value = "customerId") Integer customerId) {
		try {
			MzMaizhenyiHistory condition = new MzMaizhenyiHistory();
			condition.setCustomerId(customerId);
			condition.setDescending(true);
			ListResult<MzMaizhenyiHistory> historyResult = maizhenyiHistoryService.list(condition, 1, 1);
			return JsonUtil.succeedJson(historyResult.get(0));

		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(HttpServletRequest request, MzMaizhenyiHistory history) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			history.setClubId(es.getClubId());
			history.setOperatorId(es.getEmployeeId());
			maizhenyiHistoryService.insertSelective(history);
			return JsonUtil.succeedJson(history.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(MzMaizhenyiHistory history) {
		try {
			maizhenyiHistoryService.updateByPrimaryKeySelective(history);
			return JsonUtil.succeedJson(history.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/{id}/diseases", method = RequestMethod.GET)
	public JSONObject getDiseases(@PathVariable Integer id, String category) {
		try {
			return JsonUtil.succeedJson(maizhenyiHistoryService.calculateDiseasesForMaizhenyiHistory(id, category));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/complete", method = RequestMethod.POST)
	public JSONObject complete(MzMaizhenyiHistory history){
		try {
			return JsonUtil.succeedJson(maizhenyiHistoryService.completeMzMaizhenyiHistory(history));
		}
		catch(MZYNoAvailableCountException e){
			JSONObject result = new JSONObject();
			result.put("status", "MZYNoAvailableCount");
			return result; 
		}
		catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	/* -------------------
	 *  FZC detail
	 */
	@Autowired
	private MzMaizhenyiHistoryDetailFzcService detailFzcService;

	
	@RequestMapping(value = "/item/fzc/insert", method = RequestMethod.POST)
	public JSONObject insert(MzMaizhenyiHistoryDetailFzc detail) {
		try {
			detailFzcService.insertSelective(detail);
			return JsonUtil.succeedJson(detail.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/item/fzc/getByThreeKey", method = RequestMethod.GET)
	public JSONObject selectByThreeKey(Integer historyId,String qimai,String pulseCheckMethod) {
		try {
			return JsonUtil.succeedJson(detailFzcService.selectByThreeKey(historyId, qimai, pulseCheckMethod));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/item/fzc/update", method = RequestMethod.POST)
	public JSONObject update(MzMaizhenyiHistoryDetailFzc detail) {
		try {
			detailFzcService.updateByPrimaryKeySelective(detail);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/item/fzc/detail/{id}", method = RequestMethod.GET)
	public JSONObject detail(@PathVariable Integer id) {
		try {
			return JsonUtil.succeedJson(detailFzcService.selectByPrimaryKey(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
