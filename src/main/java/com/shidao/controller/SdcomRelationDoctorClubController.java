package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdcomRelationDoctorClub;
import com.shidao.service.SdcomRelationDoctorClubService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value="/relationDoctorClub")
public class SdcomRelationDoctorClubController extends BaseController{

	@Autowired
	private SdcomRelationDoctorClubService  relationDoctorClubService;
	
	@RequestMapping(value="/list")
	public JSONObject list(SdcomRelationDoctorClub relationDoctorClub,Integer pageNum, Integer pageSize){
		try {
			return JsonUtil.succeedJson(relationDoctorClubService.list(relationDoctorClub));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
