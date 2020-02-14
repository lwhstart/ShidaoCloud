package com.shidao.controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.businessHandler.HandlerFactory;
import com.shidao.enums.AppointmentCategory;
import com.shidao.enums.Position;
import com.shidao.model.SdbsAppointment;
import com.shidao.service.SdbsAppointmentService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.util.CustomerSessionManager;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ListResult;
import com.shidao.util.ShidaoException;
import com.shidao.vo.SdbsAppointmentVO;

@RestController
@RequestMapping(value = "/sdbsAppoinment")
public class SdbsAppointmentController extends BaseController {

	@Autowired
	private SdbsAppointmentService appointmentService;

	@Autowired
	private SdcomEmployeeService employeeService;

	/**
	 * 预约列表
	 * 
	 * @param appointVo
	 *            customer.name搜索客户姓名， customer.mobile查询手机号码
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(SdbsAppointment condition, Integer pageNum, Integer pageSize) {
		try {
			ListResult<SdbsAppointment> sdbsAppointmentVOsList = appointmentService.list(condition, pageNum, pageSize);
			return JsonUtil.succeedJson(sdbsAppointmentVOsList);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/getDosageDays/{id}", method = RequestMethod.GET)
	public JSONObject getgetDosageDays(@PathVariable(value = "id") Integer id) {
		try {
			SdbsAppointmentVO cusList = appointmentService.getCustomerConsumption(id);
			return JsonUtil.succeedJson(cusList);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 插入预约，仅仅为工作人员使用
	 * 
	 * @param request
	 * @param appointment
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(HttpServletRequest request, SdbsAppointment appointment) {
		try {
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession(), false);

			CustomerSessionManager cManager = new CustomerSessionManager(request.getSession(), false);
			// 设置客户编号，客户预约
			if (cManager.isLoggedIn()) {
				appointment.setCustomerId(cManager.getCustomerId());
			}
			//员工登录预约
			else if (sManager.isLoggedIn()) {
				if (appointment.getClubId() == null) {
					appointment.setClubId(sManager.getClubId());
				}
				appointment.setAppointedBy( sManager.getEmployeeId());
				
				Position employeeLoginPosition = sManager.getPositionEnum();
				if ((employeeLoginPosition == Position.Regulate 
						|| employeeLoginPosition == Position.Doctor 
						|| employeeLoginPosition == Position.Therapist)
						&& appointment.getDoctorId() == null) {
					appointment.setDoctorId(sManager.getEmployeeId());
				}
				if (employeeLoginPosition == Position.Assistant && appointment.getAssistantId() == null) {
					appointment.setAssistantId(sManager.getEmployeeId());
				}
			}
			else {
				throw new ShidaoException("请登录");
			}

			// 如果是医生自己预约说明是快速预约，或者为顾客预约
			if (appointment.getDoctorId() != null && appointment.getAssistantId() == null) {
					// 判断医生是否有助理，如果没有把默认助理加上去，若没有默认助理则抛异常
					appointment.setAssistantId(employeeService.getAssistantOfDoctor(appointment.getDoctorId(),
							appointment.getClubId()));
			}
			HandlerFactory.createAppointmentHandler(AppointmentCategory.TCM).insert(appointment);

			JSONObject result = JsonUtil.succeedJson(appointment.getId());
			result.put("uuid", appointment.getUuid());
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 远程终端预约
	 * 
	 * @param appointment
	 * @param category
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/insert/{category}")
	public JSONObject insertAppointment(SdbsAppointment appointment,
			@PathVariable(value = "category") AppointmentCategory category, HttpServletRequest request) {
		try {
			EmployeeSessionManager eSessionManager = new EmployeeSessionManager(request.getSession());
			appointment.setClubId(eSessionManager.getClubId());
			appointment.setAppointedBy(eSessionManager.getEmployeeId());
			appointment.setCategory(category);
			switch (category) {
			case RemoteDiagnostic:
				HandlerFactory.createAppointmentHandler(AppointmentCategory.RemoteDiagnostic).insert(appointment);
				break;
			default:
				throw new ShidaoException("该接口暂不开放该类型");
			}
			return JsonUtil.succeedJson(appointment.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/{doctorId}/appointmentCount", method = RequestMethod.GET)
	public JSONObject appointmentCount(@PathVariable Integer doctorId, @RequestParam(value = "date") Date date,
			@RequestParam(value = "period") String period) {
		try {
			SdbsAppointmentVO appointVo = new SdbsAppointmentVO();
			appointVo.setDoctorId(doctorId);
			appointVo.setDate(date);
			appointVo.setPeriod(period);
			ListResult<SdbsAppointmentVO> sdbsAppointmentVOsList = appointmentService.list(appointVo, 1,
					Integer.MAX_VALUE);
			return JsonUtil.succeedJson(sdbsAppointmentVOsList);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 当日服务列表
	 * 
	 * @param customerId
	 * @param assistantId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/list/today", method = RequestMethod.GET)
	public JSONObject todayList(SdbsAppointment appoint,Integer pageNum,Integer pageSize,HttpServletRequest request) {
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			appoint.setDate(new Date());
			
			if (appoint.getClubId() == null) {
				appoint.setAssistantId(employeeSessionManager.getEmployeeId());
			}
			
			return JsonUtil.succeedJson(appointmentService.list(appoint, pageNum, pageSize));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 查看七日内预约
	 * 
	 * @param appointVo
	 * @return
	 */
	@RequestMapping(value = "/list/7days", method = RequestMethod.GET)
	public JSONObject sevenDaysList(SdbsAppointment appoint,HttpServletRequest request) {
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			appoint.setDate(null);
			appoint.setListSpecialCondition("7days");// setDayType(7);
			appoint.setAssistantId(employeeSessionManager.getEmployeeId());
			ListResult<SdbsAppointment> sdbsAppointmentVOsList = appointmentService.list(appoint, 1, Integer.MAX_VALUE);
			return JsonUtil.succeedJson(sdbsAppointmentVOsList.getList());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@GetMapping(value="/list/future")
	public JSONObject futureList(SdbsAppointment appoint,Integer pageNum,Integer pageSize,HttpServletRequest request) {
		try {
			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			appoint.setDate(null);
			appoint.setListSpecialCondition("Future");
			appoint.setAssistantId(employeeSessionManager.getEmployeeId());
			return JsonUtil.succeedJson(appointmentService.list(appoint, pageNum, pageSize));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 提醒列表（提前一天）
	 * 
	 * @param customerId
	 * @param assistantId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/list/remind/{assistantId}")
	public JSONObject remind(Integer assistantId, Integer pageNum, Integer pageSize) {
		try {
			SdbsAppointment appoint = new SdbsAppointment();
			appoint.setDate(DateUtils.addDays(new Date(), 1));
			appoint.setAssistantId(assistantId);
			ListResult<SdbsAppointment> sdbsAppointmentsList = appointmentService.list(appoint, pageNum, pageSize);
			return JsonUtil.succeedJson(sdbsAppointmentsList);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 根据预约ID取消预约
	 * 
	 */
	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public JSONObject cancel(Integer id) {
		try {
			HandlerFactory.createAppointmentHandler(id).cancel();
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年5月28日
	 * @param id
	 * @return
	 */
	@GetMapping("/getInfoById")
	public JSONObject getById(Integer id) {
		try {
			return JsonUtil.succeedJson(appointmentService.selectByPrimaryKey(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年5月28日 功能:顾客自己取消预约
	 * @param appointmentId
	 * @param request
	 * @return
	 */
	@PostMapping("/cancelByCustomer")
	public JSONObject cancelByCustomer(Integer appointmentId, HttpServletRequest request) {
		try {
			CustomerSessionManager cm = new CustomerSessionManager(request.getSession());
			HandlerFactory.createAppointmentHandler(appointmentId).cancelByCustomer(cm.getCustomerId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 判断是否显示取下按钮，pad
	 * @author 作者zzp: 
	 * @version 创建时间：2019年1月2日 下午8:08:58 
	 * @param appointmentId
	 * @return
	 */
	@GetMapping(value="/cancelable/{appointmentId}")
	public JSONObject cancelable(@PathVariable("appointmentId") Integer appointmentId) {
		try {
			return JsonUtil.succeedJson(appointmentService.cancelable(appointmentId));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年10月15日
	 * 功能:修改
	 * @param request
	 * @param appointment
	 * @return
	 */
	@PostMapping("/update/assistant")
	public JSONObject updateAssistant(HttpServletRequest request,Integer id,Integer assistantId) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			appointmentService.updateAssistant(id, assistantId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 增加或修改备注 pad端
	 * @author 作者zzp: 
	 * @version 创建时间：2019年1月2日 下午2:59:44 
	 * @param request
	 * @param id
	 * @param description
	 * @return
	 */
	@PostMapping(value="/update/description")
	public JSONObject updateDescription(HttpServletRequest request,Integer id,String description) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			appointmentService.updateDescription(id,description);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月7日<br>
	 * 功能:获取该门店该医生本日各个时间段的顾客信息
	 * @param request
	 * @param doctorUuid
	 * @param date
	 * @return
	 */
	@GetMapping("/doctor/daily")
	public JSONObject doctorDaily(HttpServletRequest request,Integer doctorId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			date = date == null?LocalDate.now():date;
			Map<String, Object> map = new HashMap<>();
			Map<String, Object> condition = new HashMap<>();
			condition.put("doctorId", doctorId);
			condition.put("date", date);
			map.put("periods", appointmentService.getDoctorDaily(doctorId, date, sm.getClubId()));
			map.put("condition", condition);
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}