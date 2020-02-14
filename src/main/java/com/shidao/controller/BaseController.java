package com.shidao.controller;

import java.time.LocalDate;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.shidao.framework.MyDateEditor;
import com.shidao.framework.MyLocalDateEditor;

public class BaseController {
	protected Logger _Logger = Logger.getLogger(getClass().getSimpleName());

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new MyDateEditor());
		binder.registerCustomEditor(LocalDate.class, new MyLocalDateEditor());
	}
}
