package com.shidao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manual")
public class ManualController extends BaseController {

	private static final String MANUAL_PATH = "/display/manual/";

	@RequestMapping("/{content}")
	public String gotoManual(@PathVariable(name = "content", required = true) String content) {
		return MANUAL_PATH + content;
	}
}
