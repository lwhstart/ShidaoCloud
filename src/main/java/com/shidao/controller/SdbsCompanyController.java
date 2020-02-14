package com.shidao.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsCompany;
import com.shidao.service.SdbsCompanyService;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value = "/sdbsCompany")
public class SdbsCompanyController extends BaseController {

	@Autowired
	private SdbsCompanyService sdbsCompanyService;

	@RequestMapping(value = "/d2d", method = RequestMethod.GET)
	public JSONObject getD2DCompanyByCustomerId(HttpServletRequest request) {
		try {
			SdbsCompany sdbsCompany = sdbsCompanyService.getCompanyByCustomerId(request);
			return JsonUtil.succeedJson(sdbsCompany);
		} catch (ShidaoException e) {
			return JsonUtil.errjson(e);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/*-------For Administrator-------------------------*/
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public JSONObject create(SdbsCompany company) {
		try {
			if (company.getId() != null && company.getId() != 0) {
				throw new ShidaoException("该单位已有编号，不用重复创建。");
			}
			sdbsCompanyService.insertSelective(company);
			return JsonUtil.succeedJson(company.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/", method = RequestMethod.PUT)
	public JSONObject update(SdbsCompany company) {
		try {
			if (company.getId() == null || company.getId() == 0) {
				throw new ShidaoException("未找到该单位的编号，请创建后修改:" + company.getName());
			}
			sdbsCompanyService.updateByPrimaryKeySelective(company);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(SdbsCompany condition, Integer pageNum, Integer pageSize ) {
		try {
			Map<String, Object> result = sdbsCompanyService.listByCondition(condition, pageNum, pageSize);
			return JsonUtil.succeedJson(result);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public JSONObject list(@PathVariable(value="id") Integer id) {
		try {
			SdbsCompany result = sdbsCompanyService.selectByPrimaryKey(id);
			return JsonUtil.succeedJson(result);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
