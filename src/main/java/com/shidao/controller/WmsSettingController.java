package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.WmsSetting;
import com.shidao.model.WmsSetting.SettingCategory;
import com.shidao.service.WmsSettingService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping("/wmsSetting")
public class WmsSettingController extends BaseController{

	@Autowired
	private WmsSettingService wmsSettingService;
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(WmsSetting wmsSetting) {
		try {
			wmsSettingService.updateByPrimaryKeySelective(wmsSetting);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id) {
		try {
			wmsSettingService.deleteByPrimaryKey(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping(value="/insert")
	public JSONObject insert(WmsSetting wmsSetting) {
		try {
			wmsSetting.setCategory(SettingCategory.Medicine);
			wmsSettingService.insertSelective(wmsSetting);
			return JsonUtil.succeedJson(); 
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
