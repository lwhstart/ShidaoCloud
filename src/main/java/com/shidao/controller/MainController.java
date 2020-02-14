package com.shidao.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.shidao.enums.Position;
import com.shidao.enums.WeixinState;
import com.shidao.model.CrmCustomerNotice;
import com.shidao.model.SdbsCustomer;
import com.shidao.model.SdcomClub;
import com.shidao.model.SdcomEmployee;
import com.shidao.model.SdcomRecruitment;
import com.shidao.model.SdepFile;
import com.shidao.model.SdepNews;
import com.shidao.model.TcmHealthAssessment;
import com.shidao.service.CrmCustomerNoticeService;
import com.shidao.service.SdbsAppointmentService;
import com.shidao.service.SdbsCustomerCareService;
import com.shidao.service.SdbsCustomerService;
import com.shidao.service.SdcomClubService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.service.SdcomRecruitmentService;
import com.shidao.service.SdepConsultationService;
import com.shidao.service.SdepContactorRequestService;
import com.shidao.service.SdepFileService;
import com.shidao.service.SdepNewsService;
import com.shidao.service.SdepUrlService;
import com.shidao.service.TcmHealthAssessmentService;
import com.shidao.service.TcmHealthKnowledgeService;
import com.shidao.util.CustomerSessionManager;
import com.shidao.util.ListResult;
import com.shidao.util.ShidaoException;
import com.shidao.util.StringUtil;

@Controller
@RequestMapping("/")
public class MainController extends BaseController {
	private static final String MAIN_PATH = "/display/main/";
	private static final String IMPORTED_PATH = "/display/main/imported/";

	@Autowired
	SdepUrlService sdepUrlService;

	@Autowired
	SdepNewsService sdepNewsService;

	@Autowired
	SdcomEmployeeService sdcomEmployeeService;

	@Autowired
	private SdbsCustomerService sdbsCustomerService;

	@Autowired
	private TcmHealthAssessmentService tcmHealthAssessmentService;

	@Autowired
	SdcomRecruitmentService sdcomRecruitmentService;

	@Autowired
	TcmHealthKnowledgeService tcmHealthKnowledgeService;

	@Autowired
	SdcomClubService sdcomClubService;

	@Autowired
	SdbsCustomerCareService sdbsCustomerCareService;

	@Autowired
	SdbsAppointmentService sdbsAppointmentService;

	@Autowired
	private SdepConsultationService consultationService;

	@Autowired
	private SdepFileService fileService;

	@Autowired
	private SdepContactorRequestService contactorRequestService;

	@Autowired
	private CrmCustomerNoticeService customerNoticeService;

	@RequestMapping(value = "")
	public String defaultJsp() {
		return MAIN_PATH + "index";
	}

	@GetMapping(value = "/{validationFileName:^[\\w\\d]+[.][\\w\\d]+$}")
	public void readFile(HttpServletRequest request,
			@PathVariable(value = "validationFileName") String validationFileName, HttpServletResponse response) {

		String path = Paths.get(request.getServletContext().getRealPath("/validationFiles"), validationFileName)
				.toString();
		response.setCharacterEncoding("UTF-8");
		try (PrintWriter writer = response.getWriter();) {
			try (FileReader fr = new FileReader(path); BufferedReader br = new BufferedReader(fr);) {
				String content;
				List<String> lines = new ArrayList<>();
				while ((content = br.readLine()) != null) {
					lines.add(content);
				}
				writer.write(String.join(System.getProperty("line.separator"), lines));
			} catch (FileNotFoundException e) {
				writer.write("文件不存在：" + validationFileName);
			} catch (NullPointerException e) {
				writer.write("黑洞出现请联系程序员。");
			} catch (Exception e) {
				response.setCharacterEncoding("UTF-8");
				writer.write(e.getLocalizedMessage());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// 医生入口系列

	@RequestMapping(value = "doctor/{pageName}")
	public String redirectDoctorPage(@PathVariable(value = "pageName") String pageName, Model model) {
		return MAIN_PATH + "doctor/" + pageName;
	}
	
	@RequestMapping(value = "{pageName:^(?:(?![.]).)*$}")
	public String redirectPageName(@PathVariable(value = "pageName") String pageName, Model model) {
		return MAIN_PATH + pageName;
	}

	@RequestMapping(value = "usercenter")
	public String userCentenr(Model model, HttpServletRequest request) {
		Object id = request.getSession().getAttribute("customerId");
		if (id == null) {
			return MAIN_PATH + "index";
		}
		SdbsCustomer customer = sdbsCustomerService.selectByPrimaryKey((int) id);
		request.setAttribute("customer", customer);
		return MAIN_PATH + "usercenter";
	}

	@RequestMapping(value = "sdepUrl/footer", method = RequestMethod.GET)
	public String redirectfooter(Model model) throws ShidaoException {
//		SdepUrl condition = new SdepUrl();
//		condition.setType("FOOTER");
//		model.addAttribute("sdepUrlList", sdepUrlService.list(condition));
		return IMPORTED_PATH + "footer";
	}

	@RequestMapping(value = "sdepNews/detail/{id}", method = RequestMethod.GET)
	public String sdepNews(@PathVariable Integer id, Model model) {
		SdepNews sdep = sdepNewsService.selectByPrimaryKey(id);
		model.addAttribute("sdep", sdep);
		return MAIN_PATH + "newsdetails";
	}

	@RequestMapping(value = "sdepNews/list", method = RequestMethod.GET)
	public String sdepNewsInfo(Model model) throws ShidaoException {
		try {
			SdepNews condition = new SdepNews();
			model.addAttribute("latest", sdepNewsService.list(condition, 1, 5).getList());
			condition.setRepresentative(1);
			model.addAttribute("representative", sdepNewsService.list(condition, 1, 10).getList());
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e.getMessage());
		} catch (Exception e) {
			model.addAttribute("errorMsg", "系统错误:" + e.getMessage());
		}
		return MAIN_PATH + "newsinfo";
	}

	@RequestMapping(value = "newstrend", method = RequestMethod.GET)
	public String sdepNewslast(Model model) throws ShidaoException {
		model.addAttribute("latest", sdepNewsService.list(new SdepNews(), 1, 1).getList());
		return MAIN_PATH + "newstrend";
	}

	@RequestMapping(value = "healthAssessment/last/{customerId}", method = RequestMethod.GET)
	public String lastAssessmentDetailByCustomerId(@PathVariable Integer customerId, Model model) {
		TcmHealthAssessment tcmHealthAssessment = tcmHealthAssessmentService.getLastAssessmentRecords(customerId);
		model.addAttribute("result", tcmHealthAssessment);
		return MAIN_PATH + "ourpatents";
	}

	@RequestMapping(value = "recruitment/list", method = RequestMethod.GET)
	public String recruitmentList(Model model) {
		try {
			SdcomRecruitment content = new SdcomRecruitment();
			content.setCompleted(false);
			model.addAttribute("recruitmentList", sdcomRecruitmentService.list(content));

		} catch (Exception e) {
			model.addAttribute("errorMsg", "系统错误:" + e.getMessage());
		}
		return MAIN_PATH + "engageList";
	}

	@RequestMapping(value = "recruitment/detail/{id}", method = RequestMethod.GET)
	public String recruitmentDetail(Model model, @PathVariable int id) {
		model.addAttribute("recruitmentDetail", sdcomRecruitmentService.selectByPrimaryKey(id));
		return MAIN_PATH + "engageDetail";
	}

	@RequestMapping(value = "recruitment/insert", method = RequestMethod.POST)
	public String insertRecruitment(SdcomRecruitment sdcomRecruitment) throws ShidaoException {
		sdcomRecruitmentService.insertSelective(sdcomRecruitment);
		return MAIN_PATH + "redict:recruit/list";
	}

	@RequestMapping(value = "recruitment/update", method = RequestMethod.POST)
	public String updateRecruitment(SdcomRecruitment sdcomRecruitment, @RequestParam int id) throws ShidaoException {
		sdcomRecruitment.setId(id);
		sdcomRecruitmentService.updateByPrimaryKeySelective(sdcomRecruitment);
		return MAIN_PATH + "redict:recruit/list";
	}

	@RequestMapping(value = "healthKnowledge/detail/{id}", method = RequestMethod.GET)
	public String healthKnowledgeDetail(Model model, @PathVariable int id) {
		model.addAttribute("detail", tcmHealthKnowledgeService.selectByPrimaryKey(id));
		return MAIN_PATH + "healthKnowledgeDetail";
	}

	@RequestMapping(value = "imported/{pageName}", method = RequestMethod.GET)
	public String healthKnowledgeDetail(Model model, @PathVariable String pageName, HttpServletRequest request) {
		try {
			if (pageName.equals("nav")) {
				if (request.getSession().getAttribute("customerId") != null) {
					CustomerSessionManager cm = new CustomerSessionManager(request.getSession());
					model.addAttribute("unReadCount", contactorRequestService.getUnReadCount(cm.getWebsocketId()));
				}
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return IMPORTED_PATH + pageName;
	}

	@RequestMapping(value = "Employee/detail/{uuid}", method = RequestMethod.GET)
	public String sdcomEmployeeDetail(Model model, @PathVariable String uuid) {
		try {
			SdcomEmployee doctor = sdcomEmployeeService.selectByUuid(uuid);
			model.addAttribute("employeeDetaile", doctor.transferDetail2Html());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MAIN_PATH + "doctorinfo";
	}

	/**
	 * 查看群英荟萃
	 * 
	 * @param model
	 * @param condition
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/tcmDoctorElites", method = RequestMethod.GET)
	public String lookfordoc(Model model, SdcomEmployee condition, Integer pageNum, Integer pageSize) {
		try {
			model.addAttribute("condition", condition);
			condition.setPosition(Position.Doctor.getText());
			condition.setDisplayedOnEp(1);
			ListResult<SdcomEmployee> result = sdcomEmployeeService.list(condition, pageNum, pageSize);
			List<SdcomEmployee> list = result.getList();
			if (condition.getExpertise() != null && list != null && !list.isEmpty())
				model.addAttribute("result", result);
			else {
				condition.setExpertise(null);
				model.addAttribute("warning", "没有查询到数据，返回全部医生");
				model.addAttribute("result", sdcomEmployeeService.list(condition, pageNum, pageSize));
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MAIN_PATH + "lookfordoc";
	}

	@RequestMapping(value = "tcmClinicChain", method = RequestMethod.GET)
	public String ClubList(Model model, SdcomClub sdcomClub) throws ShidaoException {
		sdcomClub.setDisplayedOnEp(1);
		model.addAttribute("healthy", sdcomClubService.list(sdcomClub));
		return MAIN_PATH + "healthyhome";
	}

	@RequestMapping(value = "tcmClinicChain/detail/{uuid}", method = RequestMethod.GET)
	public String healthyhomeDetail(Model model, @PathVariable("uuid") String uuid) throws ShidaoException {
		SdcomClub club = sdcomClubService.selectByUuid(uuid);
		SdcomEmployee employee = new SdcomEmployee();
		employee.setClubId(club.getId());
		employee.setPosition(Position.Doctor.getText());
		employee.setDisplayedOnEp(1);
		model.addAttribute("club", club);
		model.addAttribute("doctors", sdcomEmployeeService.list(employee));
		return MAIN_PATH + "healthyhomeDetail";
	}

	@GetMapping("/intelligentHardware")
	public String getIntelligentHardware() {
		return MAIN_PATH + "intelligentHardware";
	}
	
	@GetMapping("/shop")
	public String getShop() {
		return MAIN_PATH + "shop";
	}
	
	@GetMapping("/sugarknows")
	public String getSugarKnows() {
		return MAIN_PATH + "sugarknows";
	}

	@RequestMapping(value = "offline/list", method = RequestMethod.GET)
	public String sdcomClubList(Model model, SdcomClub SdcomClub) throws ShidaoException {
		model.addAttribute("offline", sdcomClubService.list(SdcomClub));
		return MAIN_PATH + "offlinestore";
	}

	// v空间
	@RequestMapping(value = "download/vspace", method = RequestMethod.GET)
	public String vspace(Model model) {
		return MAIN_PATH + "download/VSpace";
	}

	@RequestMapping(value = "/consult/detail/{id}", method = RequestMethod.GET)
	public String consultDetail(@PathVariable("id") Integer id, Model model, HttpServletRequest request) {
		try {
			CustomerSessionManager csm = new CustomerSessionManager(request.getSession());
			model.addAttribute("id", id);
			model.addAttribute("consultDetail",
					consultationService.getOneConsultationDetail(id, csm.getCustomerId(), null));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return IMPORTED_PATH + "consultDetail";
	}

	@RequestMapping(value = "/consult/customer/{uuid}", method = RequestMethod.GET)
	public String consultOfCustomer(@PathVariable("uuid") String uuid, Model model,
			HttpServletRequest request) {
		try {
			CustomerSessionManager cm = new CustomerSessionManager(request.getSession());
			SdcomEmployee employee = sdcomEmployeeService.selectByUuid(uuid);
			model.addAttribute("uuid", uuid);
			model.addAttribute("employee", employee);
			model.addAttribute("customer", sdbsCustomerService.selectByPrimaryKey(cm.getCustomerId()));
			model.addAttribute("title", "给" + employee.getName() + "留言");
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return IMPORTED_PATH + "customerConsult";
	}

	/**
	 * 微信手机号码绑定页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/binding", method = RequestMethod.GET)
	public String binding(String errorMsg, WeixinState state, Model model) {
		_Logger.info("binding:" + state);
		model.addAttribute("state", state);
		model.addAttribute("errorMsg", errorMsg);
		return MAIN_PATH + "binding";
	}

	/**
	 * 针对安卓大屏幕的形象展示页面
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@GetMapping("/android/show")
	public String getEP(Model model, HttpServletRequest request) {
		try {
			Map<String, String> map = new HashMap<>();
			SdepFile condition = new SdepFile();
			condition.setPath("andriodShow");
			condition.setSystem(1);
			List<SdepFile> files = fileService.list(condition);
			for (SdepFile sdepFile : files) {
				map.put(sdepFile.getName(), sdepFile.getUuid());
			}
			model.addAttribute("map", map);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MAIN_PATH + "androidShow";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年5月31日 功能:消息列表
	 * @param model
	 * @param condition
	 * @param pageSize
	 * @param pageNum
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "notice", method = RequestMethod.GET)
	public String customerNoticeList(Model model, CrmCustomerNotice condition, Integer pageSize, Integer pageNum,
			HttpServletRequest request) {
		try {
			Map<String, String> map = new HashMap<>();
			String path = "andriodShow";
			map.put("logo", fileService.getSystemFileUuid("logo", path));
			map.put("qrcode", fileService.getSystemFileUuid("qrcode", path));
			model.addAttribute("result", customerNoticeService.list(condition, pageNum, pageSize));
			model.addAttribute("condition", condition);
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAllAttributes(map);
		} catch (ShidaoException e) {
			e.printStackTrace();
		}
		return MAIN_PATH + "/notice";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年6月1日 功能:视频播放页面
	 * @param model
	 * @return
	 */
	@GetMapping(value = "video/show")
	public String videoShow(Model model, SdepFile condition) {
		try {
			condition.setSystem(1);
			model.addAttribute("condition", condition);
			if (!StringUtil.isNullOrEmpty(condition.getName())) {
				Map<String, String> map = new HashMap<>();
				List<SdepFile> list = fileService.list(condition);
				for (SdepFile sdepFile : list) {
					map.put(sdepFile.getExt(), "sdepFile/" + sdepFile.getId());
				}
				model.addAttribute("map", map);
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return MAIN_PATH + "/videoShow";
	}
}
