package com.shidao.controller;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdepNews;
import com.shidao.service.SdepNewsService;
import com.shidao.util.JsonUtil;
import com.shidao.util.ListResult;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value = "/sdepNews1")
public class SdepNewsController {

	@Autowired
	SdepNewsService sdepNewsService;

/*	@Autowired
	SdepUrlService sdep;*/
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public JSONObject SdepNews(@PathVariable Integer id) {
		try {
			return JsonUtil.succeedJson(sdepNewsService.selectByPrimaryKey(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}	
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject SdepNewsInfo(SdepNews sdepNews) {
		try {
			ListResult<SdepNews> newsInfo = sdepNewsService.list(sdepNews, 1, Integer.MAX_VALUE);
			return JsonUtil.succeedJson(newsInfo.getList());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}	
	}
	
	@RequestMapping(value = "/a", method = RequestMethod.GET)
	public JSONObject sdepNews() throws ShidaoException {
		try {
			SdepNews condition = new SdepNews();
			System.out.println(sdepNewsService.list(condition, 1, 5).getSize());
			return  JsonUtil.succeedJson(sdepNewsService.list(condition, 1, 5));
		} catch (ShidaoException e) {
		return JsonUtil.errjson(e);
	}
	}
}
