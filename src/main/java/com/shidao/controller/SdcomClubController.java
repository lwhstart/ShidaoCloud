package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdcomClub;
import com.shidao.service.SdcomClubService;
import com.shidao.setting.ClubSetting;
import com.shidao.util.JsonUtil;
import com.shidao.util.ListResult;

@RestController
@RequestMapping(value = "/club")
public class SdcomClubController extends BaseController {

	@Autowired
	private SdcomClubService sdcomClubService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(SdcomClub condition, Integer pageNum, Integer pageSize) {
		try {
			ListResult<SdcomClub> result = sdcomClubService.list(condition, pageNum, pageSize);
			return JsonUtil.succeedJson(result);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 超级管理员在创建门店时，同时新增一个该门店管理员 如果warehouseId有并generateWarehouse未true则添加仓库门店关系表
	 * 添加默认助理
	 * @param sdcomClub
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(SdcomClub sdcomClub,Integer warehouseId) {
		try {
			sdcomClubService.insert(sdcomClub,warehouseId);
			return JsonUtil.succeedJson(sdcomClub.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}	

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(SdcomClub sdcomClub) {
		try {
			sdcomClubService.update(sdcomClub);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月27日
	 * 功能:设置可用岗位和服务卡
	 * @param setting
	 * @param clubId
	 * @return
	 */
	@PostMapping("/setSetting")
	public JSONObject setSetting(ClubSetting setting,Integer clubId) {
		try {
			sdcomClubService.setSetting(setting, clubId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id) {
		try {
			SdcomClub club = new SdcomClub();
			club.setEnabled(0);
			club.setId(id);
			sdcomClubService.updateByPrimaryKeySelective(club);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public JSONObject detailById(@PathVariable(value = "id") Integer id) {
		try {
			return JsonUtil.succeedJson(sdcomClubService.selectByPrimaryKey(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月29日
	 * 功能:门店医生关系表
	 * @return
	 */
	@GetMapping("/clubRelationDoctor/list")
	public JSONObject clubRelationDoctorList() {
		try {
			JSONObject map = JsonUtil.succeedJson();
			map.put("clubs", sdcomClubService.listClubRelationDoctor());
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping("/relationClubs/list")
	public JSONObject relationClubsList(Integer warehouseId) {
		try {
			JSONObject map = JsonUtil.succeedJson();
			map.put("clubList", sdcomClubService.listRelationClubs(warehouseId));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

}
