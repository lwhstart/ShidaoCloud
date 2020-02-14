package com.shidao.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.DepartmentDoctorCategory;
import com.shidao.enums.SdepSettingCategory;
import com.shidao.model.SdcomEmployee;
import com.shidao.model.SdcomRelationDiseaseCategoryDoctor;
import com.shidao.model.SdepSetting;
import com.shidao.model.TcmDiseaseCategory;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.service.SdcomRelationDiseaseCategoryDoctorService;
import com.shidao.service.SdepSettingService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/sdcomRelationDiseaseCategoryDoctor")
public class SdcomRelationDiseaseCategoryDoctorController extends BaseController{
	
	@Autowired
	private SdcomRelationDiseaseCategoryDoctorService relationDiseaseCategoryDoctorService;
	
	@Autowired
	private SdcomEmployeeService employeeService;
	
	@Autowired
	private SdepSettingService sdepSettingService;
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月9日
	 * 功能:专家管理专用
	 * @param condition
	 * @return
	 */
	@RequestMapping(value = "/doctors", method = RequestMethod.GET)
	public JSONObject doctors(Integer diseaseCategoryId) {
		try {
			JSONObject map = JsonUtil.succeedJson();
			SdcomRelationDiseaseCategoryDoctor condition = new SdcomRelationDiseaseCategoryDoctor();
			condition.setDiseaseCategoryId(diseaseCategoryId);
			condition.setCategory(DepartmentDoctorCategory.RemoteDiagnostic);
			List<TcmDiseaseCategory> list = relationDiseaseCategoryDoctorService.listDiseaseCategoryOfDoctor(condition);
			List<SdcomEmployee> doctors = new ArrayList<>();
			if (list != null && !list.isEmpty())
				doctors = list.get(0).getDoctors();
			map.put("doctors", doctors);
			String doctorIds = doctors.stream().map(a->a.getId().toString()).collect(Collectors.joining(","));
			SdcomEmployee employee = new SdcomEmployee();
			employee.setNoIds(doctorIds);
			employee.setPosition("医生");
			map.put("otherDoctors", employeeService.listNoIds(employee));
			SdepSetting setting = new SdepSetting();
			setting.setName("DefaultDoctorFee");
			setting.setCategory(SdepSettingCategory.RemoteDiagnostic);
			map.put("defaultDoctorFee", sdepSettingService.list(setting).get(0));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月11日
	 * 功能:专家排班表专用
	 * @param condition
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(SdcomRelationDiseaseCategoryDoctor condition) {
		try {
			JSONObject map = JsonUtil.succeedJson();
			condition.setCategory(DepartmentDoctorCategory.RemoteDiagnostic);
			List<TcmDiseaseCategory> list = relationDiseaseCategoryDoctorService.listDiseaseCategoryOfDoctor(condition);
			map.put("list", list);
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月9日
	 * @param condition
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(SdcomRelationDiseaseCategoryDoctor condition) {
		try {
			relationDiseaseCategoryDoctorService.insert(condition);
			return JsonUtil.succeedJson(condition.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月9日
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer diseaseCategoryId,Integer doctorId) {
		try {
			relationDiseaseCategoryDoctorService.delete(diseaseCategoryId,doctorId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
