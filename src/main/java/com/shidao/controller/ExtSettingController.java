package com.shidao.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.ExtSetting;
import com.shidao.service.ExtSettingService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping("/ext/setting")
public class ExtSettingController {
	
	@Autowired
	ExtSettingService settingService;
	
	@RequestMapping("/list")
	public JSONObject listSettingByCondition(ExtSetting setting,HttpServletRequest request) {
		try {
			EmployeeSessionManager es=new EmployeeSessionManager(request.getSession());
			setting.setClubId(es.getClubId());
			return JsonUtil.succeedJson(settingService.list(setting));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping("/insert")
	public JSONObject insertIfNotExist(ExtSetting setting,HttpServletRequest request) {
		try {
			EmployeeSessionManager es=new EmployeeSessionManager(request.getSession());
			setting.setClubId(es.getClubId());
			settingService.insertSelective(setting);
			return JsonUtil.succeedJson(settingService.selectByPrimaryKey(setting.getId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping("/update")
	public JSONObject updateSetting(ExtSetting setting) {
		try {
			settingService.updateByPrimaryKeySelective(setting);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping("/delete")
	public JSONObject deleteSetting(Integer id,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			settingService.deleteByPrimaryKeySelective(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
