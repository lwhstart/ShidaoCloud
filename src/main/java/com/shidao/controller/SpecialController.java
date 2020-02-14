/**
 * 
 */
package com.shidao.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.util.JsonUtil;

/**
 * @author zhangzhuping
 *
 */
@RestController
@RequestMapping(value="/special")
public class SpecialController extends BaseController{
	
	/**
	 * 定时连接，防止掉线
	 * @return
	 */
	@RequestMapping(value="/timedConnection", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject timedConnection(){
		try {
			return  JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
