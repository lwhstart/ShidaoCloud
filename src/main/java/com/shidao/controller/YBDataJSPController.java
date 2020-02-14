package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shidao.service.YbService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.ShidaoException;
import com.shidao.vo.FuZhenCondition;

@Controller
@RequestMapping("/ybData/jsp")
public class YBDataJSPController extends BaseController{
	
	private static final String YBDATA_PATH = "/display/ybData/";
	
	@Autowired
	private YbService ybService;
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年12月3日
	 * 功能:获取医保客户的类别和数量
	 * @param model
	 * @param request
	 * @return
	 */
	@GetMapping("/ybCustomer")
	public String ybCustomer(Model model,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			model.addAllAttributes(ybService.getCustomerFeeTypeCount());
			model.addAttribute("customer", ybService.getCustomerDate());
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e);
		}
		return YBDATA_PATH + "ybCustomer";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月4日<br>
	 * 功能:最新日期
	 * @param model
	 * @param request
	 * @return
	 */
	@GetMapping("/ybSalesOrder")
	public String ybSalesOrder(Model model,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			model.addAttribute("salesOrder", ybService.getSalesOrderDate());
			model.addAttribute("salesOrderDetail", ybService.getSalesOrderDetailDate());
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e);
		}
		return YBDATA_PATH + "ybSalesOrder";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月11日<br>
	 * 功能:看诊信息
	 * @param model
	 * @param request
	 * @return
	 */
	@GetMapping("/ybTreatment")
	public String ybTreatment(Model model,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			model.addAttribute("history", ybService.getHistoryDateAndCount());
			model.addAttribute("zhiLiao", ybService.getZhiliaoDateAndCount());
			model.addAttribute("diagnosis", ybService.getDiagnosisDateAndCount());
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e);
		}
		return YBDATA_PATH + "ybTreatment";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月27日<br>
	 * 功能:医保查看处方
	 * @param model
	 * @param request
	 * @param condition
	 * @return
	 */
	@GetMapping("/medicalRecords")
	public String medicalRecords(Model model,HttpServletRequest request,FuZhenCondition condition) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			model.addAttribute("condition", condition);
			model.addAttribute("medicalRecord", ybService.getYbMedicalRecordList(condition));
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e);
		}
		return YBDATA_PATH + "medicalRecords";
	}
	
	
}
