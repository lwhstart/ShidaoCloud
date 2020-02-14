package com.shidao.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.Position;
import com.shidao.enums.RestControllerStatus;
import com.shidao.exceptions.ItemDisabledException;
import com.shidao.exceptions.ItemExistedException;
import com.shidao.model.SdcomEmployee;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ListResult;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value = "/sdcomEmployee")
public class SdcomEmployeeController extends BaseController {

	@Autowired
	private SdcomEmployeeService employeeService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public JSONObject login(String loginName, String password, HttpServletRequest request) {

		try {
			Map<String, Object> loginINfo = employeeService.login(loginName, password);
			loginINfo.forEach((k, v) -> request.getSession().setAttribute(k, v));
			JSONObject result = JsonUtil.succeedJson();
			result.putAll(loginINfo);
			result.put("sessionId", request.getSession().getId());
			return result;
		} catch (ShidaoException e) {
			return JsonUtil.errjson(e);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * android医生助理登陆
	 * @param loginName
	 * @param password
	 * @param request
	 * @return
	 */
	@PostMapping(value="/assistant/login")
	public JSONObject assistantLogin(String loginName, String password, HttpServletRequest request) {
		try {
			Map<String, Object> loginINfo = employeeService.login(loginName, password,Position.Assistant,Position.ClubManager);
			loginINfo.forEach((k, v) -> request.getSession().setAttribute(k, v));
			JSONObject result = JsonUtil.succeedJson();
			result.putAll(loginINfo);
			result.put("sessionId", request.getSession().getId());
			return result;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * zzp  2018-07-25
	 * 保持session
	 * @param loginName
	 * @param password
	 * @param sessionId
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/keepSession",method = RequestMethod.POST)
	public JSONObject getSession(@RequestParam(value="loginName") String loginName,
			@RequestParam(value="password") String password,HttpServletRequest request) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession(),false);
			if(!esManager.isLoggedIn()) {
				Map<String, Object> loginINfo = employeeService.login(loginName, password);
				loginINfo.forEach((k, v) -> request.getSession().setAttribute(k, v));
			}
			return JsonUtil.succeedJson(request.getSession().getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 插入员工
	 * @author yzl 2018年6月22日
	 * @param request
	 * @param employee
	 * @return
	 */
	@PostMapping(value = "/insert")
	public JSONObject create(HttpServletRequest request, SdcomEmployee employee) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			employee.setClubId(esManager.getClubId());			
			employeeService.insertSelective(employee);
			return JsonUtil.succeedJson(employee.getId());
		}catch(ItemDisabledException e) {
			return JsonUtil.resultWithStatus(RestControllerStatus.disabled, employee.getId());
		}catch (ItemExistedException e) {
			return JsonUtil.resultWithStatus(RestControllerStatus.existed, employee.getId());
		}
		catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 启用员工，把对应的门店，设置成当前管理员所在的门店。
	 * @author yzl 2018年6月21日
	 * @param request
	 * @param id
	 * @return
	 */
	@PostMapping("/enable")
	public JSONObject enable(HttpServletRequest request, Integer id) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession(), false);
			SdcomEmployee newStatus = new SdcomEmployee();
			newStatus.setId(id);
			newStatus.setEnabled(1);
			newStatus.setClubId(esManager.getClubId());
			return this.update(newStatus,request);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
		
	}

	@PostMapping(value = "/update")
	public JSONObject update(SdcomEmployee employee,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (employee.getId() == null || employee.getId() == 0) {
				throw new ShidaoException("未找到该员工的编号，请创建后修改:" + employee.getName());
			}
			employee.setClubId(sm.getClubId());
			employeeService.updateByPrimaryKeySelective(employee);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(SdcomEmployee condition, Integer pageNum, Integer pageSize) {
		try {
			ListResult<SdcomEmployee> result = employeeService.list(condition, pageNum, pageSize);
			return JsonUtil.succeedJson(result);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 获取医生列表
	 * 
	 * @param clubId
	 * @return
	 */
	@RequestMapping(value = "/doctor/list", method = RequestMethod.GET)
	public JSONObject getDoctors(SdcomEmployee sdcomEmployee) {
		try {
			sdcomEmployee.setPosition(Position.Doctor.getText());
			return JsonUtil.succeedJson(employeeService.list(sdcomEmployee));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}	
	
	/**
	 * 根据岗位获取员工
	 * 
	 * @param clubId
	 * @return
	 */
	@GetMapping("/list/positions")
	public JSONObject getByPositions(HttpServletRequest request, Position[] positions) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(employeeService.listByPosition(es.getClubId(), positions));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@GetMapping(value = "/detail/{id}")
	public JSONObject sdcomEmployeeInfo(@PathVariable Integer id) {
		try {
			return JsonUtil.succeedJson(employeeService.selectByPrimaryKey(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月29日
	 * 功能:根据uuid获取医生详情
	 * @param uuid
	 * @return
	 */
	@GetMapping(value = "/uuid/{uuid}")
	public JSONObject sdcomEmployeeInfo(@PathVariable String uuid) {
		try {
			return JsonUtil.succeedJson(employeeService.selectByUuid(uuid));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 判断手机是否已经被注册
	 * 
	 * @param mobile
	 * @return 存在true 不存在false
	 */
	@RequestMapping(value = "/isRegisted/{mobile}", method = RequestMethod.GET)
	public JSONObject isMobileRegisted(@PathVariable(value = "mobile") String mobile) {
		try {

			return JsonUtil.succeedJson(employeeService.isMobileRegisted(mobile));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
	public JSONObject forgetPassword(@RequestParam String mobile, @RequestParam(value = "password") String password,
			@RequestParam(value = "verifyPassword") String verifyPassword, @RequestParam(value = "code") String code) {
		try {
			employeeService.forgetPassword(mobile, password, verifyPassword, code);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/update/password", method = RequestMethod.POST)
	public JSONObject updatePasswordById(HttpServletRequest request, Model model, SdcomEmployee keeper) {
		try {

			if (keeper.getId() == null)
				throw new ShidaoException("未指定负责人编号");

			employeeService.updateByPrimaryKeySelective(keeper);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@PostMapping(value = "/delete")
	public JSONObject delete(HttpServletRequest request, Integer id) {
		try {
			EmployeeSessionManager esManager =  new EmployeeSessionManager(request.getSession());
			employeeService.delete(id, esManager.getClubId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping(value = "/leave")
	public JSONObject leave(HttpServletRequest request, Integer id) {
		try {
			EmployeeSessionManager esManager =  new EmployeeSessionManager(request.getSession());
			employeeService.leave(id,esManager.getEmployeeId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 员工退出
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/quit")
	public JSONObject quit(HttpServletRequest request) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession(), false);
			if (esManager.isLoggedIn()) {
				request.getSession().invalidate();
			}
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping("/chooseClub")
	public JSONObject chooseClub(Integer clubId, HttpServletRequest request){
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			esManager.setClubId(clubId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 获取门店相关医生的简单信息
	 * @author yzl 2018年12月10日
	 * @param request
	 * @return
	 */
	@GetMapping("/appointable")
	public JSONObject listDoctorTherapist(HttpServletRequest request){
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(employeeService.listByPosition(esManager.getClubId(), Position.appointmentPositions));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
