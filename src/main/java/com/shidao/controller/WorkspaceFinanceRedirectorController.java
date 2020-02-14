package com.shidao.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shidao.enums.ProductCategory;
import com.shidao.model.DatePeriodParameter;
import com.shidao.model.SdcomClub;
import com.shidao.service.SdcomClubService;
import com.shidao.service.WmsInventoryDailySummaryService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.PermissionEnum;

@Controller
@RequestMapping(value = "/workspace/finance")
public class WorkspaceFinanceRedirectorController extends BaseController {
	private static final String FINANCE_PATH = "/display/workspace/finance/";

	@Autowired
	private SdcomClubService clubService;

	@Autowired
	private WmsInventoryDailySummaryService inventoryDailySummaryService;

	/**
	 * 
	 * @param request
	 * @param model
	 * @param dateStart
	 *            开始日期
	 * @param dateEnd
	 *            借宿日期
	 * @param clubId
	 *            门诊编号，如果当前用户是财务，则使用该参数，如果是ShidaoClinic，则使用自己的clubId,如果是其他情况，则抛出异常
	 * @return
	 */
	@GetMapping(value = "/jinxiaocun/summary")
	public String getjinxiaocunSummary(HttpServletRequest request, Model model, DatePeriodParameter datePeriodParameter,
			Integer clubId,ProductCategory category) {
		try {
			EmployeeSessionManager eSessionManager = new EmployeeSessionManager(request.getSession());
			if (category == null)
				category = ProductCategory.Keliji;
			if (eSessionManager.hasPermission(PermissionEnum.View_ALL_CLUB_JXC)) {
				List<SdcomClub> clubs = clubService.list();
				model.addAttribute("clubList", clubs);
			} else {
				clubId=eSessionManager.getClubId();
			}

			model.addAllAttributes(inventoryDailySummaryService.getJinxiaocunSummary(datePeriodParameter, clubId,category));
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter", datePeriodParameter);
			model.addAttribute("jinxiaocunClubId", clubId);
			model.addAttribute("categories", ProductCategory.kelijiAndCaoyao);
			model.addAttribute("category", category);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}

		return FINANCE_PATH + "jinxiaocunSummary";
	}

	/**
	 * 
	 * @param request
	 * @param model
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @param vendorId
	 * @return
	 */
	@GetMapping(value = "/jinxiaocun/overview")
	public String getjinxiaocunOverview(HttpServletRequest request, Model model,
			DatePeriodParameter datePeriodParameter, Integer clubId, Integer vendorId,ProductCategory category) {
		try {
			EmployeeSessionManager eSessionManager = new EmployeeSessionManager(request.getSession());

			clubId = inventoryDailySummaryService.isFinanceRoot(clubId, eSessionManager.getPosition(),eSessionManager.getClubId());

			model.addAllAttributes(
					inventoryDailySummaryService.getJinxiaocunOverview(datePeriodParameter, clubId, vendorId,category));
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter", datePeriodParameter);
			model.addAttribute("jinxiaocunClubId", clubId);
			model.addAttribute("category", category);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return FINANCE_PATH + "jinxiaocunOverview";
	}

	/**
	 * 
	 * @param request
	 * @param model
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @param productId
	 * @return
	 */
	@GetMapping(value = "/jinxiaocun/detail")
	public String getjinxiaocunDetail(HttpServletRequest request, Model model, DatePeriodParameter datePeriodParameter,
			Integer clubId, @RequestParam Integer productId) {
		try {
			EmployeeSessionManager eSessionManager = new EmployeeSessionManager(request.getSession());
			clubId = inventoryDailySummaryService.isFinanceRoot(clubId, eSessionManager.getPosition(),eSessionManager.getClubId());
			model.addAllAttributes(
					inventoryDailySummaryService.getJinxiaocunDetail(datePeriodParameter, clubId, productId));
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("datePeriodParameter", datePeriodParameter);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return FINANCE_PATH + "jinxiaocunDetail";
	}

}
