package com.shidao.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.shidao.model.ExtBusinessStatisics;
import com.shidao.service.ExtBusinessStatisicsService;
import com.shidao.util.EmployeeSessionManager;

@Controller
public class ExtBusinissRedirectorController {
	@Autowired
	ExtBusinessStatisicsService extBusinessStatisicsService;
	/*
	 * private static final String EXT_BUSINESS_PATH = "/display/extBusiness/";
	 * 
	 * @RequestMapping("/Consumption/dinggao/full") public String
	 * listFullDinggao(HttpServletRequest request,Model model) { try {
	 * EmployeeSessionManager es=new EmployeeSessionManager(request.getSession());
	 * String data = request.getParameter("data"); DateTimeFormatter fmt =
	 * DateTimeFormatter.ofPattern("yyyy-MM-dd"); String category =
	 * request.getParameter("category"); List<ExtBusinessStatisics> listFullDinggaot
	 * = extBusinessStatisicsService.listFullDinggao(LocalDate.parse(data,fmt),
	 * es.getClubId(), category); for (int i = 0; i < listFullDinggaot.size(); i++)
	 * { System.err.println(listFullDinggaot.get(i).getExtConsumptionOrderDetail().
	 * getId()); } model.addAttribute("yibaoxinxi",listFullDinggaot);
	 * 
	 * } catch (Exception e) { // TODO: handle exception
	 * model.addAttribute("errorMsg", e.getMessage()); } return
	 * EXT_BUSINESS_PATH+"fullDinggao"; }
	 * 
	 * @RequestMapping("/outpke") public ModelAndView get() { return new
	 * ModelAndView("/display/extBusiness/outpke"); }
	 */
	
}
