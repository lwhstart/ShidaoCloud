package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.Position;
import com.shidao.model.MzMaizhenyiHistory;
import com.shidao.service.MzMaizhenyiHistoryService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@Controller
@RequestMapping(value = "/pulse")
public class PulseSystemJspController extends BaseController {
	private static final String PULSE_PATH = "/display/pulseReport/";

	@Autowired
	private MzMaizhenyiHistoryService historyService;

	// 脉诊报告
	@RequestMapping(value = "/report/{category}/{uuid}", method = RequestMethod.GET)
	public String pulseReport(HttpServletRequest request, Model model,
			@PathVariable(value = "category") String category, @PathVariable(value = "uuid") String uuid) {
			MzMaizhenyiHistory history = new MzMaizhenyiHistory();
		try {
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession(),false);
			history = historyService.selectSimpleByUuid(uuid);
			Boolean isDoctor = sManager.getEmployeeId() != null
					&& sManager.getPosition().equals(Position.Doctor.getText());
			model.addAttribute("isDoctor", isDoctor);
			model.addAttribute("result", historyService.generateMaizhenyiResultReport(uuid, category));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		switch (history.getCategory()) {
		case FZC:
			return PULSE_PATH + "pulseReport" + category.toUpperCase()+"FZC";
		default:
			return PULSE_PATH + "pulseReport" + category.toUpperCase();
		}
	}

	@RequestMapping(value = "/report1/{category}/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject pulseReports1(HttpServletRequest request, Model model,
			@PathVariable(value = "category") String category, @PathVariable(value = "uuid") String uuid) {
		try {
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession(),false);
			Boolean isDoctor = sManager.getEmployeeId() != null
					&& sManager.getPosition().equals(Position.Doctor.getText());
			JSONObject result = JsonUtil.succeedJson(historyService.generateMaizhenyiResultReport(uuid, category));
					
			result.put("isDoctor", isDoctor);
			return result;
		} catch (ShidaoException e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/research/{uuid}", method = RequestMethod.GET)
	public String research(HttpServletRequest request, Model model, @PathVariable(value = "uuid") String uuid) {
		MzMaizhenyiHistory history = new MzMaizhenyiHistory();
		try {
			new EmployeeSessionManager(request.getSession());
			history = historyService.selectByUuid(uuid);
			model.addAttribute("history",history);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		switch (history.getCategory()) {
		case FZC:
			return PULSE_PATH + "mzyResearchFZC";
		case WBFX:
			return PULSE_PATH + "mzyResearch";
		default:
			return null;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/json/research/{uuid}", method = RequestMethod.GET)
	public JSONObject researchJson(HttpServletRequest request, Model model, @PathVariable(value = "uuid") String uuid) {
		try {
			JSONObject result = JsonUtil.succeedJson();
			result.put("history", historyService.selectByUuid(uuid));
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

}
