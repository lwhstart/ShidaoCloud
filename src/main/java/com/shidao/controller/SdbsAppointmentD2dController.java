package com.shidao.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdbsAppointmentD2d;
import com.shidao.model.SdbsAppointmentD2dOrder;
import com.shidao.model.SdbsCustomer;
import com.shidao.service.SdbsAppointmentD2dOrderService;
import com.shidao.service.SdbsAppointmentD2dService;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;
import com.shidao.vo.appointmentD2dStatisticsVo;

@RestController
@RequestMapping(value = "/sdbsAppoinmentD2d")
public class SdbsAppointmentD2dController extends BaseController {

	@Autowired
	private SdbsAppointmentD2dService sdbsAppointmentD2dService;
	

	@Autowired
	private SdbsAppointmentD2dOrderService sdbsAppointmentD2dOrderService;

	@RequestMapping(value = "", method = RequestMethod.POST)
	public JSONObject appoinmentD2d(SdbsAppointmentD2d sdbsAppoinmentD2d, HttpServletRequest request) {
		try {
			Integer curCustomerId = SdbsCustomer.getCurrentId(request);
			sdbsAppoinmentD2d.setCustomerId(curCustomerId);
			sdbsAppointmentD2dService.appoinmentD2d(sdbsAppoinmentD2d);
			return JsonUtil.succeedJson();
		} catch (ShidaoException e) {
			return JsonUtil.errjson(e);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/period/{date}", method = RequestMethod.GET)
	public JSONObject getPeriodByservicTypeAndDate(@PathVariable String date) {
		try {
			List<String> period = sdbsAppointmentD2dService.getPeriodByserviceTypeAndDate(date);
			return JsonUtil.succeedJson(period);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/appointmentStatistics", method = RequestMethod.GET)
	public JSONObject getAppointmentStatistics(@RequestParam(value = "companyId") Integer companyId, Date date,
			String period, Integer isFuture) {
		try {
			List<appointmentD2dStatisticsVo> appointmentStatistics = sdbsAppointmentD2dService
					.getAppointmentStatistics(companyId, date, period, isFuture);
			return JsonUtil.succeedJson(appointmentStatistics);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/myAppointmentStatistics")
	public JSONObject getMyAppointmentStatistics(@RequestParam(value = "companyId") Integer companyId, Date date,
			String period, Integer isFuture, HttpServletRequest request) {
		try {
			List<appointmentD2dStatisticsVo> appointmentStatistics = sdbsAppointmentD2dService
					.getAppointmentStatistics(companyId, date, period, isFuture, request);
			return JsonUtil.succeedJson(appointmentStatistics);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/appointmentRecord", method = RequestMethod.GET)
	public JSONObject getByCustomerId(HttpServletRequest request, @RequestParam(value = "pageNum") Integer pageNum,
			@RequestParam(value = "pageSize") Integer pageSize) {
		try {
			Integer curCustomerid = SdbsCustomer.getCurrentId(request);
			Map<String, Object> sdbsAppoinmentD2ds = sdbsAppointmentD2dService.getByCustomerId(curCustomerid, pageNum,
					pageSize);
			if (!JsonUtil.hasPage(sdbsAppoinmentD2ds)) {
				return JsonUtil.emptyJson();
			}
			return JsonUtil.succeedJson(sdbsAppoinmentD2ds);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/myUpdate", method = RequestMethod.POST)
	public JSONObject getMyUpdate(SdbsAppointmentD2d sdbsAppoinmentD2d, HttpServletRequest request) {
		try {
			SdbsCustomer.getCurrentId(request);
			sdbsAppointmentD2dService.updateByPrimaryKeySelective(sdbsAppoinmentD2d);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public JSONObject cancleAppoinment(Integer appointmentId, HttpServletRequest request) {
		try {
			Integer curCustomerId = SdbsCustomer.getCurrentId(request);
			if (curCustomerId == null) {
				throw new ShidaoException("未登录，请先登录");
			}
			sdbsAppointmentD2dService.cancleAppoiment(appointmentId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/*
	 * 1. 已经取消 2. 可以直接取消 3. 需要扣免费取消次数，如果没有，则会扣除服务单元 0. 不能取消
	 */
	@RequestMapping(value = "/cancelstate/{appointmentId}", method = RequestMethod.POST)
	public JSONObject getCancelState(@PathVariable Integer appointmentId, HttpServletRequest request) {
		try {
			Integer curCustomerId = SdbsCustomer.getCurrentId(request);
			if (curCustomerId == null) {
				throw new ShidaoException("未登录，请先登录");
			}

			return JsonUtil.succeedJson(sdbsAppointmentD2dService.selectByPrimaryKey(appointmentId).getCancelStatus());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/detail/{appointmentId}", method = RequestMethod.GET)
	public JSONObject getAppointmentDetail(@PathVariable(value = "appointmentId") Integer appointmentId,
			HttpServletRequest request) {
		try {
			Integer curCustomerId = SdbsCustomer.getCurrentId(request);
			if (curCustomerId == null) {
				throw new ShidaoException("未登录，请先登录");
			}
			SdbsAppointmentD2d appointment = sdbsAppointmentD2dService.selectByPrimaryKey(appointmentId);
			if (appointment.getCustomerId() != curCustomerId) {
				throw new ShidaoException("非法用户。");
			}
			return JsonUtil.succeedJson(appointment);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	public JSONObject arrangeServer(Integer appointmentId, Integer serverId) {
		try {
			SdbsAppointmentD2d appointmentD2d = new SdbsAppointmentD2d();
			appointmentD2d.setId(appointmentId);
			appointmentD2d.setServerId(serverId);
			appointmentD2d.setLocked(1);
			sdbsAppointmentD2dService.updateByPrimaryKeySelective(appointmentD2d);
			return JsonUtil.succeedJson();

		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}

	}
	
	@RequestMapping(value = "/getd2dList/{orderId}", method = RequestMethod.GET)
	public JSONObject getd2dList(@PathVariable(value = "orderId")  Integer orderId) {
		try {
			List<String> d2dList = sdbsAppointmentD2dService.getAppointmentD2dList(orderId);
			SdbsAppointmentD2dOrder d2dOrder = sdbsAppointmentD2dOrderService.selectD2DInfo(orderId);
			JSONObject result= JsonUtil.succeedJson(d2dList);
			result.put("d2dOrder",d2dOrder);
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

}
