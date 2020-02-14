package com.shidao.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsCustomer;
import com.shidao.model.SdbsRelationCustomerWeixin;
import com.shidao.service.SdbsRelationCustomerWeixinService;
import com.shidao.service.WeixinSmallProgramService;
import com.shidao.util.CaptchaUtil;
import com.shidao.util.CustomerSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value="/weixinSmallProgram")
public class WeixinSmallProgramController {
	protected Logger _Logger = Logger.getLogger(getClass().getSimpleName());
	
	@Autowired
	private WeixinSmallProgramService weixinSmallProgramService;
	
	@Autowired
	private SdbsRelationCustomerWeixinService relationCustomerWeixinService;
	
	/**
	 * 微信小程序登陆
	 * @param request
	 * @param code
	 * @param customer
	 * @return
	 */
	@PostMapping(value="/login")
	public JSONObject getLogin(HttpServletRequest request,@RequestParam(value="code") String code,SdbsCustomer customer,String spName) {
		try {
			JSONObject openidAndSessionKey = weixinSmallProgramService.getOpenidAndSessionKey(code,spName);
			if(openidAndSessionKey == null) {
				throw new ShidaoException("程序配置出错，请联系管理员，更新配置。");
			}
			Object openId = openidAndSessionKey.get("openid");
			Map<String, Object> map = weixinSmallProgramService.login(openId.toString(),customer);
			request.getSession().setAttribute("customerId", ((SdbsCustomer) map.get("customer")).getId());
			JSONObject jsonObject = JsonUtil.succeedJson((SdbsCustomer)map.get("customer"));
			jsonObject.put("openId", openId);
			jsonObject.put("isNew", map.get("isNew"));
			return jsonObject;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 判断session是否存在
	 * @param request
	 * @return
	 */
	@GetMapping(value="/isLoggedIn")
	public JSONObject getSession(HttpServletRequest request) {
		try {
			CustomerSessionManager customerSessionManager = new CustomerSessionManager(request.getSession());
			return JsonUtil.succeedJson(customerSessionManager.isLoggedIn());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 保持登陆
	 * @param request
	 * @param openId
	 * @param sessionId
	 * @return
	 */
	@PostMapping(value="/keepSession")
	public JSONObject getKeepSession(HttpServletRequest request,
			@RequestParam(value="openId") String openId,
			@RequestParam(value="sessionId") String sessionId) {
		try {
			CustomerSessionManager customerSessionManager = new CustomerSessionManager(request.getSession(),false);
			if (!customerSessionManager.isLoggedIn()) {
				SdbsRelationCustomerWeixin relationCustomerWeixin = relationCustomerWeixinService.selectByOpenid(openId);
				if (relationCustomerWeixin == null) {
					throw new ShidaoException("该openid没有绑定用户");
				}
				request.getSession().setAttribute("customerId", relationCustomerWeixin.getCustomerId());
				sessionId = request.getSession().getId();
			}
			return JsonUtil.succeedJson(sessionId);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
	/**
	 * 绑定手机
	 * @param request
	 * @param customerName
	 * @param mobile
	 * @param vCode
	 * @return
	 */
	@PostMapping(value="/bindMobile")
	public JSONObject bindMobile(HttpServletRequest request,String customerName,
			@RequestParam(value="mobile") String mobile,
			@RequestParam(value="vCode") String vCode,
			@RequestParam(value="openId") String openId) {
		try {
			CustomerSessionManager customerSessionManager = new CustomerSessionManager(request.getSession());
			Integer customerId = customerSessionManager.getCustomerId();
			SdbsCustomer customer = weixinSmallProgramService.bindMobile(customerId,customerName,mobile,vCode,openId);
			request.getSession().setAttribute("customerId", customer.getId());
			CaptchaUtil.Instance.removerCode(mobile);
			return JsonUtil.succeedJson(customer);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping(value="/relieveBinding")
	public JSONObject relieveBinding(HttpServletRequest request,@RequestParam(value="openId") String openId,@RequestParam(value="vCode") String vCode) {
		try {
			CustomerSessionManager customerSessionManager = new CustomerSessionManager(request.getSession());
			weixinSmallProgramService.relieveBinding(customerSessionManager.getCurrent().getMobile(),openId,vCode);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	//获取当前系统时间
	@GetMapping(value="/systemTime")
	public JSONObject systemTime() {
		try {
			return JsonUtil.succeedJson(new Date());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
//	@PostMapping(value="/decrypt/shareInfo")
//	public JSONObject decryptShareInfo(HttpServletRequest request,
//			@RequestParam(value="encryptedData") String encryptedData,
//			@RequestParam(value="iv") String iv,
//			@RequestParam(value="weixinSessionKey") String weixinSessionKey) {
//		try {
//			_Logger.info(String.format("分享解密传入值:encryptedData %s,iv %s", encryptedData,iv,weixinSessionKey));
//			weixinSmallProgramService.decryptShareInfo(encryptedData,iv,weixinSessionKey.toString());
//			return JsonUtil.succeedJson();
//		} catch (Exception e) {
//			return JsonUtil.errjson(e);
//		}
//	}
	
	/**
	 * 获取用户信息
	 * @param request
	 * @return
	 */
//	@RequestMapping(value="/customer",method=RequestMethod.GET)
//	public JSONObject ceshisession(HttpServletRequest request) {
//		try {
//			Object customer = request.getSession().getAttribute("weiixnCustomer");
//			return JsonUtil.succeedJson(customer);
//		} catch (Exception e) {
//			return JsonUtil.errjson(e);
//		}
//	}
	
}
