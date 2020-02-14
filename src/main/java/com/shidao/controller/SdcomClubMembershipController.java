package com.shidao.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdcomClubMembership;
import com.shidao.model.SdcomEmployee;
import com.shidao.service.SdcomClubMembershipService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value="/clubMembership")
public class SdcomClubMembershipController extends BaseController{

	@Autowired
	private SdcomClubMembershipService clubMembershipService;
	
	@Autowired
	private SdcomEmployeeService employeeService;
	
	@RequestMapping(value="/insert",method=RequestMethod.POST)
	public JSONObject insert(SdcomClubMembership sdcomClubMembership,HttpServletRequest request) {
		try {
			if (sdcomClubMembership.getClubId() == null) {
				Integer employeeId =(Integer)request.getSession().getAttribute("employeeId");
				SdcomEmployee key = employeeService.selectByPrimaryKey(employeeId);
				sdcomClubMembership.setClubId(key.getClubId());
			}
			clubMembershipService.insert(sdcomClubMembership);
			return JsonUtil.succeedJson(sdcomClubMembership.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public JSONObject update(SdcomClubMembership sdcomClubMembership) {
		try {
			clubMembershipService.update(sdcomClubMembership);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@RequestMapping(value="/updateTime")
	public JSONObject updateTime(SdcomClubMembership sdcomClubMembership) {
		try {
			//查询club_member是否有数据
			SdcomClubMembership condition = new SdcomClubMembership();
			condition.setClubId(sdcomClubMembership.getClubId());
			condition.setOperation(sdcomClubMembership.getOperation());
			List<SdcomClubMembership> list = clubMembershipService.list(condition);
			if (list != null && !list.isEmpty()) {
				//直接修改次数
				SdcomClubMembership clubMembership = new SdcomClubMembership();
				clubMembership.setId(list.get(0).getId());
				clubMembership.setAvailableCount(sdcomClubMembership.getAvailableCount());
				clubMembership.setTotalCount(list.get(0).getTotalCount()+sdcomClubMembership.getAvailableCount()-list.get(0).getAvailableCount());
				clubMembershipService.update(clubMembership);
			} else {
				//插入一条数据
				SdcomClubMembership clubMembership = new SdcomClubMembership();
				clubMembership.setAvailableCount(sdcomClubMembership.getAvailableCount());
				clubMembership.setTotalCount(sdcomClubMembership.getAvailableCount());
				clubMembership.setClubId(sdcomClubMembership.getClubId());
				clubMembership.setOperation(sdcomClubMembership.getOperation());
				clubMembershipService.insertSelective(clubMembership);
			}
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public JSONObject delete(SdcomClubMembership sdcomClubMembership) {
		try {
			sdcomClubMembership.setEnabled(0);
			clubMembershipService.update(sdcomClubMembership);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
