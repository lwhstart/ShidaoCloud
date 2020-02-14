package com.shidao.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsCustomerCare;
import com.shidao.model.SdbsCustomerCare.CustomerCareType;
import com.shidao.service.SdbsCustomerCareService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ListResult;

@RestController
@RequestMapping("/sdbsCustomerCare")
public class SdbsCustomerCareController extends BaseController {

	@Autowired
	SdbsCustomerCareService customerCareService;

	@RequestMapping(value="/care/1/list", method = RequestMethod.GET)
	public JSONObject care1list(Integer pageNum, Integer pageSize,HttpServletRequest request) {
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			
			SdbsCustomerCare condition = new SdbsCustomerCare();
			condition.setType(CustomerCareType.Care);
			condition.setSequence(1);
			condition.setCareDate(new Date());
			condition.setCarerId(employeeSessionManager.getEmployeeId());
			ListResult<SdbsCustomerCare> cares = customerCareService.list(condition, pageNum, pageSize);
			return JsonUtil.succeedJson(cares);

		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="/care/2/list", method = RequestMethod.GET)
	public JSONObject care2list(Integer pageNum, Integer pageSize,HttpServletRequest request) {
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			SdbsCustomerCare condition = new SdbsCustomerCare();
			condition.setType(CustomerCareType.Care);
			condition.setSequence(2);
			condition.setCareDate(new Date());
			condition.setCarerId(employeeSessionManager.getEmployeeId());
			ListResult<SdbsCustomerCare> cares = customerCareService.list(condition, pageNum, pageSize);
			return JsonUtil.succeedJson(cares);

		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="/care/3/list", method = RequestMethod.GET)
	public JSONObject care32list(Integer pageNum, Integer pageSize,HttpServletRequest request) {
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			SdbsCustomerCare condition = new SdbsCustomerCare();
			condition.setType(CustomerCareType.Care);
			condition.setSequence(3);
			condition.setCareDate(new Date());
			condition.setCarerId(employeeSessionManager.getEmployeeId());
			ListResult<SdbsCustomerCare> cares = customerCareService.list(condition, pageNum, pageSize);
			return JsonUtil.succeedJson(cares);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="/overdue/care",method = RequestMethod.GET)
	public JSONObject overdueCare(Integer pageNum, Integer pageSize,HttpServletRequest request){
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			SdbsCustomerCare condition = new SdbsCustomerCare();
			condition.setType(CustomerCareType.Care);
			condition.setCarerId(employeeSessionManager.getEmployeeId());
			condition.setListSpecialCondition("过期关怀");
			ListResult<SdbsCustomerCare> cares = customerCareService.list(condition, pageNum, pageSize);
			return JsonUtil.succeedJson(cares);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping("/detail/{id}")
	public JSONObject detail(@PathVariable Integer id) {
		try {
			SdbsCustomerCare care = customerCareService.selectByPrimaryKey(id);
			return JsonUtil.succeedJson(care);

		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 
	 * @param type 关怀类型， Care，Remind
	 * @param carerId 关怀者编号，这里是助理编号
	 * @param sequence ，如果是看诊后回访关怀（Remid),此为第几次关怀
	 * @return
	 */
	@RequestMapping("/list/{type}")
	public JSONObject listRemind(@PathVariable(value="type")String  type, Integer sequence,HttpServletRequest request){
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			SdbsCustomerCare condition = new SdbsCustomerCare();
			CustomerCareType customerCareType = CustomerCareType.valueOf(type);
			condition.setType(customerCareType);
			condition.setSequence(sequence);
			condition.setCarerId(employeeSessionManager.getEmployeeId());			
			condition.setCareDate(new Date());
			
			List<SdbsCustomerCare> cares =  customerCareService.list(condition );
			return JsonUtil.succeedJson(cares);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value="/update",method = RequestMethod.POST)
	public JSONObject update(SdbsCustomerCare customerCare){
		try {
			customerCareService.updateByPrimaryKeySelective(customerCare);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	@ResponseBody
	@RequestMapping(value="/list/customerId",method = RequestMethod.GET)
	public JSONObject caredetail(@RequestParam Integer customerId,SdbsCustomerCare care) {
		try {
			care.setCustomerId(customerId);
			return JsonUtil.succeedJson(customerCareService.list(care));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
