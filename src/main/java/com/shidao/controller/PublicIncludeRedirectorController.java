package com.shidao.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.shidao.enums.DatePeriod;
import com.shidao.enums.ProductCategory;
import com.shidao.model.DatePeriodParameter;
import com.shidao.util.RegexUtil;
import com.shidao.util.ShidaoException;
import com.shidao.util.StringUtil;

@Controller
@RequestMapping("/public/include")
public class PublicIncludeRedirectorController extends BaseController {

	private static final String PUBLIC_INCLUDE_PATH = "/display/public/include/";

	/**
	 * @author Zhilin ,2018-3-5 转向到分页页面的Controller
	 *         调用此接口的接口，需要增加model.addAttribute("requestURI",
	 *         request.getRequestURI()); 不能再页面中使用
	 *         pageContext.request.getRequestURI(), 他返回的是jsp页面地址。
	 *
	 * @param request
	 * @param model
	 * @param requestURI
	 *            分页页面的controller地址
	 * @param pageNum
	 *            当前页码
	 * @param navigateFirstPage
	 *            显示的第一页
	 * @param navigateLastPage
	 *            显示的最后一页
	 * @param pages
	 *            总页数
	 * @return baseUrl： 页面地址，以pageNum=结尾，可以拼接分页的指定页码
	 */
	@RequestMapping(value = "/pagerTemplate", method = RequestMethod.GET)
	public String gotoPagerTemplate(HttpServletRequest request, Model model, String requestURI, Integer pageNum,
			Integer navigateFirstPage, Integer navigateLastPage, Integer pages) {

		try {
			String baseUrl = buildUrl(requestURI, request.getQueryString());
			// 如果原来指定了页码，这里要删除页码，以便从新添加页码信息。
			baseUrl = RegexUtil.removeAll(baseUrl, "pageNum=\\d*");
			baseUrl = RegexUtil.removeDuplicateAmp(baseUrl);
			baseUrl = buildUrl(baseUrl, "pageNum=");

			model.addAttribute("pageNum", pageNum);
			model.addAttribute("navigateFirstPage", navigateFirstPage);
			model.addAttribute("navigateLastPage", navigateLastPage);
			model.addAttribute("pages", pages);
			model.addAttribute("baseUrl", baseUrl);
		} catch (Exception e) {
			model.addAttribute("pagerErrorMsg", e);
		}
		return PUBLIC_INCLUDE_PATH + "pagerTemplate";
	}

	/**
	 * 转向按日期段查询的公共页面
	 * 
	 * @author Zhilin
	 * @param request
	 * @param model
	 * @param period
	 * @param requestURI
	 * @return
	 * @throws ShidaoException
	 *//*
	@RequestMapping(value = "/datePeriodTemplate", method = RequestMethod.GET)
	public String timerTemplate(HttpServletRequest request, Model model, String requestURI, DatePeriodParameter datePeriodParameter) {
		try {
			model.addAttribute("datePeriodParameter", datePeriodParameter);
			String queryString = request.getQueryString();
			String baseUrl = buildUrl(requestURI, queryString);
			String regex = "(dateStart|dateEnd)=\\d{4}-\\d{2}-\\d{2}";
			baseUrl = RegexUtil.removeAll(baseUrl, regex);
			baseUrl = RegexUtil.removeDuplicateAmp(baseUrl);

			String fmt = buildUrl(baseUrl, "dateStart=%tF&dateEnd=%tF");
			for (DatePeriod dPeriod : DatePeriod.values()) {
				String url = String.format(fmt, dPeriod.getDateStart(), dPeriod.getDateEnd());
				model.addAttribute(dPeriod.name(), url);
			}
			model.addAttribute("baseUrl", baseUrl);
		} catch (Exception e) {
			model.addAttribute("datePeriodErrorMsg", e);
		}

		return PUBLIC_INCLUDE_PATH + "datePeriodTemplate";
	}*/
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月26日
	 * 功能:只返回每个日期段的开始日期和结束日期
	 * @param model
	 * @param requestURI
	 * @param datePeriodParameter
	 * @return
	 */
	@RequestMapping(value = "/datePeriodTemplate", method = RequestMethod.GET)
	public String timerTemplate(Model model,String pageClassName,ProductCategory category) {
		try {
			Map<String, DatePeriodParameter> map = new HashMap<>();
			for (DatePeriod all : DatePeriod.values()) {
				DatePeriodParameter date = new DatePeriodParameter();
				date.setDateEnd(all.getDateEnd());
				date.setDateStart(all.getDateStart());
				map.put(all.name(), date);
			}
			model.addAllAttributes(map);
			model.addAttribute("pageClassName", pageClassName);
			model.addAttribute("category", category);
		} catch (Exception e) {
			model.addAttribute("datePeriodErrorMsg", e);
		}
		
		return PUBLIC_INCLUDE_PATH + "datePeriodTemplate";
	}

	/**
	 * 拼接地址和参数
	 * 
	 * @author Zhilin
	 * @param url1
	 *            地址
	 * @param url2
	 *            参数
	 * @return
	 * @throws ShidaoException
	 */
	private String buildUrl(String url1, String url2) throws ShidaoException {
		if (StringUtil.isNullOrEmpty(url1)) {
			throw new ShidaoException("没有有效的地址");
		}
		if (StringUtil.isNullOrEmpty(url2)) {
			return url1;
		} else if (url1.endsWith("?") || url1.endsWith("&")) {
			return url1 + url2;
		} else if (url1.contains("?")) {
			return url1 + "&" + url2;
		} else {
			return url1 + "?" + url2;
		}
	}
}
