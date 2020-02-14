package com.shidao.controller;

import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.shidao.model.ExtConsumptionOrderFinalization;
import com.shidao.enums.ExtApprovalStatus;
import com.shidao.enums.ExtBusinessCategory;
import com.shidao.enums.IncomeProjectCategory;
import com.shidao.enums.IncomeSource;
import com.shidao.model.ExtConsumptionOrder;
import com.shidao.model.ExtConsumptionOrderDailyStatus;
import com.shidao.model.ExtConsumptionOrderDailySummary;
import com.shidao.model.ExtConsumptionOrderItem;
import com.shidao.model.ExtTreatmentDistribution;
import com.shidao.model.SdepDrawSetting;
import com.shidao.service.ExtConsumptionOrderDailyStatusService;
import com.shidao.service.ExtConsumptionOrderDailySummaryService;
import com.shidao.service.ExtConsumptionOrderFinalizationService;
import com.shidao.service.ExtConsumptionOrderItemService;
import com.shidao.service.ExtConsumptionOrderService;
import com.shidao.service.ExtTreatmentDistributionService;
import com.shidao.service.SdepDrawSettingService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping("/ext/consumption/order")
public class ExtConsumptionOrderController extends BaseController {

    @Autowired
    ExtConsumptionOrderService extConsumptionOrderService;
    @Autowired
    ExtConsumptionOrderItemService extConsumptionOrderItemService;
    @Autowired
    ExtConsumptionOrderFinalizationService extOrderFinalizationService;
    @Autowired
    SdepDrawSettingService drawSettingService;
    @Autowired
    ExtTreatmentDistributionService treatmentDistributionService;
	@Autowired
	ExtConsumptionOrderDailySummaryService dailySummaryService;
	
    @Autowired
    ExtConsumptionOrderDailyStatusService dailyStatusService;

    @PostMapping("/insert")
    public JSONObject insertOrder(ExtConsumptionOrder order, String srcOrderUuid, HttpServletRequest request) {
        try {

            EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
            order.setClubId(es.getClubId());
            return JsonUtil.succeedJson(extConsumptionOrderService.insertOrder(order, srcOrderUuid));
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @GetMapping("/list")
    public JSONObject list(ExtConsumptionOrder order, Integer pageNum, Integer pageSize) {
        try {
            return JsonUtil.succeedJson(extConsumptionOrderService.list(order));
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/update")
    public JSONObject update(ExtConsumptionOrder order) {
        try {
            extConsumptionOrderService.updateByPrimaryKeySelective(order);
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/delete")
    public JSONObject delete(String uuid) {
        try {
            extConsumptionOrderService.deleteByUuid(uuid);
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @GetMapping("/item/list")
    public JSONObject listItem(ExtConsumptionOrderItem item) {
        try {
            return JsonUtil.succeedJson(extConsumptionOrderItemService.list(item));
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/item/delete")
    public JSONObject deleteItem(ExtConsumptionOrderItem item) {
        try {
            extConsumptionOrderItemService.deleteBySelective(item);
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/item/insert")
    public JSONObject insertItem(ExtConsumptionOrderItem item) {
        try {
            extConsumptionOrderItemService.insertSelective(item);
            return JsonUtil.succeedJson(item);
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/item/update")
    public JSONObject updateItem(ExtConsumptionOrderItem item) {
        try {
            extConsumptionOrderItemService.updateByPrimaryKeySelective(item,false);
            return JsonUtil.succeedJson(item);
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/finalization/update")
    public JSONObject updateFinalization(ExtConsumptionOrderFinalization finalization, HttpServletRequest request) {
        try {
            EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
            finalization.setLastModifiedId(es.getEmployeeId());
            extOrderFinalizationService.updateByPrimaryKeySelective(finalization, false);
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/approve")
    public JSONObject extApproveConsumptionOrder(String uuid, HttpServletRequest request) {
        try {
            EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
            extConsumptionOrderService.extApproveConsumptionOrder(uuid, es.getEmployeeId());
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/cancelApproval")
    public JSONObject extCancelApproval(String uuid, HttpServletRequest request) {
        try {
            EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
            extConsumptionOrderService.extCancelApproval(uuid, es.getEmployeeId());
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @GetMapping("/drawSetting/selIncomeEnum")
    public JSONObject selIncomeEnum(IncomeSource payType) {
        try {
            if (payType == IncomeSource.医保) {
                return JsonUtil.succeedJson(IncomeProjectCategory.values());
            } else {
                return JsonUtil.succeedJson();
            }
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/drawSetting/update")
    public JSONObject updateDrawSetting(SdepDrawSetting drawSetting) {
        try {
            drawSettingService.updateByPrimaryKeySelective(drawSetting);
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/drawSetting/delete")
    public JSONObject delDrawSetting(Integer id) {
        try {
            drawSettingService.deleteByPrimaryKey(id);
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/drawSetting/insert")
    public JSONObject insertDrawSetting(SdepDrawSetting drawSetting, HttpServletRequest request) {
        try {
            EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
            drawSetting.setClubId(es.getClubId());
            drawSettingService.insertSelective(drawSetting);
            return JsonUtil.succeedJson(drawSetting);
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/item/treatmentDistribution/insert")
    public JSONObject insertTreament(ExtTreatmentDistribution treament, HttpServletRequest request) {
        try {
            EmployeeSessionManager.checkLogin(request.getSession());
            treatmentDistributionService.insertSelective(treament);
            return JsonUtil.succeedJson(treament);
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/item/treatmentDistribution/update")
    public JSONObject updateTreament(ExtTreatmentDistribution treament, HttpServletRequest request) {
        try {
            EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
            treament.setLastModifiedId(es.getEmployeeId());
            treatmentDistributionService.updateByPrimaryKeySelective(treament);
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    @PostMapping("/item/treatmentDistribution/delete")
    public JSONObject deletetTreament(String uuid, HttpServletRequest request) {
        try {
            EmployeeSessionManager.checkLogin(request.getSession());
            treatmentDistributionService.deleteByUuid(uuid);
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }

    /**
     *   提交订单
     */
    @PostMapping("/submit")
    public JSONObject submitConsumptionOrder(@RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,HttpServletRequest request,ExtBusinessCategory category) {
        try {
            
           EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
           extConsumptionOrderService.submit(date, es.getClubId(), category, es.getEmployeeId());
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }
    
    @PostMapping("/cancelSubmit")
    public JSONObject cancelSubmitConsumptionOrder(@RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,HttpServletRequest request,ExtBusinessCategory category) {
        try {
            EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
            extConsumptionOrderService.extCancelSubmitConsumptionOrder(date, es.getClubId(), category,es.getEmployeeId());
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }
    
    @PostMapping("/approveByDate")
    public JSONObject batchExtApproveConsumptionOrder(@RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,HttpServletRequest request,ExtBusinessCategory category) {
        try {
            EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
           extConsumptionOrderService.extApproveConsumptionOrderByDate(date, es.getClubId(), category, es.getEmployeeId());
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }
    
    @PostMapping("/cancelApproveByDate")
    public JSONObject cancelApproveConsumptionOrder(@RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,HttpServletRequest request,ExtBusinessCategory category) {
        try {
            EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
            extConsumptionOrderService.extCancelApproveConsumptionOrderByDate(date, es.getClubId(), category, es.getEmployeeId());
            return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
    }
    
	@PostMapping("/dailySummary/update")
	public JSONObject dailySummaryUpdate(ExtConsumptionOrderDailySummary summary,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			dailySummaryService.updateByPrimaryKeySelective(summary);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping("/generateDailySummary")
	public JSONObject generateDailySummary(@RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,HttpServletRequest request,ExtBusinessCategory category) {
	    try {
	         EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
	           ExtConsumptionOrderDailyStatus dailyStatus = dailyStatusService.selectDailyStatus(new ExtConsumptionOrderDailyStatus(date, category, es.getClubId()));
	            if (!StringUtils.isEmpty(dailyStatus)&&dailyStatus.getStatus() == ExtApprovalStatus.已创建) {
	                   extConsumptionOrderService.generateDailySummary(date,es.getClubId(),category,es.getEmployeeId());
	            }
	        return JsonUtil.succeedJson();
        } catch (Exception e) {
            return JsonUtil.errjson(e);
        }
	}
}
