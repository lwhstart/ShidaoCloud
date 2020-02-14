package com.shidao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.shidao.model.CrmCustomerNotice;
import com.shidao.service.CrmCustomerNoticeService;
import com.shidao.util.ShidaoException;

@Controller
@RequestMapping("/workspace/crm")
public class CRMJSPController extends BaseController{

	private static final String CRM_PATH = "/display/workspace/crm/";
	
	@Autowired
	private CrmCustomerNoticeService customerNoticeService;
	
	@RequestMapping(value="customerNotice/list",method=RequestMethod.GET)
	public String customerNoticeList(Model model,CrmCustomerNotice condition,Integer pageSize,Integer pageNum){
		try {
			model.addAllAttributes(customerNoticeService.listByCondition(condition, pageNum, pageSize));
			model.addAttribute("condition", condition);
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return CRM_PATH + "customerNoticeList";
	}
	@RequestMapping(value="customerNotice/detail",method=RequestMethod.GET)
	public String customerNoticeDetail(Model model,Integer id){
		if (id == null) {
			return CRM_PATH + "customerNoticeDetail";
		}
			model.addAttribute("customerNotice", customerNoticeService.selectByPrimaryKey(id));
		return CRM_PATH + "customerNoticeDetail";
	}
}
