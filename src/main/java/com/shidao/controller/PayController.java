package com.shidao.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.PayMethod;
import com.shidao.enums.Status;
import com.shidao.model.SdbsNetPayHistory;
import com.shidao.model.SdbsSalesOrder;
import com.shidao.service.PayService;
import com.shidao.service.SdbsNetPayHistoryService;
import com.shidao.service.SdbsSalesOrderService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.PayFactory;
import com.shidao.util.ShidaoException;
import com.shidao.util.StringUtil;
import com.shidao.util.Write;
import com.shidao.vo.PayOrderVo;
import com.shidao.websocket.SendSystemMessage;

@Controller
@RequestMapping(value = "/pay")
public class PayController extends BaseController {

	@Autowired
	private PayService payService;

	@Autowired
	private SdbsSalesOrderService salesOrderService;

	@Autowired
	private SdbsNetPayHistoryService netPayHistoryService;

	private static final String WORKSPACE_PATH = "/display/";

	/**
	 * 支付宝支付
	 * 
	 * @param payMethod（
	 *            ALIPAY，WEIXIN）
	 * @param payOrderVo
	 * @param request
	 * @param httpResponse
	 * @return
	 */
	@GetMapping(value = "/ALIPAY")
	public void payDirect(PayOrderVo payOrderVo, 
			HttpServletRequest request, 
			HttpServletResponse httpResponse) {
		PayFactory payFactory = new PayFactory();
		EmployeeSessionManager sessionManager;
		Integer employeeId = null;
		String outTradeNo = StringUtil.orderNumber();// 订单号
		try {	
			sessionManager = new EmployeeSessionManager(request.getSession());
			payOrderVo.setOutTradeNo(outTradeNo);
			employeeId = sessionManager.getEmployeeId();
			SendSystemMessage.sendPay("E"+employeeId, String.format("订单号%s正在支付,请不要关闭", outTradeNo));
			
			// 支付产品类型
			switch (payOrderVo.getSubject()) {
			case RemoteDiagnosticDoctorFee:
				payOrderVo.setPayOtherParameter(employeeId+"");
				payService.payRemoteDoctoreFee(payOrderVo);
				break;
			case SalesOrdersFee:
				Integer clubId = sessionManager.getClubId();
				String telephone = sessionManager.getClubTelephone();
				Integer warehouseId = sessionManager.getWarehouseId();
				payOrderVo.setPayOtherParameter(String.format("%d_%d_%d_%s", employeeId, clubId, warehouseId, telephone));
				payService.paySalesOrdersFee(payOrderVo);
				break;
			default:
				throw new ShidaoException("没有找到该产品类型");
			}
			// 支付
			payFactory.aliPay(payOrderVo, httpResponse);
		} catch (Exception e) {
			try {
				SendSystemMessage.sendPay("E"+employeeId, String.format("支付宝生成支付单出错，请联系程序员协助解决。错误原因：%s", e.getMessage()));
				httpResponse.sendRedirect("error？errorMsg="+URLEncoder.encode(e.getMessage(), payFactory.CHARSET));
			} catch (Exception e1) {
				_Logger.error(e1.getMessage());
			}
		}
	}
	
	@GetMapping(value = "/error")
	public String payDirect(HttpServletRequest request, 
			Model model, String errorMsg) {
		model.addAttribute("title", "支付出错");
		model.addAttribute("errorMsg", errorMsg);
		return "/display/information/payError";
	}
	/**
	 * 微信支付
	 * 
	 * @param payMethod
	 * @param payOrderVo
	 * @param request
	 * @param httpResponse
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/WEIXIN")
	public String WeChatPayFrom(PayOrderVo payOrderVo, HttpServletRequest request, HttpServletResponse httpResponse,
			Model model) {
		EmployeeSessionManager sessionManager;
		Integer employeeId = null;
		String outTradeNo = StringUtil.orderNumber();// 订单号
		try {
			sessionManager = new EmployeeSessionManager(request.getSession());
            employeeId = sessionManager.getEmployeeId();
            SendSystemMessage.sendPay("E"+employeeId, String.format("订单号%s正在支付,请不要关闭", outTradeNo));

			payOrderVo.setOutTradeNo(outTradeNo);
			// 支付产品类型
			switch (payOrderVo.getSubject()) {
			case RemoteDiagnosticDoctorFee:
				payOrderVo.setPayOtherParameter(employeeId+"");
				payService.payRemoteDoctoreFee(payOrderVo);
				break;
			case SalesOrdersFee:
				Integer clubId = sessionManager.getClubId();
				String telephone = sessionManager.getClubTelephone();
	            Integer warehouseId = sessionManager.getWarehouseId();
	            payOrderVo.setPayOtherParameter(String.format("%d_%d_%d_%s", employeeId, clubId, warehouseId, telephone));
				payService.paySalesOrdersFee(payOrderVo);
				break;
			default:
				throw new ShidaoException("没有找到该产品类型");
			}
			// 支付
			String codeUrl = new PayFactory().weChatPay(payOrderVo, request);
			model.addAttribute("codeUrl", codeUrl);
			model.addAttribute("payOrderVo", payOrderVo);
		} catch (Exception e) {
			_Logger.error("支付异常：", e);
			try {
				SendSystemMessage.sendPay("E"+employeeId, String.format("微信支付生成支付单出错，请联系程序员协助解决。错误原因：%s", e.getMessage()));
			} catch (ShidaoException e1) {
				_Logger.error("发送微信支付消息错误："+e1.getMessage());
			}
			model.addAttribute("errorMsg", e.getMessage());
			return "redirect:error";
		}
		return WORKSPACE_PATH + "payment/weChatQRCode";
	}

	/**
	 * 扫描返回
	 * 
	 * @param request
	 * @param payMethod
	 * @return
	 */
	@RequestMapping(value = "/ALIPAY/notify")
	@ResponseBody
	public String notifyUrl(HttpServletRequest request) {
		_Logger.info("Alipay notify...." + request.getParameterMap());
		Integer employeeId = null;
		String outTradeNo = request.getParameter("out_trade_no");
		BigDecimal payAmount = new BigDecimal(request.getParameter("total_amount"));
		String payOtherParameter = request.getParameter("passback_params");
		SdbsNetPayHistory netPayHistory = new SdbsNetPayHistory();// 支付记录
		netPayHistory.setOutTradeNo(outTradeNo);
		netPayHistory.setPayAmount(payAmount);
		try {			
			employeeId = payService.payOrder(outTradeNo, PayMethod.valueOf("支付宝"), payAmount,payOtherParameter);
			netPayHistory.setStatus(Status.Succeed);
		} catch (Exception e) {
			_Logger.error("支付失败：" + e);
			try {
				SendSystemMessage.sendPay("E"+employeeId, String.format("订单号%s支付宝支付失败：%s", outTradeNo ,e.getMessage()));
			} catch (ShidaoException e1) {
				_Logger.error("支付宝支付报错:"+e.getMessage());
			}
			netPayHistory.setErrorMsg(e.getMessage());
			netPayHistory.setStatus(Status.Fail);
		}
		try {
			_Logger.info("进入插入支付状态页面" + netPayHistory.getOutTradeNo());
			netPayHistoryService.insertSelective(netPayHistory);
		} catch (ShidaoException e1) {
			Write.text("E:\\pay.txt", e1.getMessage());
		}
		return "success";
	}

	@RequestMapping(value = "/WEIXIN/notify")
	@ResponseBody
	public void weixinNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
		SdbsNetPayHistory netPayHistory = new SdbsNetPayHistory();// 支付记录
		Integer employeeId = null;
		String outTradeNo = null;
		try (ServletOutputStream servletOutputStream = response.getOutputStream();){
			// 读取参数
			InputStream inputStream;
			StringBuffer sb = new StringBuffer();
			inputStream = request.getInputStream();
			String s;
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((s = in.readLine()) != null) {
				sb.append(s);
			}
			in.close();
			inputStream.close();
			_Logger.info("微信支付返回参数：" + sb.toString());

			//String sb="<xml><appid><![CDATA[wx8e061adfc548f819]]></appid><bank_type><![CDATA[CFT]]></bank_type><cash_fee><![CDATA[1]]></cash_fee><device_info><![CDATA[WEB]]></device_info><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[Y]]></is_subscribe><mch_id><![CDATA[1414581602]]></mch_id><nonce_str><![CDATA[DCA2300EF1A94FF690B0FC44AF143DB7]]></nonce_str><openid><![CDATA[ox_SxwRmOseWjCAvidN0lx-e6sY4]]></openid><out_trade_no><![CDATA[sdjk20180522080046996C06]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[F981D467C73330A52B619B7B74AA06EE]]></sign><time_end><![CDATA[20180522200102]]></time_end><total_fee>1</total_fee><trade_type><![CDATA[NATIVE]]></trade_type><transaction_id><![CDATA[4200000135201805225767079339]]></transaction_id></xml>";
			// 解析xml成map
			Map<String, String> m = new HashMap<String, String>();
			PayFactory payFactory = new PayFactory();
			m = payFactory.xmlToMap(sb.toString());

			// 过滤空 设置 TreeMap 微信验签需要排序
			SortedMap<String, String> packageParams = new TreeMap<String, String>();

			m.forEach((k, v) -> {
				if (null != v)
					v = v.trim();

				_Logger.info(String.format("微信回掉参数：【%s:%s】", k, v));
				packageParams.put(k, v);
			});
			// 这里验签省略。。。
			if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
				outTradeNo = (String) packageParams.get("out_trade_no");
				String amountFee = packageParams.get("cash_fee").toString();
				_Logger.info("支付宝进入获取额外参数。");
				String payOtherParameter = packageParams.get("attach");
				 _Logger.info("额外参数payOtherParameter："+payOtherParameter);
				BigDecimal payAmount =  new BigDecimal(amountFee).divide(new BigDecimal("100"));//(float)amountFee/100;
				netPayHistory.setPayAmount(payAmount);
				netPayHistory.setOutTradeNo(outTradeNo);

				employeeId = payService.payOrder(outTradeNo, PayMethod.valueOf("微信"), payAmount, payOtherParameter);
				netPayHistory.setStatus(Status.Succeed);
			} else {
				netPayHistory.setStatus(Status.Fail);
			}
			netPayHistoryService.insertSelective(netPayHistory);
			// 通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
			String resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
					+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml>";
			// ------------------------------
			// 处理业务完毕
			// ------------------------------
			BufferedOutputStream out = new BufferedOutputStream(servletOutputStream);
			out.write(resXml.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			netPayHistory.setErrorMsg(e.getMessage());
			netPayHistory.setStatus(Status.Fail);
			try {
				SendSystemMessage.sendPay("E"+employeeId, String.format("订单号%s微信支付失败：%s", outTradeNo ,e.getMessage()));				
				netPayHistoryService.insertSelective(netPayHistory);
			} catch (ShidaoException e1) {
				Write.text("E:\\pay.txt", e1.getMessage());
			}
		}
	}

	/**
	 * 支付完成跳转倒计时关闭页面
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws ShidaoException
	 */
	@RequestMapping(value = "/result")
	public String returnUrl(HttpServletRequest request, Model model) throws ShidaoException {
		return WORKSPACE_PATH + "management/OperationDone";
	}

	/**
	 * 退款
	 * 
	 * @param salesOrderId
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/refund/{salesOrderId}")
	@ResponseBody
	public JSONObject refund(@PathVariable(value = "salesOrderId") Integer salesOrderId, HttpServletRequest request) {
		try {
			SdbsSalesOrder salesOrder = salesOrderService.selectByPrimaryKey(salesOrderId);
			if (salesOrder == null || salesOrder.getPayDate() == null) {
				throw new ShidaoException("没有找到可退款的订单");
			}
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession());
			payService.refund(salesOrder, sManager.getEmployeeId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
