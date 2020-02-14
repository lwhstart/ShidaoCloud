package com.shidao.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.shidao.enums.ClubType;
import com.shidao.enums.Position;
import com.shidao.enums.ServiceCardType;
import com.shidao.enums.SupportPayType;
import com.shidao.model.SdbsVendor;
import com.shidao.model.SdcomClub;
import com.shidao.model.SdcomEmployee;
import com.shidao.model.SdepPayCategory;
import com.shidao.model.WmsWarehouse;
import com.shidao.service.SdbsVendorService;
import com.shidao.service.SdcomClubService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.service.SdepPayCategoryService;
import com.shidao.service.StatisticsService;
import com.shidao.service.WmsWarehouseService;
import com.shidao.setting.ClubSetting;
import com.shidao.setting.GlobalSetting;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.PermissionEnum;
import com.shidao.util.ShidaoException;

@Controller
@RequestMapping(value = "/centerAdmin")
public class CenterAdminRedirectorController extends BaseController{
	private static final String CENTERADMIN_PATH = "/display/centerAdmin/";

	@Autowired
	private SdcomClubService clubService;

	@Autowired
	private WmsWarehouseService warehouseService;

	@Autowired
	private SdcomEmployeeService employeeService;
	
	@Autowired
	private SdbsVendorService sdbsVendorService;
	
	@Autowired
	private StatisticsService statisticsService;
	
	@Autowired
	private SdepPayCategoryService payCategoryService;
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月8日 功能:机构管理列表
	 * @param condition
	 * @param model
	 * @param pageNum
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/club/list", method = RequestMethod.GET)
	public String clubList(SdcomClub condition, Model model, Integer pageNum, Integer pageSize,
			HttpServletRequest request) {
		try {
			model.addAttribute("condition", condition);
			model.addAttribute("result", clubService.list(condition, pageNum, pageSize));
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return CENTERADMIN_PATH + "clubList";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年5月8日 功能:机构管理详情
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/club/detail", method = RequestMethod.GET)
	public String clubDetail(Integer id, Model model,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			SdcomClub detail = null;
			Map<ClubType, String> map = new HashMap<>();
			for (ClubType clubType : ClubType.AvailableTypes) {
				ClubSetting clubTypeSetting = ClubSetting.getSetting(clubType);
				map.put(clubType, clubTypeSetting.getDescription());
			}
			model.addAttribute("typeMap", map);
			if (id == null || id == 0 || (detail = clubService.selectByPrimaryKey(id)) == null) {
				model.addAttribute("defaultLogoUuid",GlobalSetting.Instance.getClubLogoUUID());
				model.addAttribute("detail", new SdcomClub());
				model.addAttribute("insert", true);
			} else {
				model.addAttribute("isCenterAdmin", sm.getPosition().equals(Position.CenterAdmin.getText()));
				model.addAttribute("detail", detail);
				ClubSetting clubTypeSetting = ClubSetting.getSetting(id);
				model.addAttribute("typeDescription", clubTypeSetting.getDescription());
				SdcomEmployee employee = new SdcomEmployee();
				employee.setPosition(detail.getType() == ClubType.RemoteTerminal ? "远程终端管理员" : "店长");
				employee.setClubId(id);
				model.addAttribute("adminInfo", employeeService.getAdminLoginInfo(employee));	
				model.addAttribute("insert", false);
			}

		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return CENTERADMIN_PATH + "clubDetail";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月26日
	 * 功能:门店的功能设置（服务卡和岗位）
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("/club/setting")
	public String clubSetting(Integer id, Model model) {
		try {
			//所有可用的服务卡项
			model.addAttribute("validServiceCards", ServiceCardType.validServiceCards);
			//所有可供选择的职位
			model.addAttribute("validPositions", Position.validPositions);
			
			SdepPayCategory payCategory = new SdepPayCategory();
			payCategory.setClubId(id);
			payCategory.addParameter("supportPayTypes", Arrays.asList(SupportPayType.Nonmembership,SupportPayType.Both));
			payCategory.addParameter("enabled", 1);
			List<String> validPayMethods4NonMemberships = new ArrayList<String>();
			List<String> validPayMethods4Generals = new ArrayList<String>();
			payCategoryService.list(payCategory).forEach(p->{
				p.getMethods().forEach(m->{
					switch (p.getSupportPayType()) {
					case Nonmembership:
						validPayMethods4NonMemberships.add(m.getName());
						break;
					case Both:
						validPayMethods4Generals.add(m.getName());
					    break;
					default:
						break;
					}
				});
			});
			//所有出会员卡的支付方式
			model.addAttribute("validPayMethods4NonMembership", validPayMethods4NonMemberships);
			//赠送和免单
			model.addAttribute("validPayMethods4General", validPayMethods4Generals);
			//本门店可用的服务卡项和可用的职位
			model.addAttribute("clubSetting", ClubSetting.getSetting(id));
			model.addAttribute("clubSettingId", id);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return CENTERADMIN_PATH + "clubSetting";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月22日
	 * 功能:供应商列表
	 * @param model
	 * @param condition
	 * @param pageNum
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@GetMapping("/sdbsVendor/list")
	public String vendorList(Model model, SdbsVendor condition, Integer pageNum, Integer pageSize,
			HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			// 该门店仓库
			model.addAttribute("result", sdbsVendorService.list(condition, pageNum, pageSize));
			model.addAttribute("requestURI",request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}

		return CENTERADMIN_PATH + "vendorList";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月22日
	 * 功能:供应商详情
	 * @param model
	 * @param id
	 * @return
	 */
	@GetMapping("/sdbsVendor/detail/{id}")
	public String vendorDetailById(Model model, @PathVariable(value = "id") Integer id) {
		try {
			SdbsVendor result = new SdbsVendor();
			if (id == null || id == 0) {
				model.addAttribute("vendor", result);
				return CENTERADMIN_PATH + "vendorDetail";
			}
			result = sdbsVendorService.selectByPrimaryKey(id);
			model.addAttribute("vendor", result);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return CENTERADMIN_PATH + "vendorDetail";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月22日
	 * @param model
	 * @return
	 */
	@GetMapping("/sdbsVendor/detail")
	public String vendorDetail(Model model) {
		return CENTERADMIN_PATH + "vendorDetail";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月24日
	 * 功能:仓库列表
	 * @param model
	 * @param request
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/warehouse/list")
	public String warehouseList(Model model,HttpServletRequest request,Integer pageNum,Integer pageSize) {
		try {
			WmsWarehouse condition = new WmsWarehouse();
			condition.setEnabled(1);
			model.addAttribute("result", warehouseService.list(condition, pageNum, pageSize));
			model.addAttribute("requestURI",request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return CENTERADMIN_PATH + "warehouseList";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月24日
	 * 功能:仓库对应的门店和供应商
	 * @param model
	 * @param type
	 * @param warehouseId
	 * @return
	 */
	@GetMapping("/warehouse/relation/{type}")
	public String warehouseRelation(Model model,@PathVariable String type,Integer warehouseId) {
		model.addAttribute("warehouseId", warehouseId);
		model.addAttribute("type", type);	
		try {
			switch (type) {
			case "club":
				model.addAttribute("list", warehouseService.listClubsByWarehouseId(warehouseId));
				break;
			case "vendor":
				model.addAttribute("list", warehouseService.listRelatedOrNoRelatedVendorsByWarehouseId(warehouseId));
				break;
			default:
				break;
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return CENTERADMIN_PATH + "warehouseRelation";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年8月16日
	 * 功能:各门店的短信数量统计
	 * @param datePeriodParameter
	 * @param model
	 * @param request
	 * @return
	 */
	@GetMapping("/smsMessageStatistics")
	public String smsMessageStatistics(Date date,Model model,HttpServletRequest request){
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (date == null)
				date = new Date();
			Integer clubId = null;
			if (sm.hasPermission(PermissionEnum.Club_Message_Statistics))
				clubId = sm.getClubId();
			else if (!sm.hasPermission(PermissionEnum.All_Club_Message_Statistics))
				throw new ShidaoException("没有查看短信统计的权限");
			model.addAttribute("requestURI", request.getRequestURI());
			model.addAttribute("date", date);
			model.addAllAttributes(statisticsService.getSmsMessageStatistics(date,clubId));
		} catch (Exception e) {
			model.addAttribute("errMessage", e.getMessage());
		}
		return CENTERADMIN_PATH + "smsMessageStatistics";
	}
}
