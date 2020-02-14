package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shidao.service.SdbsCustomerInfoServices;
import com.shidao.service.SdbsCustomerSourceService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.ValidList.ValidListCategory;

/**
 * 
 * @author yzl 2019年8月5日
 *
 */
@Controller
@RequestMapping(value = "/workspace/modal")
public class WorkspaceModalRedirectController extends BaseController {
	private static final String WORKSPACE_MODAL_PATH = "/display/workspace/modal/";

	@Autowired
	private SdbsCustomerSourceService customerSourceService;

	@Autowired
	SdbsCustomerInfoServices customerInfoService;

	/**
	 * @author 创建人:liupengyuan,时间:2018年4月10日 功能:设置客户来源
	 * @param CustomerUuid
	 * @param model
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/customerSourceSetting")
	public String getCustomerSourceSetting(String customerUuid, Model model, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("sources", customerSourceService.listSources(sm.getClubId()));
			model.addAttribute("customerSource",
					customerSourceService.selectByCustomerUuid(customerUuid, sm.getClubId()));
			model.addAttribute("customerUuid", customerUuid);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return WORKSPACE_MODAL_PATH + "customerSourceSetting";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年4月10日 功能:设置客户来源
	 * @param CustomerUuid
	 * @param model
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/customerInfoOfValidList")
	public String getCustomerInfoOfValidList(HttpServletRequest request, Model model, String customerUuid,
			ValidListCategory category) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("infoList", customerInfoService.listValidListOfCategory(customerUuid, sm.getClubId(), category));
			model.addAttribute("customerUuid", customerUuid);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return WORKSPACE_MODAL_PATH + "customerInfoOfValidList";
	}
}
