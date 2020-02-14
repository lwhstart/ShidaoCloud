package com.shidao.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.ClubType;
import com.shidao.enums.MRPrescriptionCategory;
import com.shidao.enums.MedicalRecordStatus;
import com.shidao.enums.PrintingPageOptionSettingType;
import com.shidao.enums.SalesOrderCategory;
import com.shidao.model.SdbsCustomer;
import com.shidao.model.SdbsProduct;
import com.shidao.model.SdbsSalesOrder;
import com.shidao.model.SdbsSalesOrderItem;
import com.shidao.model.SdcomClub;
import com.shidao.model.TcmMedicalRecord;
import com.shidao.model.TcmMedicalRecordPrescription;
import com.shidao.model.TcmMedicalRecordPrescriptionItem;
import com.shidao.model.WmsInventory;
import com.shidao.model.WmsPurchaseOrder;
import com.shidao.model.WmsPurchaseOrderDetail;
import com.shidao.model.WmsPurchaseRequestOrderDetail;
import com.shidao.service.MzMaizhenyiHistoryService;
import com.shidao.service.SdbsCustomerMembershipService;
import com.shidao.service.SdbsCustomerService;
import com.shidao.service.SdbsCustomerSourceService;
import com.shidao.service.SdbsPayOrderItemService;
import com.shidao.service.SdbsProductService;
import com.shidao.service.SdbsSalesOrderItemService;
import com.shidao.service.SdbsSalesOrderService;
import com.shidao.service.SdcomClubService;
import com.shidao.service.SdcomEmployeeService;
import com.shidao.service.TcmMedicalRecordService;
import com.shidao.service.WmsDeliveryVoucherService;
import com.shidao.service.WmsInventoryService;
import com.shidao.service.WmsInventorySnapshotService;
import com.shidao.service.WmsPurchaseOrderService;
import com.shidao.service.WmsPurchaseRequestOrderDetailService;
import com.shidao.service.WmsPurchaseRequestOrderService;
import com.shidao.setting.ClubSetting;
import com.shidao.setting.PrescriptionPrintingSetting;
import com.shidao.setting.PrintingPageOptionSetting;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;
import com.shidao.util.Statistics;
import com.shidao.util.StringUtil;
import com.shidao.vo.Confirmation4Salesorders;
import com.shidao.vo.XMKCustomerVO;

@Controller
@RequestMapping(value = "/printing")
public class PrintingController extends BaseController {
	private static final String PRINTING_PATH = "/display/printing/";

	@Autowired
	private TcmMedicalRecordService medicalRecordService;

	@Autowired
	private SdbsSalesOrderItemService salesOrderItemService;

	@Autowired
	private SdbsSalesOrderService salesOrderService;

	@Autowired
	private MzMaizhenyiHistoryService historyService;

	@Autowired
	private SdcomEmployeeService employeeService;

	@Autowired
	private WmsPurchaseOrderService purchaseOrderService;

	@Autowired
	private SdbsProductService productService;

	@Autowired
	private WmsInventoryService inventoryService;

	@Autowired
	private WmsDeliveryVoucherService deliveryVoucherService;

	@Autowired
	private SdcomClubService clubService;

	@Autowired
	private SdbsCustomerService customerService;

	@Autowired
	private SdbsCustomerMembershipService customerMembershipService;

	@Autowired
	private WmsInventorySnapshotService inventorySnapshotService;
	
	@Autowired
	private SdbsPayOrderItemService payOrderItemService;
	
	@Autowired
	private WmsPurchaseRequestOrderService purchaseRequestOrderService;
	
	@Autowired
	private WmsPurchaseRequestOrderDetailService purchaseRequestOrderDetailService;
	
	@Autowired
	private SdbsCustomerSourceService customerSourceService;
// 输入病历页面
	@RequestMapping(value = "/template/medicalRecord/{medicalRecordId}", method = RequestMethod.GET)
	public String printMedicalRecord(Model model, @PathVariable Integer medicalRecordId,HttpServletRequest request) {
		try {
			TcmMedicalRecord record = medicalRecordService.selectByPrimaryKey(medicalRecordId);
			List<TcmMedicalRecordPrescription> prescriptions = record.getPrescriptions();
			Map<MRPrescriptionCategory, Object> notMedicinePrescription = new LinkedHashMap<>();
			for (MRPrescriptionCategory category : MRPrescriptionCategory.notMedicine) {
				notMedicinePrescription.put(category, prescriptions.stream().filter(a->a.getCategory()==category).findFirst().orElse(null));
				prescriptions.removeIf(a->a.getCategory() == category);
			}
			model.addAttribute("notMedicinePrescription", notMedicinePrescription);
			model.addAttribute("medicalRecord", record);
			if (record.getClub().getType() == ClubType.HZYCooperation) {
				return PRINTING_PATH + "medicalRecordTemplate4HZY";
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}

		return PRINTING_PATH + "medicalRecordTemplate";
	}
	
	@RequestMapping(value = "/template/salesOrder/{salesOrderId}", method = RequestMethod.GET)
	public String printSalesOrder(HttpServletRequest request, Model model, @PathVariable Integer salesOrderId) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			SdbsSalesOrder salesOrder = salesOrderService.selectByPrimaryKey(salesOrderId);
			model.addAttribute("salesOrder", salesOrder);
			model.addAttribute("sumTotal", salesOrderItemService.getSumTotalBysalesOrderId(salesOrderId));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}

		return PRINTING_PATH + "financeOrderOfMedicalRecordTemplate";
	}

	@ResponseBody
	@RequestMapping(value = "/json/medicalRecord/{medicalRecordId}", method = RequestMethod.GET)
	public JSONObject printMedicalRecordJson(Model model, @PathVariable Integer medicalRecordId) {
		try {
			TcmMedicalRecord record = medicalRecordService.selectByPrimaryKey(medicalRecordId);
			return JsonUtil.succeedJson(record);

		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 跳转模板
	 * @param model
	 * @param templateName 末班名称，是个枚举
	 * @param request
	 * @return
	 * @throws ShidaoException
	 */
	
	@RequestMapping(value = "/framework", method = RequestMethod.GET)
	public String print(Model model, PrintingPageOptionSettingType templateName, HttpServletRequest request) throws ShidaoException {
		//门店logo和门店标语
		EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
		model.addAttribute("club", clubService.selectByPrimaryKey(sm.getClubId()));
		
//		model.addAllAttributes(PrintingPageOptionSetting.getSetting(templateName, esManager.getPositionEnum()).getSaveAsMap());
		model.addAttribute("option", PrintingPageOptionSetting.getSetting(templateName, sm.getPositionEnum()));
		return PRINTING_PATH + "framework";
	}

	// 脉诊报告页面
	@RequestMapping(value = "/template/pulseReport/{uuid}", method = RequestMethod.GET)
	public String pulseReport(Model model, String category, @PathVariable(value = "uuid") String uuid,
			HttpServletRequest request) {
		try {
			model.addAttribute("result", historyService.generateMaizhenyiResultReport(uuid, category));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return PRINTING_PATH + "pulseReportTemplate";
	}

	// 采购单页面
	@RequestMapping(value = "template/purchaseDetail/{purchaseOrderId}", method = RequestMethod.GET)
	public String purchaseDetail(Model model, @PathVariable(value = "purchaseOrderId") Integer purchaseOrderId) {
		try {
			// 表名
			String tableName = null;
			String vendorName = "";
			WmsPurchaseOrder key = purchaseOrderService.selectByPrimaryKey(purchaseOrderId);
			if (key.getVendor() != null) {
				vendorName = key.getVendor().getName();
			}
			WmsPurchaseOrderDetail purchaseOrderDetail = new WmsPurchaseOrderDetail();
			purchaseOrderDetail.setPurchaseOrderId(purchaseOrderId);
			SdbsProduct product = new SdbsProduct();
//			product.setVendorId(key.getVendorId());
	//		product.setCategory(ProductCategory.Keliji);
			product.setOrderBys("c.code asc");
			product.setPurchaseOrderDetail(purchaseOrderDetail);
			List<SdbsProduct> framework = productService.getFramework(product);
			if (framework == null || framework.isEmpty()) {
				throw new ShidaoException("没有数据，不可打印");
			}
			tableName = "药品" + framework.get(0).getPackageUnit() + "装" + vendorName + "订货表格";
			model.addAttribute("purchaseDetailList", framework);
			model.addAttribute("type", "药品");
			model.addAttribute("tableName", tableName);
			model.addAttribute("purchaseCode", key.getPurchaseCode());
			// 总价
			model.addAttribute("total", key.getTotal());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return PRINTING_PATH + "purchaseDetailTemplate";
	}

	// 库存报告页面
	@RequestMapping(value = "/template/wmsInventory", method = RequestMethod.GET)
	public String wmsInventory(Model model, HttpServletRequest request) {

		try {
			Integer employeeId = (Integer) request.getSession().getAttribute("employeeId");
			if (employeeId == null)
				throw new ShidaoException("请先登录");
			model.addAttribute("employee", employeeService.selectByPrimaryKey(employeeId));
			WmsInventory inventory = new WmsInventory();
			inventory.setSpecialType(1);
			List<WmsInventory> list = inventoryService.list(inventory);
			List<WmsInventory> nList = new ArrayList<WmsInventory>();
			Float allTotal = 0f, sub, total;
			for (WmsInventory w : list) {
				sub = w.getProduct().getPurchaseGHerbPrice() * w.getAmount();
				allTotal += sub;
				total = w.getAmount() * w.getProduct().getRatioHerbal() / w.getProduct().getRatioGranule();
				w.setHerbalAmount((float) (Math.round(total * 100)) / 100);
				w.setGranuleAmount(w.getAmount());
				w.setSubtotal((float) (Math.round(sub * 100)) / 100);
				w.setAmount(w.getAmount() / w.getProduct().getAmountPerPackageUnit());
				nList.add(w);
			}
			model.addAttribute("list", nList);
			model.addAttribute("allTotal", (float) (Math.round(allTotal * 100)) / 100);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			model.addAttribute("date", df.format(new Date()));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return PRINTING_PATH + "wmsInventoryTemplate";
	}

	@RequestMapping(value = "/template/pharmacyDetails/{uuid}", method = RequestMethod.GET)
	public String pharmacyDetails(@PathVariable(value = "uuid") String uuid, Model model,
			HttpServletRequest request) {
		try {
			model.addAttribute("result", deliveryVoucherService.getPrescriptionPrintDetails(uuid));
			model.addAttribute("serverDate", new Date());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return PRINTING_PATH + "pharmacyDetailsTemplate";
	}

	// 打印处方详情页面
	@RequestMapping(value = "/template/prescriptionDetail/{medicalRecordId}", method = RequestMethod.GET)
	public String prescriptionDetail(@PathVariable(value = "medicalRecordId") Integer medicalRecordId, Model model,
			HttpServletRequest request) {
		try {
			TcmMedicalRecord medicalRecord = medicalRecordService.selectPrescriptionById(medicalRecordId);
			if (medicalRecord.getStatus() != MedicalRecordStatus.Completed) 
				throw new ShidaoException("处方正在处理中");
			EmployeeSessionManager sManager = new EmployeeSessionManager(request.getSession());
			SdcomClub club = clubService.selectByPrimaryKey(sManager.getClubId());
			model.addAttribute("clubName", club.getName());
			model.addAttribute("medicalRecord", medicalRecord);
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return PRINTING_PATH + "prescriptionDetailTemplate";
	}

	// 打印处方详情页面，满足医保标准
	@RequestMapping(value = "/template/prescriptionDetail4Yibao/{uuid}", method = RequestMethod.GET)
	public String prescriptionDetail4Yibao(@PathVariable(value = "uuid") String uuid, Model model,
			HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			TcmMedicalRecord medicalRecord = medicalRecordService.selectPrescription4PrintByUuid(uuid);
			if (medicalRecord.getStatus() != MedicalRecordStatus.Completed) 
				throw new ShidaoException("处方尚未完成，不可查看处方");

			//分解病历和处方结构，新结构为：{处方：[药品列表1，药品列表2]}，
			// 逻辑： 把处方对应的药品列表进行拆分，每一个子列表的数量，都小于等于每页的指定数量。
			PrescriptionPrintingSetting printingSetting = PrescriptionPrintingSetting.getSetting(medicalRecord.getClub().getId());
			Map<TcmMedicalRecordPrescription,List< List<TcmMedicalRecordPrescriptionItem>>> map = new LinkedHashMap<>();
			for(TcmMedicalRecordPrescription prescription : medicalRecord.getPrescriptions()) {
				List<List<TcmMedicalRecordPrescriptionItem>> itemsList = new ArrayList<>();
				while(!prescription.getItems().isEmpty()) {
					Integer size= prescription.getItems().size();
					Integer pageSize = printingSetting.getItemsPerPage() >size ? size : printingSetting.getItemsPerPage() ;
					List<TcmMedicalRecordPrescriptionItem> medicines = new ArrayList<>();
					medicines.addAll(prescription.getItems().subList(0, pageSize));
					itemsList.add(medicines);
					prescription.getItems().subList(0, pageSize).clear();
				}
				map.put(prescription, itemsList);
			}
			model.addAttribute("clubName", medicalRecord.getClub().getName());
			model.addAttribute("medicalRecord", medicalRecord);
			model.addAttribute("map", map);
			model.addAttribute("clubSetting", ClubSetting.getSetting(medicalRecord.getClub().getId()));
			model.addAttribute("printingSetting", printingSetting);
			if (medicalRecord.getClub().getType() == ClubType.HZYCooperation) {
				return PRINTING_PATH + "prescriptionDetailTemplate4Yibao4HZY";
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return PRINTING_PATH + "prescriptionDetailTemplate4Yibao";
	}
	
	// 打印处方详情页面，满足医保标准
		@RequestMapping(value = "/template/prescriptionDetail4MedicineOnly/{uuid}", method = RequestMethod.GET)
		public String prescriptionDetailMedicineOnly(@PathVariable(value = "uuid") String uuid, Model model,
				HttpServletRequest request) {
			try {
				EmployeeSessionManager.checkLogin(request.getSession());
				TcmMedicalRecord medicalRecord = medicalRecordService.selectPrescription4PrintByUuid(uuid);
				if (medicalRecord.getStatus() != MedicalRecordStatus.Completed) 
					throw new ShidaoException("处方尚未完成，不可查看处方");

				//分解病历和处方结构，新结构为：{处方：[药品列表1，药品列表2]}，
				// 逻辑： 把处方对应的药品列表进行拆分，每一个子列表的数量，都小于等于每页的指定数量。
				Integer itemsPerPage = 44; //葆元堂修改
				Map<TcmMedicalRecordPrescription,List< List<TcmMedicalRecordPrescriptionItem>>> map = new LinkedHashMap<>();
				for(TcmMedicalRecordPrescription prescription : medicalRecord.getPrescriptions()) {
					List<List<TcmMedicalRecordPrescriptionItem>> itemsList = new ArrayList<>();
					while(!prescription.getItems().isEmpty()) {
						Integer size= prescription.getItems().size();
						Integer pageSize = itemsPerPage>size ? size : itemsPerPage;
						List<TcmMedicalRecordPrescriptionItem> medicines = new ArrayList<>();
						medicines.addAll(prescription.getItems().subList(0, pageSize));
						itemsList.add(medicines);
						prescription.getItems().subList(0, pageSize).clear();
					}
					map.put(prescription, itemsList);
				}
				model.addAttribute("map", map);
				model.addAttribute("medicalRecord", medicalRecord);
			} catch (Exception e) {
				model.addAttribute("errorMsg", e.getMessage());
			}
			return PRINTING_PATH + "prescriptionDetailTemplate4MedicineOnly";
		}

	// 根据日期范围、供应商、草药编码(开始编码、结束编码)，查询产品的期初、入库、出库、期末信息
	@RequestMapping(value = "/template/financeInventory", method = RequestMethod.GET)
	public String financeInventory(Model model, SdbsProduct condition, Date startDate, Date endDate,
			String fieldCondition, String startCode, String endCode) {
		try {
			if (startDate == null || endDate == null)
				throw new ShidaoException("请选择日期");
			condition.setOrderBys("m.code ASC");

			List<SdbsProduct> list = productService.list(condition);
			model.addAttribute("list", productService.selectProductListByDate(list, startDate, endDate));
			model.addAttribute("allStartTotal", inventorySnapshotService.getStartEndTotal(condition.getVendorName(),
					fieldCondition, startCode, endCode, startDate));
			model.addAttribute("allEndTotal", inventorySnapshotService.getStartEndTotal(condition.getVendorName(),
					fieldCondition, startCode, endCode, endDate));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return PRINTING_PATH + "financeInventoryTemplate";
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月7日
	 * 功能:多个订单打印
	 * @param request
	 * @param model
	 * @param salesOrderId
	 * @return
	 */
	@RequestMapping(value = "/template/printSalesorders4Customer", method = RequestMethod.GET)
	public String payDemandNotes(HttpServletRequest request, Model model, String salesOrderIds) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			SdbsSalesOrder salesOrder = new SdbsSalesOrder();
			salesOrder.setSalesOrderIds(salesOrderIds);
			salesOrder.setPayed(false);
			List<SdbsSalesOrder> list = salesOrderService.selectByIdsAndCategory(salesOrder);
			if (list != null && !list.isEmpty())
				throw new ShidaoException("还没有结算，不能打印！");
			salesOrder.setPayed(null);
			List<SdbsSalesOrder> orders = salesOrderService.selectByIdsAndCategory(salesOrder);
			model.addAttribute("printfName",orders.stream().map(a->a.getCategory().getText()).collect(Collectors.joining(","))); 
			Integer salesOrderId = Integer.parseInt(salesOrderIds.split(",")[0]);
			SdbsSalesOrder condition = new SdbsSalesOrder();
			condition.addCategoriesParams(SalesOrderCategory.medicine);
			condition.setSalesOrderIds(salesOrderIds);
			String medicineIds = salesOrderService.getSalesOrderIds(condition);
			if (medicineIds != null)
				salesOrderId = Integer.parseInt(medicineIds.split(",")[0]);
			SdbsSalesOrder key = salesOrderService.selectByPrimaryKey(salesOrderId);
			SdbsCustomer customer = customerService.selectByPrimaryKey(key.getCustomerId(),sm.getClubId());
			//项目卡
			XMKCustomerVO vo = customerService.listXMKById(customer.getId(), sm.getClubId());
			if (vo != null)
				model.addAttribute("XMK", vo);
			model.addAttribute("medicines", salesOrderItemService.listMedicineTotal(salesOrderIds));
			
			// 产品总原价
			model.addAllAttributes(salesOrderItemService.getProductSumPrice(salesOrderIds));
			//产品折后总价
			model.addAllAttributes(salesOrderItemService.getProductSumTotal(salesOrderIds));
			// 得到该订单里理疗列表自营产品的列表
			model.addAllAttributes(salesOrderItemService.listProductBySalesOrderId(salesOrderIds));
			// 客户信息
			model.addAttribute("customer", customer);
			// 操作员
			model.addAttribute("operator", employeeService.selectByPrimaryKey(sm.getEmployeeId()).getName());
			if(key.getAppointment() != null && key.getAppointment().getAssistant() != null)
				model.addAttribute("assistant", key.getAppointment().getAssistant());
			
			// 该顾客消费前后卡项的余额
			model.addAttribute("payInfo", customerMembershipService.listServiceCardInfo(null,salesOrderIds));
			//项目卡和积分卡
			model.addAllAttributes(payOrderItemService.getXMKAndJFKTotal(salesOrderIds));
			/*//代付人
			model.addAttribute("daiFuCustomer", payOrderItemService.daifuCustomer(salesOrderIds));*/
			//支付后和支付前的余额
			model.addAttribute("payAfter", payOrderItemService.listPayMethodAndAmount(null, salesOrderIds));
			model.addAttribute("total", salesOrderService.calculateAmount(salesOrderIds));
			model.addAttribute("clubName", sm.getClubName());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return PRINTING_PATH + "printSalesorders4CustomerTemplate";
	}
	
	/**
	 * 
	 * @param request
	 * @param model
	 * @param salesOrderIds
	 * @return
	 */
	@RequestMapping(value = "/template/confirmationSalesorders4Customer", method = RequestMethod.GET)
	public String confirmationSalesorders4Customer(HttpServletRequest request, Model model,@RequestParam String salesOrderIds) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			
			SdbsSalesOrder condition = new SdbsSalesOrder();
			condition.addListSalesOrderIdParams(salesOrderIds);
			condition.setClubId(sm.getClubId());
			List<Confirmation4Salesorders> medicines = salesOrderService.listMedicine4Confirmation(condition);
			List<Confirmation4Salesorders> products = salesOrderService.listProduct4Confirmation(condition);
			List<Confirmation4Salesorders> cards = salesOrderService.listCardRelated4Confirmation(condition);

			if(medicines.size()+products.size() +cards.size()== 0) {
				throw new ShidaoException("没有合法的编号");
			}
			model.addAttribute("medicines", medicines);
			model.addAttribute("products", products);
			model.addAttribute("cards", cards);
			
			Integer customerId =(medicines.size()> 0 ? medicines :products).get(0).getCustomerId();			
			SdbsCustomer customer = customerService.selectByPrimaryKey(customerId,sm.getClubId());
			model.addAttribute("customer", customer);		
			model.addAttribute("summary", salesOrderService.getSalesOrdersSummary(condition));
			model.addAttribute("sources", customerSourceService.listSources(sm.getClubId()));
				
			model.addAttribute("clubName", sm.getClubName());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return PRINTING_PATH + "confirmationSalesorders4CustomerTemplate";
	}
	
	@GetMapping("template/prescriptionPrice/{uuid}")
	public String prescriptionPrice(Model model,@PathVariable("uuid")String uuid,HttpServletRequest request){
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			TcmMedicalRecord medicalRecord = medicalRecordService.selectSimpleByUuid(uuid);
			if (medicalRecord.getStatus() != MedicalRecordStatus.Completed) 
				throw new ShidaoException("处方尚未完成，不可查看处方");
			List<SdbsSalesOrder> salesOrders = salesOrderService.getPrescriptionPrice(uuid);
			if (salesOrders.size()<=0)
				throw new ShidaoException("没有要打印的药");
			Integer clubId = salesOrders.get(0).getClubId();
			//分解病历和处方结构，新结构为：{处方：[药品列表1，药品列表2]}，
			// 逻辑： 把处方对应的药品列表进行拆分，每一个子列表的数量，都小于等于每页的指定数量。
			PrescriptionPrintingSetting printingSetting = PrescriptionPrintingSetting.getSetting(clubId);
			Map<SdbsSalesOrder,List< List<SdbsSalesOrderItem>>> map = new LinkedHashMap<>();
			for(SdbsSalesOrder salesOrder : salesOrders) {
				List<List<SdbsSalesOrderItem>> itemsList = new ArrayList<>();
				while(!salesOrder.getItems().isEmpty()) {
					Integer size= salesOrder.getItems().size();
					Integer pageSize = printingSetting.getItemsPerPage() >size ? size : printingSetting.getItemsPerPage() ;
					List<SdbsSalesOrderItem> medicines = new ArrayList<>();
					medicines.addAll(salesOrder.getItems().subList(0, pageSize));
					itemsList.add(medicines);
					salesOrder.getItems().subList(0, pageSize).clear();
				}
				map.put(salesOrder, itemsList);
			}
			String clinicalDiagnosis = "";
			if(!StringUtil.isNullOrEmpty(medicalRecord.getFubingDiagnose()))
				clinicalDiagnosis = medicalRecord.getFubingDiagnose();
			else if (!StringUtil.isNullOrEmpty(medicalRecord.getWmDiagnose())) {
				clinicalDiagnosis = medicalRecord.getWmDiagnose();
			}
			model.addAttribute("map", map);
			model.addAttribute("clubSetting", ClubSetting.getSetting(clubId));
			model.addAttribute("printingSetting", printingSetting);
			model.addAttribute("clubName", clubService.selectByPrimaryKey(clubId).getName());
			model.addAttribute("customer", customerService.selectByPrimaryKey(medicalRecord.getCustomerId()));
			model.addAttribute("clinicalDiagnosis", clinicalDiagnosis);
			model.addAttribute("medicalRecord", medicalRecord);
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return PRINTING_PATH+"prescriptionPriceTemplate";
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月28日<br>
	 * 功能:打印采购申请单
	 * @param model
	 * @param uuid
	 * @param request
	 * @return
	 */
	@GetMapping("template/wmsPurchaseRequestOrderDetail/{uuid}")
	public String wmsPurchaseRequestOrderDetail(Model model,@PathVariable("uuid")String uuid,HttpServletRequest request){
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			WmsPurchaseRequestOrderDetail condition = new WmsPurchaseRequestOrderDetail();
			condition.addUuid(uuid);
			condition.addIsNotZero(true);
			condition.setOrderBys("a.ACTUAL_VENDOR_ID");
			model.addAttribute("list", purchaseRequestOrderDetailService.list(condition));
			model.addAttribute("condition", condition);
			model.addAttribute("purchaseRequestOrder", purchaseRequestOrderService.selectByUuid(uuid));
			List<Map<String, Object>> vendorInfo = purchaseRequestOrderDetailService.getVendorTotalAndDifference(uuid);
			model.addAttribute("vendorInfo", vendorInfo);
			model.addAttribute("summary", Statistics.summarize(vendorInfo));
			model.addAttribute("size", vendorInfo.stream().filter(a->Float.parseFloat(a.get("total").toString()) !=0).count());
			model.addAttribute("requestURI", request.getRequestURI());
		} catch (ShidaoException e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return PRINTING_PATH+"wmsPurchaseRequestOrderDetailTemplate";
	}
	
	@RequestMapping(value = "/template/salesordersConfirmation4Customer", method = RequestMethod.GET)
	public String salesordersConfirmation4Customer(HttpServletRequest request, Model model, String salesOrderIds) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			SdbsSalesOrder salesOrder = new SdbsSalesOrder();
			salesOrder.setSalesOrderIds(salesOrderIds);
			salesOrder.setPayed(false);
			List<SdbsSalesOrder> list = salesOrderService.selectByIdsAndCategory(salesOrder);
			if (list != null && !list.isEmpty())
				throw new ShidaoException("还没有结算，不能打印！");
			salesOrder.setPayed(null);
			List<SdbsSalesOrder> orders = salesOrderService.selectByIdsAndCategory(salesOrder);
			model.addAttribute("printfName",orders.stream().map(a->a.getCategory().getText()).collect(Collectors.joining(","))); 
			Integer salesOrderId = Integer.parseInt(salesOrderIds.split(",")[0]);
			SdbsSalesOrder condition = new SdbsSalesOrder();
			condition.addCategoriesParams(SalesOrderCategory.medicine);
			condition.setSalesOrderIds(salesOrderIds);
			String medicineIds = salesOrderService.getSalesOrderIds(condition);
			if (medicineIds != null)
				salesOrderId = Integer.parseInt(medicineIds.split(",")[0]);
			SdbsSalesOrder key = salesOrderService.selectByPrimaryKey(salesOrderId);
			SdbsCustomer customer = customerService.selectByPrimaryKey(key.getCustomerId(),sm.getClubId());
			//项目卡
			XMKCustomerVO vo = customerService.listXMKById(customer.getId(), sm.getClubId());
			if (vo != null)
				model.addAttribute("XMK", vo);
			model.addAttribute("medicines", salesOrderItemService.listMedicineTotal(salesOrderIds));
			
			// 产品总原价
			model.addAllAttributes(salesOrderItemService.getProductSumPrice(salesOrderIds));
			//产品折后总价
			model.addAllAttributes(salesOrderItemService.getProductSumTotal(salesOrderIds));
			// 得到该订单里理疗列表自营产品的列表
			model.addAllAttributes(salesOrderItemService.listProductBySalesOrderId(salesOrderIds));
			// 客户信息
			model.addAttribute("customer", customer);
			// 操作员
			model.addAttribute("operator", employeeService.selectByPrimaryKey(sm.getEmployeeId()).getName());
			if(key.getAppointment() != null && key.getAppointment().getAssistant() != null)
				model.addAttribute("assistant", key.getAppointment().getAssistant());
			
			// 该顾客消费前后卡项的余额
			model.addAttribute("payInfo", customerMembershipService.listServiceCardInfo(null,salesOrderIds));
			//项目卡和积分卡
			model.addAllAttributes(payOrderItemService.getXMKAndJFKTotal(salesOrderIds));
			/*//代付人
			model.addAttribute("daiFuCustomer", payOrderItemService.daifuCustomer(salesOrderIds));*/
			//支付后和支付前的余额
			model.addAttribute("payAfter", payOrderItemService.listPayMethodAndAmount(null, salesOrderIds));
			model.addAttribute("total", salesOrderService.calculateAmount(salesOrderIds));
			model.addAttribute("clubName", sm.getClubName());
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}
		return PRINTING_PATH + "salesordersConfirmation4CustomerTemplate";
	}
	
}
