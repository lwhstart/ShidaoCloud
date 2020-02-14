package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.SdepSettingCategory;
import com.shidao.model.SdepSetting;
import com.shidao.service.SdepSettingService;
import com.shidao.util.JsonUtil;
import com.shidao.util.StringUtil;

@RestController
@RequestMapping(value = "/sdepSetting")
public class SdepSettingController extends BaseController{
	
	@Autowired
	private SdepSettingService sdepSettingService;
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月10日
	 * @param condition
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(SdepSetting condition) {
		try {
			sdepSettingService.insertSelective(condition);
			return JsonUtil.succeedJson(condition.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月10日
	 * 功能:单次会诊时间（MinutesPerRD），会诊费（DefaultDoctorFee）
	 * @param condition
	 * @return
	 */
	@RequestMapping(value = "/setting", method = RequestMethod.POST)
	public JSONObject setting(String MinutesPerRD,String DefaultDoctorFee) {
		try {
			if (!StringUtil.isNullOrEmpty(MinutesPerRD))
				Integer.parseInt(MinutesPerRD);
			if (!StringUtil.isNullOrEmpty(DefaultDoctorFee))
				Float.parseFloat(DefaultDoctorFee);
			
			if (!StringUtil.isNullOrEmpty(MinutesPerRD))
				sdepSettingService.setting("MinutesPerRD", MinutesPerRD, SdepSettingCategory.RemoteDiagnostic);
			if (!StringUtil.isNullOrEmpty(DefaultDoctorFee))
				sdepSettingService.setting("DefaultDoctorFee", DefaultDoctorFee, SdepSettingCategory.RemoteDiagnostic);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
