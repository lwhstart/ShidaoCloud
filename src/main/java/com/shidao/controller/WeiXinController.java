package com.shidao.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.shidao.enums.WeixinState;
import com.shidao.model.SdbsCustomer;
import com.shidao.service.WeixinService;
import com.shidao.util.CaptchaUtil;
import com.shidao.util.ShidaoException;
import com.shidao.util.StringUtil;

@Controller
@RequestMapping(value="/weixin")
public class WeiXinController extends BaseController{
	
	@Autowired
	private WeixinService weixinService;

	@RequestMapping(value="/response")
	public String getLoginResponse(@RequestParam(value = "code", required = false) String code,WeixinState state,
			HttpServletRequest request){
		_Logger.info(String.format("开始进入绑定获取WeixinState：%s", state));
		if (!StringUtil.isNullOrEmpty(code)) {
			// 重定向,是否綁定手機
			try {
				SdbsCustomer cust=weixinService.loginWithCode(code);
				// 如果没有手机，则重定向到绑定手机
				_Logger.info("mobile:"+cust.getMobile());
				request.getSession().setAttribute("openid", cust.getWeixinOpenid());
				if (StringUtil.isNullOrEmpty(cust.getMobile())) {
					_Logger.info("state.name():"+state.name());
					return String.format("redirect:/binding?state=%s", state.name());
				}
				request.getSession().setAttribute("customerId", cust.getId());
				_Logger.info("用户姓名："+cust.getName());
				request.getSession().setAttribute("customerName", cust.getName());
				request.getSession().setAttribute("customerLevel", cust.getMemberType());
				_Logger.info("用户会员等级："+cust.getMemberType());
				request.getSession().setAttribute("customer", cust);
				request.getSession().setAttribute("uid", cust.getWebsocketId());
				// 如果有手机，则冲顶系那个到首页
			} catch (Exception exp) {
				_Logger.error(String.format("微信绑定手机%s", exp.getMessage()));
			}
		}
		_Logger.info("登陆成功，正在跳转页面");
		return state.getRedirectUrl();
	}
	
	/*
	 * 绑定手机给微信登录用户
	 */
	@RequestMapping(value = "/bind", method=RequestMethod.POST)
	public String bindingMobile(String mobile, String vCode,WeixinState state,
			HttpServletRequest request,Model model) {
		_Logger.info(String.format("获取到WeixinState:%s", state));
		    _Logger.info("bindingMobile");
		    try {
				if (StringUtil.isNullOrEmpty(mobile)) {
					throw new ShidaoException("请输入手机号码");
				}
				if (StringUtil.isNullOrEmpty(vCode)) {
					throw new ShidaoException("请输入验证码");
				}
				
				String code = CaptchaUtil.Instance.getCode(mobile);
				_Logger.info(String.format("vCode:%s&code:%s", vCode,code));
				if (!code .equals(vCode)) {
					throw new ShidaoException("验证码不正确");
				}
				
				SdbsCustomer cust = weixinService.bindingMobile(mobile, request
						.getSession().getAttribute("openid").toString());
				request.getSession().setAttribute("customerId", cust.getId());
				request.getSession().setAttribute("customer", cust);
				request.getSession().setAttribute("mobile", cust.getMobile());
				request.getSession().setAttribute("customerLevel", cust.getMemberType());
				request.getSession().setAttribute("customerName", cust.getName());
			} catch (Exception e) {
				try {
					return String.format("redirect:/binding?errorMsg=%s&state=%s", URLEncoder.encode(e.getMessage(), "utf-8"),state);
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
			return state.getRedirectUrl();
	}
	
//	/**
//	 * 读取微信MP_verify_nHMJoKLuWwGTTLmp.txt
//	 * @param response
//	 * @param txtPath
//	 * @throws IOException
//	 */
//	@RequestMapping(value="/{txtPath}")
//	public void weiixnText(HttpServletResponse response,@PathVariable(value="txtPath") String txtPath) throws IOException { 
//		response.setContentType("text/html;charset=UTF-8");
//		String path=String.format("E:\\servertools\\apache-tomcat-9.0.0.M21\\webapps\\ROOT\\filePath\\%s.txt", txtPath);		
//		try(
//				FileReader fr = new FileReader(path);
//				BufferedReader br = new BufferedReader(fr);
//				PrintWriter pr = response.getWriter()
//		){
//			String content = br.readLine();
//			while(content != null) {
//				response.getWriter().write(content);
//				content = br.readLine();
//			}
//		}catch(Exception ex) {
//			ex.printStackTrace();
//		}
//	}
	
}
