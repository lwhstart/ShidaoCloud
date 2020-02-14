package com.shidao.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.shidao.enums.PriceStrategy;
import com.shidao.enums.ProductCategory;
import com.shidao.model.DatePeriodParameter;
import com.shidao.model.SdbsMedicinePrice;
import com.shidao.model.SdbsProduct;
import com.shidao.model.SdbsVendor;
import com.shidao.model.TcmMedicine;
import com.shidao.model.TcmMedicineAlias;
import com.shidao.model.WmsBorrowMedicineOrder;
import com.shidao.model.WmsInventory;
import com.shidao.model.WmsInventoryWarningSetting;
import com.shidao.model.WmsPurchaseRequestOrder;
import com.shidao.model.WmsPurchaseRequestOrderDetail;
import com.shidao.model.WmsWarehouse;
import com.shidao.model.WmsWarehouseVoucher.StatusType;
import com.shidao.service.SdbsProductService;
import com.shidao.service.SdbsVendorService;
import com.shidao.service.SdcomClubService;
import com.shidao.service.StatisticsService;
import com.shidao.service.TcmMedicineAliasService;
import com.shidao.service.TcmMedicineService;
import com.shidao.service.WmsBorrowMedicineOrderService;
import com.shidao.service.WmsInventoryService;
import com.shidao.service.WmsInventoryWarningSettingService;
import com.shidao.service.WmsPurchaseRequestOrderDetailService;
import com.shidao.service.WmsPurchaseRequestOrderService;
import com.shidao.service.WmsWarehouseService;
import com.shidao.setting.ClubSetting;
import com.shidao.setting.UnitsSetting;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.ListResult;
import com.shidao.util.PermissionEnum;
import com.shidao.util.ShidaoException;
import com.shidao.util.Statistics;

/**
 * 药房相关的功能接口
 */
@Controller
@RequestMapping("/workspace/warehouse")
public class WorkspaceWarehouseController extends BaseController {

	private static final String WORKSPACEWAREHOUSE_PATH = "/display/workspace/warehouse/";

	@Autowired
	private SdbsVendorService sdbsVendorService;

	@Autowired
	private SdbsProductService sdbsProductService;

	@Autowired
	private TcmMedicineService medicineService;

	@Autowired
	private WmsInventoryService inventoryService;
	
	@Autowired
	private WmsWarehouseService warehouseService;

	@Autowired
	private StatisticsService statisticsService;
	
	@Autowired
	private SdcomClubService clubService;
	
	@Autowired
	private TcmMedicineAliasService medicineAliasService;
	
	@Autowired
	private WmsInventoryWarningSettingService warningSettingService;
	
	@Autowired
	private WmsBorrowMedicineOrderService borrowMedicineOrderService;
	
	@Autowired
	private WmsPurchaseRequestOrderService purchaseRequestOrderService;
	
	@Autowired
	private WmsPurchaseRequestOrderDetailService purchaseRequestOrderDetailService;
	
	// 创建指定类型的
	@RequestMapping(value = "/product/create/{category}", method = RequestMethod.GET)
	public String createSelfProduct(HttpServletRequest request, Model model,
			@PathVariable(name = "category") ProductCategory category) {
		try {
			// 供应商列表
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("category", category);
			model.addAttribute("vendorList", sdbsVendorService.listByWarehouse(sm.getWarehouseId()));
			model.addAttribute("packageUnits", UnitsSetting.getSetting(category).getPackageUnit());
			model.addAttribute("units", UnitsSetting.getSetting(category).getChargeUnit());
			switch (category) {
			case Keliji:
				model.addAttribute("medicineList", medicineService.list());
				model.addAttribute("priceStrategy", ClubSetting.getSetting(sm.getClubId()).getPriceStrategy4Keliji());
				return WORKSPACEWAREHOUSE_PATH + "drugDetail";
			case Caoyao:
			case Gaobuxiliao:
			case ZCY:
				return WORKSPACEWAREHOUSE_PATH + "/selfProductEdit";
			default:
				break;
			}
			} catch (ShidaoException e) {
				model.addAttribute("errorMsg", e);
			}
		return WORKSPACEWAREHOUSE_PATH + "/selfProductEdit";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年5月24日 功能:草药管理（抽象）
	 * @param model
	 * @param condition
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/tcmName/list")
	public String medicineList(Model model, TcmMedicine condition, Integer pageNum, Integer pageSize,
			HttpServletRequest request) {
		try {
			if (condition.getEnabled() == null)
				condition.setEnabled(1);
			model.addAttribute("result", medicineService.list(condition, pageNum, pageSize));
			model.addAttribute("requestURI",request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACEWAREHOUSE_PATH + "tcmNameList";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年5月24日 功能:草药管理详情
	 * @param model
	 * @param id
	 * @return
	 */
	@GetMapping("/tcmName/detail/{uuid}")
	public String medicineDetail(Model model, @PathVariable("uuid")String uuid,HttpServletRequest request) {
		model.addAttribute("units", UnitsSetting.getSetting(ProductCategory.Caoyao).getChargeUnit());
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("uuid", uuid);
			TcmMedicine medicine = medicineService.selectByUuid(uuid);
			TcmMedicineAlias alias = new TcmMedicineAlias();
			alias.setName(medicine.getName());
			model.addAttribute("medicine", medicine);
			model.addAttribute("aliasList", medicineAliasService.list(alias));
			WmsInventory inventory = new WmsInventory();
			inventory.setProduct(new SdbsProduct());
			inventory.getProduct().setMedicineId(medicine.getId());
			inventory.setWarehouseId(sm.getWarehouseId());
			model.addAttribute("inventories", inventoryService.listProductByInventory(inventory));
			PriceStrategy priceStrategy = ClubSetting.getSetting(sm.getClubId()).getPriceStrategy4Keliji();
			model.addAttribute("priceStrategy", priceStrategy);
			if (priceStrategy == PriceStrategy.WarehouseSamePerGHerbal)
				model.addAttribute("medicinesPrice", medicineService.getPerGherbalPrice(medicine.getId(),ProductCategory.Keliji, sm.getWarehouseId()));
			if (priceStrategy == PriceStrategy.ClubTypeSamePerGHerbal)
				model.addAttribute("medicinesPrice", medicineService.getPerGherbalPrice(medicine.getId(),ProductCategory.Keliji, sm.getClubType()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACEWAREHOUSE_PATH + "tcmNameDetail";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月19日
	 * 功能:keliji，caoyao，zcy，gaobuxiliao
	 * @param model
	 * @param category
	 * @param request
	 * @param pageNum
	 * @param pageSize
	 * @param condition
	 * @param factor
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/medicine/list")
	public String medicineListByCategory(Model model,HttpServletRequest request,
			Integer pageNum,Integer pageSize,SdbsProduct condition,String factor) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (condition.getCategory() == null) {
				condition.setCategory(ProductCategory.Keliji);
			}
			model.addAttribute("categories", ProductCategory.MedicineManagement);
			model.addAttribute("condition", condition);
			model.addAttribute("requestURI", request.getRequestURI());
			SdbsVendor vendor = new SdbsVendor();
			vendor.setWarehouseId(sm.getWarehouseId());
			vendor.setDeleted(0);
			List<SdbsVendor> vendors = sdbsVendorService.list(vendor);
			// 所有的供应商列表
			model.addAttribute("vendorList", vendors);
			model.addAttribute("priceStrategy", ClubSetting.getSetting(sm.getClubId()).getPriceStrategy4Keliji());
			switch (condition.getCategory()) {
			case Keliji:
				TcmMedicine medicine = new TcmMedicine();
				medicine.setFieldCondition(condition.getFieldCondition());
				condition.setMedicine(medicine);
				condition.setVendorsParams(vendors);
				condition.setOrderBys("m.code");
				condition.setWarehouseId(sm.getWarehouseId());
				ListResult<SdbsProduct> result = sdbsProductService.list(condition, pageNum, pageSize);
				List<SdbsProduct> list = result.getList();
				List<SdbsProduct> lastList = new ArrayList<>();
				if ("全部".equals(factor)) {
					factor = "全部";
					lastList = sdbsProductService.warning(list, factor);
					model.addAttribute("result", result);
				} else {
					condition.setType(factor);
					Map<String, Object> allDateInventory = sdbsProductService.getAllDateInventory(condition, pageNum,
							pageSize);
					lastList = (List<SdbsProduct>) allDateInventory.get("list");
					model.addAllAttributes(allDateInventory);
				}
				result.setList(lastList);
				model.addAttribute("result", result);
				model.addAttribute("choose", sdbsProductService.getChoose());
				model.addAttribute("selected", factor);
				return WORKSPACEWAREHOUSE_PATH + "drugList"; 
			case Gaobuxiliao:
			case ZCY:
				condition.setVendorsParams(vendors);
				condition.setWarehouseId(sm.getWarehouseId());
				model.addAttribute("result", sdbsProductService.list(condition, pageNum, pageSize));
				return WORKSPACEWAREHOUSE_PATH + "/selfProductList";
			case Caoyao:
				condition.setOrderBys("m.code");
				condition.setVendorsParams(vendors);
				condition.setWarehouseId(sm.getWarehouseId());
				model.addAttribute("result", sdbsProductService.list(condition, pageNum, pageSize));
				return WORKSPACEWAREHOUSE_PATH + "/selfProductList";
			default:
				break;
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return null;
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月19日
	 * 功能:keliji，caoyao，zcy，gaobuxiliao
	 * @param model
	 * @param uuid
	 * @param request
	 * @return
	 */
	@GetMapping("/medicine/detail/{uuid}")
	public String medicineEdit(Model model, @PathVariable("uuid")String uuid,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			model.addAttribute("uuid", uuid);
			SdbsProduct product = sdbsProductService.selectByUuid(uuid);
			model.addAttribute("units", UnitsSetting.getSetting(product.getCategory()).getChargeUnit());
			//判断是否有统一价格配置
			PriceStrategy priceStrategy = ClubSetting.getSetting(sm.getClubId()).getPriceStrategy4Keliji();
			model.addAttribute("priceStrategy", priceStrategy);
			if (priceStrategy != PriceStrategy.Indepent) {
				List<SdbsMedicinePrice> medicinePrices = new ArrayList<>();
				if (priceStrategy == PriceStrategy.WarehouseSamePerGHerbal)
					medicinePrices = medicineService.getPerGherbalPrice(product.getMedicineId(), product.getCategory(), sm.getWarehouseId());
				if (priceStrategy == PriceStrategy.ClubTypeSamePerGHerbal)
					medicinePrices = medicineService.getPerGherbalPrice(product.getMedicineId(), product.getCategory(), sm.getClubType());
				model.addAttribute("medicinePrices", medicinePrices.size()>0?medicinePrices.get(0):null);
			}
			switch (product.getCategory()) {
			case Keliji:
				SdbsVendor vendor = sdbsVendorService.selectByPrimaryKey(product.getVendorId());
				if (product.getPurchaseUnitPrice() != null)
					product.setActualPrice(product.getPurchaseUnitPrice() * vendor.getDiscount());
				model.addAttribute("product", product);
				// 库存列表
				WmsInventory inventory = new WmsInventory();
				inventory.setProductId(product.getId());
				inventory.setWarehouseId(sm.getWarehouseId());
				List<WmsInventory> list = inventoryService.list(inventory);
				model.addAttribute("packageUnits", UnitsSetting.getSetting(product.getCategory()).getPackageUnit());
				model.addAttribute("totalAmount", list.stream().mapToDouble(a -> a.getAmount()).sum());
				model.addAttribute("inventoryList", inventoryService.warning(list));
				return WORKSPACEWAREHOUSE_PATH + "drugDetail";
			case Caoyao:
			case Gaobuxiliao:
			case ZCY:
				model.addAttribute("product", product);
				model.addAttribute("inventories", inventoryService.listProductInventory(product.getId(),sm.getWarehouseId()));
				return WORKSPACEWAREHOUSE_PATH + "/selfProductEdit";
			default:
				break;
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACEWAREHOUSE_PATH + "selfProductEdit";
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月19日
	 * 功能:药方信息管理
	 * @param model
	 * @param request
	 * @return
	 */
	@GetMapping("/setting")
	public String setting(Model model, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Integer warehouseId = sm.getWarehouseId();
			//1.药房信息
			model.addAttribute("warehouseInfo", warehouseService.selectByPrimaryKey(warehouseId));
			//2.供应商管理
			model.addAllAttributes(statisticsService.getWarehouseVendorStatistics(warehouseId));
			//3.关联门店
			model.addAttribute("clubs", clubService.listRelationClubs(warehouseId));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACEWAREHOUSE_PATH + "setting";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月21日<br>
	 * 功能:库存警报
	 * @param model
	 * @param request
	 * @param condition
	 * @return
	 */
	@GetMapping("/inventory/warning")
	public String inventoryWarning(Model model, HttpServletRequest request,DatePeriodParameter datePeriodParameter,ProductCategory category) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (category == null) {
				category = ProductCategory.Keliji;
			}
			Map<String, Object> condition = new HashMap<>();
			condition.put("category", category);
			model.addAttribute("datePeriodParameter", datePeriodParameter);
			model.addAttribute("condition", condition);
			model.addAttribute("categories", Arrays.asList(ProductCategory.Keliji,ProductCategory.Caoyao));
			model.addAttribute("warningList",warningSettingService.listPurchaseMedicine(category, sm.getWarehouseId(), datePeriodParameter));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACEWAREHOUSE_PATH + "inventoryWarning";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月21日<br>
	 * 功能:单个库存警报药品的各个供应商的库存量
	 * @param model
	 * @param request
	 * @param medicineId
	 * @param category
	 * @return
	 */
	@GetMapping("/medicine/inventory/amount")
	public String medicineInventoryAmount(Model model, HttpServletRequest request,Integer medicineId,String medicineName,ProductCategory category) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (category == null) {
				category=ProductCategory.Keliji;
			}
			Map<String, Object> condition = new HashMap<>();
			condition.put("medicineName", medicineName);
			List<WmsInventory> list = inventoryService.listProductInventory(medicineId, sm.getWarehouseId(),category);
			model.addAttribute("inventoryList", list);
			model.addAttribute("condition", condition);
			model.addAttribute("total", list.stream().filter(a->a.getAmount()>0).mapToDouble(b->b.getAmount()).sum());
			model.addAttribute("caoyaoTotal", list.stream().filter(a->a.getAmount()>0).mapToDouble(b->Double.parseDouble(b.getCaoyaoAmount().toString())).sum());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACEWAREHOUSE_PATH + "medicineInventoryAmount";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月21日<br>
	 * 功能:库存预警设置
	 * @param model
	 * @param request
	 * @param medicineId
	 * @param category
	 * @return
	 */
	@GetMapping("/inventory/warning/setting")
	public String inventoryWarningSetting(Model model, HttpServletRequest request,String name,Boolean haveSetting,ProductCategory category) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			TcmMedicine condition = new TcmMedicine();
			if (haveSetting == null) {
				haveSetting = true;
			}
			condition.setName(name);
			condition.addHaveSettingParams(haveSetting);
			condition.setWarningSetting(new WmsInventoryWarningSetting());
			condition.getWarningSetting().setCategory(category);
			condition.getWarningSetting().setWarehouseId(sm.getWarehouseId());
			List<TcmMedicine> list = warningSettingService.listInventoryWarning(condition);
			model.addAttribute("warningSettingList", list);
			model.addAttribute("count", list.size());
			model.addAttribute("condition", condition);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACEWAREHOUSE_PATH + "inventoryWarningSetting";
	}
	
	/**
	 * 从出库列表中生成借药单并返回借药单uuid供后面查看
	 * @author 作者zzp: 
	 * @version 创建时间：2019年1月30日 下午2:46:59 
	 * @param request
	 * @param model
	 * @param deliveryVoucherId
	 * @return
	 */
	@GetMapping(value="/borrowMedicineOrder/{deliveryVoucherId}")
	public String borrowMedicineOrder(HttpServletRequest request,RedirectAttributesModelMap model,@PathVariable(value="deliveryVoucherId") Integer deliveryVoucherId) {
		String uuid = "0";
		try {
			EmployeeSessionManager eManager = new EmployeeSessionManager(request.getSession());
			uuid = borrowMedicineOrderService.insertBorrowMedicine(deliveryVoucherId,eManager.getEmployeeId());			
		} catch (Exception e) {
			model.addFlashAttribute("errorMsg", e.getMessage());
		}
		return "redirect:/workspace/warehouse/borrowMedicineOrder/detail/" + uuid;
	}
	
	/**
	 * 根据uuid查看借药单详情
	 * @author 作者zzp: 
	 * @version 创建时间：2019年1月30日 下午2:48:15 
	 * @param request
	 * @param model
	 * @param uuid
	 * @return
	 */
	@GetMapping(value="/borrowMedicineOrder/detail/{uuid}")
	public String borrowMedicineOrderByUuid(HttpServletRequest request,Model model,@PathVariable(value="uuid") String uuid) {
		try {
			EmployeeSessionManager eManager = new EmployeeSessionManager(request.getSession());
			model.addAttribute("warehouseId", eManager.getWarehouseId());
			model.addAttribute("borrowMedicineOrder", borrowMedicineOrderService.borrowMedicineOrderByUuid(uuid));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACEWAREHOUSE_PATH + "/borrowMedicineOrderDetail";
	}
	/**
	 * 借药单列表
	 * @author 作者zzp: 
	 * @version 创建时间：2019年1月30日 下午2:48:44 
	 * @param request
	 * @param model
	 * @param borrowMedicineOrder
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping(value="/{type}borrowMedicineOrder/list")
	public String borrowMedicineOrderList(HttpServletRequest request,Model model,@PathVariable(value="type") String type,WmsBorrowMedicineOrder borrowMedicineOrder,Integer pageNum,Integer pageSize) {
		try {
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession());
			if (type.equals("send")) {
				borrowMedicineOrder.setBorrowWarehouseId(sManager.getWarehouseId());	
			}else {
				borrowMedicineOrder.setLendWarehouseId(sManager.getWarehouseId());
			}
			model.addAttribute("borrowMedicineOrders", borrowMedicineOrderService.list(borrowMedicineOrder, pageNum, pageSize));
			model.addAttribute("type", type);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACEWAREHOUSE_PATH+"borrowMedicineOrderList";
	}
	
	@GetMapping(value="/model/selectWarehouseAndVendor")
	public String modelSelectVendor(Integer excludeWarehouseId,String uuid,Model model) {
		try {
			List<WmsWarehouse> warehouseAndVendor = warehouseService.selectWarehouseAndVendor();
			warehouseAndVendor.removeIf(p -> p.getId().equals(excludeWarehouseId));
			model.addAttribute("warehouseAndVendor", warehouseAndVendor);
			model.addAttribute("uuid", uuid);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACEWAREHOUSE_PATH + "modelSelectWarehouseAndVendor";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月21日<br>
	 * 功能:
	 * @param model
	 * @param request
	 * @param condition
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/wmsPurchaseRequestOrder")
	public String wmsPurchaseRequestOrder(Model model, HttpServletRequest request,WmsPurchaseRequestOrder condition,Integer pageNum,Integer pageSize) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			condition.setWarehouseId(sm.getWarehouseId());
			List<StatusType> sList = new ArrayList<>();
			if (sm.hasPermission(PermissionEnum.PurchaseRequestOrderApproval)) {
				sList.add(StatusType.Approved);
			}
			if (sm.hasPermission(PermissionEnum.PurchaseRequestOrderRequest)) {
				sList.add(StatusType.RequestApproval);
			}
			if (sm.hasPermission(PermissionEnum.PurchaseRequestOrderAll)) {
				sList.add(StatusType.Approved);
				sList.add(StatusType.RequestApproval);
				sList.add(StatusType.Created);
				sList.add(StatusType.Rejected);
			}
			if (sList == null || sList.isEmpty()) {
				throw new ShidaoException("权限不足!");
			}
			condition.addStatus(sList);
			model.addAttribute("result", purchaseRequestOrderService.list(condition, pageNum, pageSize));
			model.addAttribute("condition", condition);
			model.addAttribute("statusType", StatusType.values());
			model.addAttribute("categories", Arrays.asList(ProductCategory.Keliji,ProductCategory.Caoyao));
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACEWAREHOUSE_PATH + "wmsPurchaseRequestOrder";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月21日<br>
	 * 功能:
	 * @param model
	 * @param request
	 * @param condition
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/wmsPurchaseRequestOrderDetail/{uuid}")
	public String wmsPurchaseRequestOrderDetail(Model model, HttpServletRequest request,@PathVariable("uuid")String uuid) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			WmsPurchaseRequestOrderDetail condition = new WmsPurchaseRequestOrderDetail();
			condition.addUuid(uuid);
			model.addAttribute("list", purchaseRequestOrderDetailService.list(condition));
			model.addAttribute("condition", condition);
			model.addAttribute("purchaseRequestOrder", purchaseRequestOrderService.selectByUuid(uuid));
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACEWAREHOUSE_PATH + "wmsPurchaseRequestOrderDetail";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年2月14日<br>
	 * 功能:获取供应商的总价和差价
	 * @param model
	 * @param request
	 * @param uuid
	 * @return
	 */
	@GetMapping("/vendorTotalAndDifference/{uuid}")
	public String getVendorTotalAndDifference(Model model, HttpServletRequest request,@PathVariable("uuid")String uuid) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			List<Map<String, Object>> list = purchaseRequestOrderDetailService.getVendorTotalAndDifference(uuid);
			model.addAttribute("list", list);
			model.addAttribute("summary", Statistics.summarize(list));
			model.addAttribute("size", list.stream().filter(a->Float.parseFloat(a.get("total").toString()) !=0).count());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return WORKSPACEWAREHOUSE_PATH + "vendorTotalAndDifference";
	}
}
