package com.shidao.controller;

import java.time.LocalDate;
import java.time.Month;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsCustomerSource;
import com.shidao.service.SdbsCustomerService;
import com.shidao.service.SdbsCustomerSourceService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value="/sdbsCustomerSource")
public class SdbsCustomerSourceController extends BaseController{

	@Autowired
	private SdbsCustomerSourceService customerSourceService;
	
	@Autowired
	private SdbsCustomerService customerService;
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月10日
	 * 功能:对sdbs_customer_source 进行操作，如果有，则修改值,如果没有，则插入值,如果source为会员介绍，则介绍人编号可以设置
	 * @param customerUuid
	 * @param customerSource
	 * @return
	 */
	@RequestMapping(value="/setSource",method = RequestMethod.POST)
	public JSONObject setSource(String customerUuid,SdbsCustomerSource customerSource,HttpServletRequest request){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Integer customerId = customerService.selectByUuid(customerUuid).getId();
			SdbsCustomerSource source = customerSourceService.selectByCustomerId(customerId,sm.getClubId());
			if (source == null) {
				customerSource.setFirstVisitClubId(sm.getClubId());
				customerSource.setCustomerId(customerId);
				customerSourceService.insertSelective(customerSource);
			}else {
				customerSource.setId(source.getId());
				customerSourceService.updateByPrimaryKeySelective(customerSource,false);
			}
			return JsonUtil.succeedJson(customerSource.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月10日
	 * 功能:对sdbs_customer_source 进行操作，如果有，则修改值,如果没有，则插入值,如果source为会员介绍，则介绍人编号可以设置
	 * @param customerUuid
	 * @param customerSource
	 * @return
	 */
	@RequestMapping(value="/changeFirstVisitDate",method = RequestMethod.POST)
	public JSONObject update(SdbsCustomerSource customerSource,String customerUuid,HttpServletRequest request){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Integer customerId = customerService.selectByUuid(customerUuid).getId();
			SdbsCustomerSource source = customerSourceService.selectByCustomerId(customerId,sm.getClubId());
			if (source == null) {
				customerSource.setFirstVisitClubId(sm.getClubId());
				customerSource.setCustomerId(customerId);
				customerSourceService.insertSelective(customerSource);
			}else {
				customerSource.setCustomerId(source.getCustomerId());
				customerSource.setId(source.getId());
				customerSource.setFirstVisitClubId(sm.getClubId());
				customerSourceService.updateByPrimaryKeySelective(customerSource);
			}
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月10日
	 * 功能:如果初诊日期没有，则设置为当天
	 * @param customerUuid
	 * @param firstVisitDate
	 * @return
	 */
	@RequestMapping(value="/setFirstVisitDate",method = RequestMethod.POST)
	public JSONObject setFirstVisitDate(String customerUuid,Boolean isSet,LocalDate firstVisitDate,HttpServletRequest request){
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			if (firstVisitDate == null)
				firstVisitDate = LocalDate.now();
			Integer customerId = customerService.selectByUuid(customerUuid).getId();
			SdbsCustomerSource source = customerSourceService.selectByCustomerId(customerId,employeeSessionManager.getClubId());
			if (isSet != null && !isSet){
				LocalDate.of(1900, Month.JANUARY, 01);
			}
			SdbsCustomerSource customerSource = new SdbsCustomerSource();
			customerSource.setId(source.getId());
			customerSource.setCustomerId(customerId);
			customerSource.setFirstVisitDate(firstVisitDate);
			customerSourceService.updateByPrimaryKeySelective(customerSource);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月10日
	 * @param customerUuid
	 * @return
	 */
	@RequestMapping(value="/getCustomerSource",method = RequestMethod.GET)
	public JSONObject getCustomerSource(String customerUuid,HttpServletRequest request){
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(customerSourceService.selectByCustomerUuid(customerUuid,employeeSessionManager.getClubId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 获取门店所有用户来源
	 * @author 作者zzp: 
	 * @version 创建时间：2019年7月15日 下午4:18:13 
	 * @param request
	 * @return
	 */
	@GetMapping(value="/list/source")
	public JSONObject sources(HttpServletRequest request) {
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(customerSourceService.listSources(employeeSessionManager.getClubId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
