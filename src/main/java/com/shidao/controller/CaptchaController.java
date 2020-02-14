package com.shidao.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.util.CaptchaUtil;
import com.shidao.util.DateUtil;
import com.shidao.util.JsonUtil;
import com.shidao.util.SMSUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value="/captcha")
public class CaptchaController extends BaseController{
  
	/**
	 * 
	 * @param mobile
	 *            手机号码
	 * @return 
	 */
	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public JSONObject sendCaptcha(@RequestParam(value = "mobile") String mobile) {
		try {
			SMSUtil.sendCaptcha(mobile);
			return JsonUtil.succeedJson();
		} catch (ShidaoException e) {
			return JsonUtil.errjson(e);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 验证码倒计时
	 */
	@RequestMapping(value="/countDown" ,method = RequestMethod.POST)
	public JSONObject countDown(@RequestParam(value="mobile") String mobile){
		try {
			Date lastVisitedDate = (Date) CaptchaUtil.Instance.dataMap.get(mobile).get("lastVisitedDate");
			Date date = new Date();
			Integer result=DateUtil.dateSub(date, lastVisitedDate);
			if(result>=0 && result<2*60){
				return JsonUtil.succeedJson(result);
			}
			return JsonUtil.emptyJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 验证码是否正确
	 * @param code
	 * @param mobile
	 * @return
	 */
	/*@RequestMapping(value="/verify" ,method=RequestMethod.POST)
	public JSONObject captchaIsRight(@RequestParam(value="code") String code,@RequestParam(value="mobile") String mobile){
		try {
			if(!code.equals(CaptchaUtil.Instance.getCode(mobile)))
				return JsonUtil.errjson("验证码错误");
			
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}*/

}
