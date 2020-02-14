package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsServiceCard;
import com.shidao.service.SdbsServiceCardService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;
import com.shidao.util.StringUtil;

@RestController
@RequestMapping(value = "/serviceCard")
public class SdbsServiceCardController {

	@Autowired
	private SdbsServiceCardService serviceCardService;
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject updateSdbsServiceCard(SdbsServiceCard serviceCard,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			serviceCard.setLastModifier(sm.getEmployeeId());
			serviceCardService.updateByPrimaryKeySelective(serviceCard);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject deleteSdbsServiceCard(Integer id) {
		try {
			serviceCardService.delete(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月6日
	 * 功能:新增TCM("会员卡"),TCK("套餐卡"),JFK("积分卡"),CPDZK("产品打折卡");
	 * @param serviceCard
	 * @param insertTCMType
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(SdbsServiceCard serviceCard, String insertTCMType, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			switch (serviceCard.getType()) {
			case TCK:
			case JFK:
			case CPZKK:
			case CXK:
				if (serviceCard.getItems() ==null || serviceCard.getItems().size()<=0)
					throw new ShidaoException(serviceCard.getTypeName() +":没有详单。");
				serviceCard.setClubId(sm.getClubId());
				break;
			case TCM:
				//本门店
				if(StringUtil.isNullOrEmpty(insertTCMType)) {
					throw new ShidaoException("必须指定会员卡的类型是属于门诊还是连锁店。");
				}else if (insertTCMType.equals("clubId")) {
					serviceCard.setClubId(sm.getClubId());
				}else if (insertTCMType.equals("clubType")) {
					serviceCard.setClubType(sm.getClubType());
				}else {
					throw new ShidaoException("错误的会员卡："+ insertTCMType);
				}
				break;
			default:
				throw new ShidaoException("未处理的卡项类型："+serviceCard.getTypeName());
			}
			serviceCardService.insert(serviceCard);
			return JsonUtil.succeedJson(serviceCard.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
	
	/**
	 * 禁用0,启动1
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/enabled", method = RequestMethod.POST)
	public JSONObject enabled(Integer id,Integer enabled,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			serviceCardService.enabled(id,enabled,sm.getEmployeeId());
			return JsonUtil.succeedJson(enabled);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="/detail/{id}",method=RequestMethod.GET)
	public JSONObject getServiceCardById(@PathVariable(value="id") Integer id,HttpServletRequest request){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(serviceCardService.getServiceCardDetail(id,sm.getClubId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
