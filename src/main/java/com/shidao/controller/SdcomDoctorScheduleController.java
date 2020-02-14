package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.DiseaseCategory;
import com.shidao.enums.DoctorScheduleCategory;
import com.shidao.model.SdcomDoctorSchedule;
import com.shidao.model.TcmDiseaseCategory;
import com.shidao.service.SdcomDoctorScheduleService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/doctorSchedule")
public class SdcomDoctorScheduleController extends BaseController{

	@Autowired
	private SdcomDoctorScheduleService doctorScheduleService;
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月8日
	 * @param doctorSchedule
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(SdcomDoctorSchedule doctorSchedule) {
		try {
			doctorScheduleService.insert(doctorSchedule);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月8日
	 * @param doctorSchedule
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(SdcomDoctorSchedule doctorSchedule) {
		try {
			doctorScheduleService.updateByPrimaryKeySelective(doctorSchedule);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月8日
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id) {
		try {
			doctorScheduleService.delete(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月14日
	 * 功能:病种下的医生，医生下的预约日期，预约日期下的时间段
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/listRemoteDiagnosticAppointment", method = RequestMethod.GET)
	public JSONObject list(SdcomDoctorSchedule condition) {
		try {
			condition.setListSpecialCondition("TodayAndFuture");
			condition.setCategory(DoctorScheduleCategory.RemoteCenter);
			TcmDiseaseCategory diseaseCategory = new TcmDiseaseCategory();
			diseaseCategory.setCategory(DiseaseCategory.RemoteDiagnositc);
			condition.setDiseaseCategory(diseaseCategory);
			return JsonUtil.succeedJson(doctorScheduleService.listRemoteDiagnosticAppointment(condition));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
