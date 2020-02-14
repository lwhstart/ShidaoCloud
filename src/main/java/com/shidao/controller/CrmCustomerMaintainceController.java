package com.shidao.controller;

import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.Position;
import com.shidao.model.CrmCustomerMaintaince;
import com.shidao.service.CrmCustomerMaintainceService;
import com.shidao.service.SdbsCustomerService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.PermissionEnum;
import com.shidao.util.ValidList;

@RestController
@RequestMapping(value = "/customerMaintaince")
public class CrmCustomerMaintainceController extends BaseController{
	
	@Autowired
	private CrmCustomerMaintainceService customerMaintainceService;
	
	@Autowired
	private SdbsCustomerService customerService;
	
	@PostMapping("/update")
	public JSONObject update(HttpServletRequest request, CrmCustomerMaintaince condition) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setLastModifier(sm.getEmployeeId());
			customerMaintainceService.updateByPrimaryKeySelective(condition);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping("/insert")
	public JSONObject insert(HttpServletRequest request,CrmCustomerMaintaince item){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			item.setReportId(sm.getEmployeeId());
			item.setClubId(sm.getClubId());
			customerMaintainceService.insertSelective(item);
			return JsonUtil.succeedJson(item.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping("/customerLastVisitInfo")
	public JSONObject listLastVisitInfo(HttpServletRequest request,
			@RequestParam(required=false) LocalDate dateStart, @RequestParam(required=false) LocalDate dateEnd, Integer doctorId) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			if(!es.hasPermission(PermissionEnum.ClubManagement) && es.getPositionEnum() == Position.Doctor) {
				doctorId = es.getEmployeeId();
			}
			return JsonUtil.succeedJson(customerService.listLastVisitInfo(es.getClubId(), dateStart, dateEnd, doctorId));
			
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping("/lostReasons")
	public JSONObject listLostReasons() {
		try {
			return JsonUtil.succeedJson(ValidList.getLostReasons());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
