package com.shidao.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shidao.enums.AppointmentCategory;
import com.shidao.enums.ClubType;
import com.shidao.enums.DatePeriod;
import com.shidao.enums.DepartmentDoctorCategory;
import com.shidao.enums.DiseaseCategory;
import com.shidao.enums.DoctorScheduleCategory;
import com.shidao.enums.MedicalRecordStatus;
import com.shidao.enums.RefundRequestStatus;
import com.shidao.enums.SalesOrderCategory;
import com.shidao.enums.SdepSettingCategory;
import com.shidao.model.SdbsAppointment;
import com.shidao.model.SdbsCustomer;
import com.shidao.model.SdbsDelivery.DeliveryStatus;
import com.shidao.model.SdbsRefundRequest;
import com.shidao.model.SdbsSalesOrder;
import com.shidao.model.SdcomClub;
import com.shidao.model.SdcomDoctorSchedule;
import com.shidao.model.SdcomRelationDiseaseCategoryDoctor;
import com.shidao.model.SdepSetting;
import com.shidao.model.TcmDiseaseCategory;
import com.shidao.model.TcmMedicalRecord;
import com.shidao.service.SdbsAppointmentService;
import com.shidao.service.SdbsDeliveryService;
import com.shidao.service.SdbsRefundRequestsService;
import com.shidao.service.SdbsSalesOrderService;
import com.shidao.service.SdcomClubService;
import com.shidao.service.SdcomDoctorScheduleService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.service.SdcomRelationDiseaseCategoryDoctorService;
import com.shidao.service.SdepSettingService;
import com.shidao.service.TcmDiseaseCategoryService;
import com.shidao.service.TcmMedicalRecordService;
import com.shidao.setting.RemoteDiagnosticSetting;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.StringUtil;

@Controller
@RequestMapping("/remoteDiagnostic")
public class RemoteDiagnosticController {
	
	private static final String REMOTEDIAGNOSTIC_PATH = "/display/remoteDiagnostic/";

	@Autowired
	private SdcomDoctorScheduleService doctorScheduleService;
	
	@Autowired
	private SdcomRelationDiseaseCategoryDoctorService relationDiseaseCategoryDoctorService;
	
	@Autowired
	private SdbsAppointmentService appointmentService;
	
	@Autowired
	private SdepSettingService sdepSettingService;
	
	@Autowired
	private TcmDiseaseCategoryService diseaseCategoryService;
	
	@Autowired
	private SdcomClubService clubService;
	
	@Autowired
	private SdbsSalesOrderService salesOrderService;
	
	@Autowired
	private TcmMedicalRecordService medicalRecordService;
	
	@Autowired
	private SdbsDeliveryService deliveryService;
	
	@Autowired
	private SdcomEmployeeService employeeService;
	
	@Autowired
	private SdbsRefundRequestsService refundRequestsService;
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月9日
	 * 功能:医生排班表列表
	 * @param model
	 * @return
	 */
	@GetMapping("/doctorSchedule/list")
	public String doctorScheduleList(Model model) {
		try {
			SdcomDoctorSchedule condition = new SdcomDoctorSchedule();
			condition.setListSpecialCondition("TodayAndFuture");
			condition.setCategory(DoctorScheduleCategory.RemoteCenter);
			SdbsAppointment appointment = new SdbsAppointment();
			appointment.setCategory(AppointmentCategory.RemoteDiagnostic);
			TcmDiseaseCategory diseaseCategory = new TcmDiseaseCategory();
			diseaseCategory.setCategory(DiseaseCategory.RemoteDiagnositc);
			condition.setAppointment(appointment);
			condition.setDiseaseCategory(diseaseCategory);
			model.addAttribute("list", doctorScheduleService.listDoctorSchedule(condition));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "doctorScheduleList";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月9日
	 * 功能:预约详情
	 * @param model
	 * @param id
	 * @return
	 */
	@GetMapping("/appointment/detail")
	public String appointmentDetail(Model model,Integer id) {
		try {
			SdcomDoctorSchedule doctorSchedule = doctorScheduleService.selectByPrimaryKey(id);
			model.addAttribute("doctorSchedule", doctorSchedule);
			SdbsAppointment condition = new SdbsAppointment();
			condition.setAppointmentIds(doctorSchedule.getAppointmentIds());
			model.addAttribute("appointmentList", appointmentService.listRemoteDiadnostic(condition));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "appointmentDetail";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月9日
	 * 功能:新增排班表
	 * @param model
	 * @return
	 */
	@GetMapping("/doctorSchedule/insert")
	public String doctorScheduleInsert(Model model,Integer diseaseCategoryId,Integer doctorId) {
		try {
			model.addAttribute("diseaseCategoryId", diseaseCategoryId);
			model.addAttribute("doctorId", doctorId);
			SdcomRelationDiseaseCategoryDoctor condition = new SdcomRelationDiseaseCategoryDoctor();
			condition.setCategory(DepartmentDoctorCategory.RemoteDiagnostic);
			model.addAttribute("list", relationDiseaseCategoryDoctorService.listDiseaseCategoryOfDoctor(condition));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			RemoteDiagnosticSetting rdSetting = RemoteDiagnosticSetting.getSetting();
			Calendar cale = Calendar.getInstance();
			cale.add(Calendar.DATE, rdSetting.getDays4Begin());
			model.addAttribute("dateStart", dateFormat.format(cale.getTime()));
			cale.add(Calendar.DATE, rdSetting.getDaysTotal());
			model.addAttribute("dateEnd", dateFormat.format(cale.getTime()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "doctorScheduleInsert";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月9日
	 * 功能:专家管理界面
	 * @return
	 */
	@GetMapping("/expert/management")
	public String expertManagement(Model model) {
		try {
			TcmDiseaseCategory diseaseCategory = new TcmDiseaseCategory();
			diseaseCategory.setCategory(DiseaseCategory.RemoteDiagnositc);
			model.addAttribute("list", diseaseCategoryService.list(diseaseCategory));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "expertManagement";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月10日
	 * 功能:远程会诊基本设置
	 * @param model
	 * @return
	 */
	@GetMapping("/diagnostic/setting")
	public String remoteDiagnosticSetting(Model model) {
		try {
			SdepSetting sdepSetting = new SdepSetting();
			sdepSetting.setCategory(SdepSettingCategory.RemoteDiagnostic);
			sdepSetting.setName("MinutesPerRD");
			List<SdepSetting> defaultMinutes = sdepSettingService.list(sdepSetting);
			model.addAttribute("MinutesPerRD", defaultMinutes.size()>0?defaultMinutes.get(0).getValue():"");
			sdepSetting.setName("DefaultDoctorFee");
			List<SdepSetting> defaultDoctorFee = sdepSettingService.list(sdepSetting);
			model.addAttribute("DefaultDoctorFee", defaultDoctorFee.size()>0?defaultDoctorFee.get(0).getValue():"");
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "remoteDiagnosticSetting";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月10日
	 * 功能:远程今日工作列表
	 * @param model
	 * @return
	 */
	@GetMapping("/doctor/todayWork")
	public String remoteTodayAppointList(Model model,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			SdbsAppointment condition = new SdbsAppointment();
			condition.setDoctorId(sm.getEmployeeId());
			condition.setMedicalRecord(new TcmMedicalRecord());
			condition.setDate(new Date());
			MedicalRecordStatus[] statuses = {
					MedicalRecordStatus.Created,
					MedicalRecordStatus.Diagnosing,
					MedicalRecordStatus.ToBeFilled,
					MedicalRecordStatus.Filled,
					MedicalRecordStatus.Suggested,
					MedicalRecordStatus.Completed};
			Map<MedicalRecordStatus, List<SdbsAppointment>> appointments = new HashMap<>();
			
			for(MedicalRecordStatus status : statuses) {
				condition.getMedicalRecord().setStatus(status);
				appointments.put(status, appointmentService.listRemoteDiadnostic(condition));
			}
			model.addAttribute("total", appointments.values().stream().mapToInt(l -> l.size()).sum());
			model.addAttribute("statuses", statuses);

			model.addAttribute("appointments", appointments);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "doctorTodayWork";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月10日
	 * 功能:远程会诊预约列表
	 * @param model
	 * @param appointment
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/remoteAppoint/list")
	public String remoteAppointList(Model model,SdbsAppointment appointment,String listSpecialCondition,Integer pageNum,Integer pageSize,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("condition", appointment);
			SdcomClub club = new SdcomClub();
			club.setType(ClubType.RemoteTerminal);
			club.setEnabled(1);
			model.addAttribute("clubList", clubService.list(club));
			appointment.setDoctorId(sm.getEmployeeId());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date now = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(now);
			appointment.setCategory(AppointmentCategory.RemoteDiagnostic);
			if (listSpecialCondition.equals("Future"))
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			if (listSpecialCondition.equals("History")) {
				appointment.setMedicalRecord(new TcmMedicalRecord());
				appointment.getMedicalRecord().setId(0);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
			}
			model.addAttribute("result", appointmentService.listRemoteDiagnostic(appointment, pageNum, pageSize));
			model.addAttribute("requestURI",request.getRequestURI());
			model.addAttribute("listSpecialCondition", listSpecialCondition);
			model.addAttribute("date", format.format(calendar.getTime()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "remoteAppointList";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月14日
	 * 功能:远程会诊需要发送快的模态框
	 * @param medicalRecordUuid
	 * @param model
	 * @return
	 */
	@GetMapping("/deliveryModalBox/{medicalRecordUuid}")
	public String deliveryModalBox(@PathVariable("medicalRecordUuid")String medicalRecordUuid,Model model) {
		try {
			model.addAllAttributes(deliveryService.deliverySettlement(medicalRecordUuid,true));
			model.addAttribute("medicalRecordUuid", medicalRecordUuid);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e);
		}
		return "/display/workspace/modal/deliveryModalBox";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月14日
	 * 功能:远程看诊快递列表
	 * @param customerName
	 * @param model
	 * @param request
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/delivery/list")
	public String deliveryList(String customerName,Model model,DeliveryStatus status,
			Integer pageNum,Integer pageSize,HttpServletRequest request) {
		try {
			model.addAttribute("status", DeliveryStatus.values());
			model.addAllAttributes(deliveryService.deliveryRemoteDiagnostic(customerName, status, pageNum, pageSize));
			model.addAttribute("requestURI",request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "deliveryList";
	}
	
	/**
	 * @author yzl, 2018-5-14
	 * @param date 日期， today表示今天，future表示未来
	 * @param customerName 客户名
	 * @return
	 */
	@GetMapping("/remoteTerminal/appointment/list")
	public String remoteTerminalAppointmentList(HttpServletRequest request, Model model, 
			String listSpecialCondition , String customerName) 
	{
		if(listSpecialCondition == null) {
			listSpecialCondition = "Today";
		}
		model.addAttribute("listSpecialCondition", listSpecialCondition);
		model.addAttribute("customerName", customerName);
		try {
		EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());

		SdbsAppointment condition = new SdbsAppointment();
		condition.setListSpecialCondition(listSpecialCondition);
		condition.setClubId(esManager.getClubId());
		if(! StringUtil.isNullOrEmpty(customerName)) {	
			condition.setCustomer(new SdbsCustomer());
			condition.getCustomer().setName(customerName);
		}
		
		Map<MedicalRecordStatus, List<SdbsAppointment>> appointmentList = new LinkedHashMap<>();		
		
		// 2. 获得待完善列表，
		condition.setMedicalRecord(new TcmMedicalRecord());		
		MedicalRecordStatus[] statuses = {
				MedicalRecordStatus.Wait4AppointmentPaying,
				MedicalRecordStatus.Created,
				MedicalRecordStatus.ToBeFilled,
				MedicalRecordStatus.Filled,
				MedicalRecordStatus.Diagnosing,
				MedicalRecordStatus.Suggested,
				MedicalRecordStatus.Completed};
		for(MedicalRecordStatus status:statuses) {
			condition.getMedicalRecord().setStatus(status);
			appointmentList.put(status, appointmentService.listRemoteDiadnostic(condition));
		}		
		model.addAttribute("appointmentList", appointmentList);
		
		model.addAttribute("totalCount", appointmentList.values().stream().mapToInt(l->l.size()).sum());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "remoteTerminalAppointmentList";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月17日
	 * 功能:退款模态框
	 * @param model
	 * @param appointmentId
	 * @return
	 */
	@GetMapping("/refund/modal")
	public String modalRefund(Model model,Integer appointmentId) {
		try{
			SdbsSalesOrder salesOrder = new SdbsSalesOrder();
			salesOrder.setPayed(true);
			salesOrder.setAppointmentId(appointmentId);
			salesOrder.addCategoriesParams(SalesOrderCategory.medicineAndDoctorFee);
			model.addAttribute("list", salesOrderService.listSimpleSalesOrder(salesOrder));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "refundModal";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月15日
	 * 功能:历史看诊列表
	 * @param request
	 * @param model
	 * @param date
	 * @param customerName
	 * @return
	 */
	@GetMapping("/remoteTerminal/appointment/history")
	public String remoteTerminalAppointmentHistory(HttpServletRequest request, Model model,String date, String customerName,Integer pageNum,Integer pageSize) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			model.addAttribute("date", date);
			model.addAttribute("customerName", customerName);
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			String listSpecialCondition = null;
			if (date == null || DatePeriod.Yesterday.getDateStart().before(format.parse(date)))
				listSpecialCondition = "History";
			SdbsAppointment condition = new SdbsAppointment();
			condition.setListSpecialCondition(listSpecialCondition);
			condition.setClubId(esManager.getClubId());
			if (date != null)
				condition.setDate(format.parse(date));
			if(! StringUtil.isNullOrEmpty(customerName)) {	
				condition.setCustomer(new SdbsCustomer());
				condition.getCustomer().setName(customerName);
			}	
			model.addAttribute("result", appointmentService.listRemoteDiagnostic(condition, pageNum, pageSize));
			model.addAttribute("requestURI",request.getRequestURI());
			model.addAttribute("yesterday", DatePeriod.Yesterday.getDateStart());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "remoteTerminalAppointmentHistory";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月14日
	 * 功能:创建预约
	 * @param customerName
	 * @param model
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/created/appointment")
	public String createdAppointment() {
		return REMOTEDIAGNOSTIC_PATH + "createdAppointment";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月15日
	 * 功能:远程会诊支付
	 * @param appointmentId
	 * @param model
	 * @return
	 */
	@GetMapping("/settlement/remoteDiagnostic")
	public String settlementRemoteDiagnosticDoctorFee(Integer appointmentId,Model model,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			SdbsAppointment appointment = appointmentService.selectByPrimaryKey(appointmentId);
			TcmMedicalRecord medicalRecord = medicalRecordService.selectSimpleByAppointmentId(appointmentId);
			Integer doctorId = appointment.getDoctorId();
			Integer diseaseCategoryId = appointment.getDiseaseCategoryId();
			SdbsSalesOrder salesOrder = new SdbsSalesOrder();
			salesOrder.setAppointmentId(appointmentId);
			if (medicalRecord != null) {
				if (medicalRecord.getStatus() == MedicalRecordStatus.Completed)
					salesOrder.addCategoriesParams(SalesOrderCategory.medicine);
				String salesOrderIds = salesOrderService.getSalesOrderIds(salesOrder);
				model.addAttribute("salesOrderIds", salesOrderIds);
				if (salesOrderIds != null){
					model.addAttribute("delivery", deliveryService.selectByPrimaryKey(deliveryService.getDeliveryIdRelation(null, salesOrderIds)));
					model.addAttribute("deliveryNeeded", salesOrderService.selectByPrimaryKey(Integer.parseInt(salesOrderIds.split(",")[0])).getDeliveryNeeded());
				}
			} else
				salesOrder.setCategory(SalesOrderCategory.RemoteDiagnosticDoctorFee);
			List<SdbsSalesOrder> medicineList = salesOrderService.listSimpleSalesOrder(salesOrder);
			model.addAttribute("medicineList", medicineList);
			model.addAttribute("appointmentId", appointmentId);
			model.addAttribute("total", medicineList.stream().filter(a->a.getPayDate() != null).map(b->b.getAmount()).reduce((m,n)->m.add(n)).orElse(BigDecimal.ZERO));
					//mapToDouble(b->b.getAmount()).sum());
			model.addAttribute("reomteDoctorName", employeeService.selectByPrimaryKey(doctorId).getName());
			model.addAttribute("diseaseCategoryName", diseaseCategoryService.selectByPrimaryKey(diseaseCategoryId).getName());
			model.addAttribute("appointmentDate", appointment.getDate());
			model.addAttribute("localDoctorName", sm.getCurrent().getName());
			
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "settlementRemoteDiagnostic";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月11日
	 * 功能:远程会诊药费结算
	 * @param model
	 * @param medicalRecordId
	 * @param request
	 * @return
	 */
	@GetMapping("/medicine/settlement/{medicalRecordUuid}")
	public String medicineSettlement(Model model,@PathVariable String medicalRecordUuid,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			TcmMedicalRecord medicalRecord = medicalRecordService.selectByUuid(medicalRecordUuid);
			Integer doctorId = medicalRecord.getDoctorId();
			Integer diseaseCategoryId = medicalRecord.getAppointment().getDiseaseCategoryId();
			//2.显示药费价格和总计
			SdbsSalesOrder salesOrder = new SdbsSalesOrder();
			salesOrder.setAppointmentId(medicalRecord.getAppointmentId());
			salesOrder.addCategoriesParams(SalesOrderCategory.medicineAndDoctorFee);
			List<SdbsSalesOrder> medicineList = salesOrderService.listSimpleSalesOrder(salesOrder);
			String salesOrderIds = salesOrderService.getSalesOrderIds(salesOrder);
			model.addAttribute("medicineList", medicineList);
			model.addAttribute("total", medicineList.stream().filter(a->a.getPayDate() != null).map(b->b.getAmount()).reduce((m,n)->m.add(n)).orElse(BigDecimal.ZERO));
					//mapToDouble(b->b.getAmount()).sum());
			model.addAttribute("salesOrderIds", salesOrderIds);
			model.addAttribute("medicalRecordUuid", medicalRecordUuid);
			model.addAttribute("reomteDoctorName", employeeService.selectByPrimaryKey(doctorId).getName());
			model.addAttribute("diseaseCategoryName", diseaseCategoryService.selectByPrimaryKey(diseaseCategoryId).getName());
			model.addAttribute("appointmentDate", medicalRecord.getAppointment().getDate());
			model.addAttribute("localDoctorName", sm.getCurrent().getName());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "medicineSettlement";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月16日
	 * 功能:远程会诊退款管理
	 * @param model
	 * @return
	 */
	@GetMapping("/refund/approvalRequired")
	public String refundList(Model model,SdbsRefundRequest condition,Integer pageNum,Integer pageSize,HttpServletRequest request) {
		try {
			model.addAttribute("condition", condition);
			SdcomClub club = new SdcomClub();
			List<ClubType> types = Arrays.asList(ClubType.RemoteTerminal);
			club.setTypes(types);
			club.setEnabled(1);
			model.addAttribute("clubList", clubService.list(club));
			condition.setStatus(RefundRequestStatus.ApprovalRequesting);
			model.addAttribute("result", refundRequestsService.list(condition, pageNum, pageSize));
			model.addAttribute("requestURI",request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return REMOTEDIAGNOSTIC_PATH + "refundApprovalRequired";
	}
	
}
