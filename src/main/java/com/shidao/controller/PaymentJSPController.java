package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shidao.enums.ProductCategory;
import com.shidao.model.SdcomEmployee;
import com.shidao.service.SdbsProductService;
import com.shidao.service.SdcomClubService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.util.ShidaoException;

@Controller
@RequestMapping("/payment")
public class PaymentJSPController extends BaseController {
	private static final String PRINTING_PATH = "/display/payment/";

	@Autowired
	SdcomEmployeeService employeeService;

	@Autowired
	SdbsProductService productService;
	
	@Autowired
	private SdcomClubService clubService;
	
	@RequestMapping("operatorType")
	public String payForoperatorType(HttpServletRequest request, Model model, Integer operatorId,ProductCategory category) {
		try {
			if (operatorId == null) {
				Object employeeId = request.getSession(true).getAttribute("employeeId");
				if (employeeId == null) {
					throw new ShidaoException("请先登录，或者指定操作站编号。");
				}
				SdcomEmployee current = employeeService.selectByPrimaryKey((Integer) employeeId);
				operatorId = current.getClubId();
			}
			model.addAttribute("operator", clubService.selectByPrimaryKey(operatorId));
			model.addAttribute("product", productService.list(category,null).get(0));
			
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		
		return PRINTING_PATH + "mzyOperatorPayment";
	}
}
