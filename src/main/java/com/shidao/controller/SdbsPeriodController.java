package com.shidao.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.service.SdbsPeriodService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value="/sdbsPeriod")
public class SdbsPeriodController extends BaseController{

	@Autowired
	private SdbsPeriodService sdbsPeriodService;
	
	/**
	 * 医生可预约的时间段
	 * @param date
	 * @return
	 */
	@RequestMapping(value="/list/{date}", method = RequestMethod.GET)
	public JSONObject getListTCM(@PathVariable Date date,HttpServletRequest request){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(sdbsPeriodService.list(date,sm.getClubId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * pad 医生可预约的时间段和每个时间段上的已预约次数
	 * @author 作者zzp: 
	 * @version 创建时间：2019年1月8日 下午9:47:52 修改
	 * @param date
	 * @param doctorId
	 * @param request
	 * @return
	 */
	@GetMapping(value="/appointmentNum/{date}")
	public JSONObject getAppointmentNumByDate(@PathVariable Date date,Integer doctorId,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(sdbsPeriodService.getAppointmentNumByDate(date, sm.getClubId(),doctorId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
