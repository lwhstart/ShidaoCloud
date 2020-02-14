package com.shidao.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsAppointmentD2dOrder;
import com.shidao.enums.Position;
import com.shidao.service.SdbsAppointmentD2dOrderService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.vo.SdbsAppointmentD2dOrderVO;

@RestController
@RequestMapping("/sdbsAppointmentD2dOrder")
public class SdbsAppointmentD2dOrderController extends BaseController {


	@Autowired
	private SdbsAppointmentD2dOrderService sdbsD2dOrderService;
	
	@Autowired
	private SdcomEmployeeService sdcomEmployeeService;
	@RequestMapping("/arrange")
	public JSONObject arrangeServer(Integer orderId, Integer serverId){
		try {
			
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/getCompanyd2dList", method = RequestMethod.GET)
	public JSONObject getd2dList(SdbsAppointmentD2dOrderVO sdbsAppointmentD2dOrder, Integer pageNum,Integer pageSize) {
		try {
			Map<String, Object> d2dList = sdbsD2dOrderService.listByCondition(sdbsAppointmentD2dOrder,pageNum,pageSize);
			return JsonUtil.succeedJson(d2dList);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public JSONObject getD2Dinfo(@PathVariable(value = "id")  Integer id) {
		try {
			SdbsAppointmentD2dOrder d2dList = sdbsD2dOrderService.selectD2DInfo(id);
			return JsonUtil.succeedJson(d2dList);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject List(SdbsAppointmentD2dOrderVO sdbsAppointmentD2dOrder, Integer pageNum,Integer pageSize) {
		try {
			Map<String, Object> d2dList = sdbsD2dOrderService.listByCondition(sdbsAppointmentD2dOrder,pageNum,pageSize);
			JSONObject result= JsonUtil.succeedJson(d2dList);
			result.put("condition",sdbsAppointmentD2dOrder);
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/technician", method = RequestMethod.GET)
	public JSONObject technicianList(HttpServletRequest request, SdbsAppointmentD2dOrderVO sdbsAppointmentD2dOrder,
			Integer pageNum,Integer pageSize) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			Map<String, Object> d2dList = sdbsD2dOrderService.listByCondition(sdbsAppointmentD2dOrder,pageNum,pageSize);
			JSONObject result= JsonUtil.succeedJson(d2dList);
			result.put("condition",sdcomEmployeeService.list(esManager.getClubId(), Position.Technician));
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="/update",method = RequestMethod.POST)
	public JSONObject updated2dOrder(SdbsAppointmentD2dOrderVO sAppointmentD2dOrderVO){
		try {
			sdbsD2dOrderService.updateByPrimaryKeySelective(sAppointmentD2dOrderVO);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
