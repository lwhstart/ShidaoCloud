package com.shidao.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.ProductCategory;
import com.shidao.model.SdbsProduct;
import com.shidao.model.TcmMedicine;
import com.shidao.service.SdbsProductService;
import com.shidao.service.SdbsSalesOrderItemService;
import com.shidao.service.SdbsVendorService;
import com.shidao.service.SdcomClubService;
import com.shidao.service.TcmMedicineService;
import com.shidao.util.ChineseCharToEn;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;
import com.shidao.util.StringUtil;

@RestController
@RequestMapping(value = "/sdbsProduct")
public class SdbsProductController {

	@Autowired
	private SdbsProductService sdbsProductService;

	@Autowired
	private SdbsVendorService vendorService;

	@Autowired
	private SdbsSalesOrderItemService salesOrderItemService;

	@Autowired
	private TcmMedicineService medicineService;
	
	@Autowired
	private SdcomClubService clubService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject listLiliao(SdbsProduct sdbsProduct, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			if (sdbsProduct.getClubId() == null)
				sdbsProduct.setClubId(sm.getClubId());
			return JsonUtil.succeedJson(sdbsProductService.list(sdbsProduct));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/list/type", method = RequestMethod.GET)
	public JSONObject list(SdbsProduct sdbsProduct) {
		try {
			// sdbsProduct.setCategory(ProductCategory.Liliao);
			return JsonUtil.succeedJson(sdbsProductService.list(sdbsProduct));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/allList", method = RequestMethod.GET)
	public JSONObject productList(SdbsProduct sdbsProduct) {
		try {
			Map<String, Object> map = new HashMap<>();
			// sdbsProduct.setCategory(ProductCategory.Liliao);
			List<String> productType = sdbsProductService.selectProductType(sdbsProduct);
			for (String type : productType) {
				sdbsProduct.setType(type);
				map.put(type, sdbsProductService.list(sdbsProduct));
			}
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public JSONObject detail(@PathVariable Integer id) {
		try {
			return JsonUtil.succeedJson(sdbsProductService.selectByPrimaryKey(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(SdbsProduct sdbsProduct,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			sdbsProduct.setLastModifiedId(sm.getEmployeeId());
			sdbsProductService.updateByPrimaryKeySelective(sdbsProduct);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月1日 修改功能:所有的产品加clubID
	 * @param product
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(SdbsProduct product, HttpServletRequest request) {
		try {

			EmployeeSessionManager employeeSessionManager = new EmployeeSessionManager(request.getSession());
			product.setLastModifiedId(employeeSessionManager.getEmployeeId());
			if (product.getVendorId() != null && StringUtil.isNullOrEmpty(product.getVendorName())) {
				// 查询供应商名字
				product.setVendorName(vendorService.selectByPrimaryKey(product.getVendorId()).getName());
			}
			if(product.getCategory().isNeedClubId()) {				
				product.setClubId(employeeSessionManager.getClubId());
			}
			//草药默认的amountPerPackageUnit=1
			if (product.getCategory()==ProductCategory.Caoyao && product.getAmountPerPackageUnit() == null) {
				product.setAmountPerPackageUnit(1);
			}
			sdbsProductService.insertSelective(product);
			JSONObject json = JsonUtil.succeedJson(product.getId());
			json.put("uuid", sdbsProductService.selectByPrimaryKey(product.getId()).getUuid());
			return json;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@PostMapping( "/medicine/insert")
	public JSONObject medicineInsert(SdbsProduct sdbsProduct,HttpServletRequest request) {
		try {			
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			sdbsProduct.setLastModifiedId(sm.getEmployeeId());
			return JsonUtil.succeedJson(sdbsProductService.insertMedicine(sdbsProduct).getUuid());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/medicine/update", method = RequestMethod.POST)
	public JSONObject medicineUpdate(SdbsProduct sdbsProduct,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			sdbsProduct.setLastModifiedId(sm.getEmployeeId());
			sdbsProductService.updateMedicine(sdbsProduct);
			return JsonUtil.succeedJson(sdbsProduct.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(Integer id) {
		try {
			if (salesOrderItemService.getPaidProductAmount(id) > 0)
				throw new ShidaoException("该产品顾客使用过，不能删除");
			sdbsProductService.deleteByPrimaryKey(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/deleteLiliao", method = RequestMethod.POST)
	public JSONObject deleteLiliao(Integer id) {
		try {
			sdbsProductService.deleteLiliao(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 快速搜索某仓库对应的商品列表。
	 * 
	 * @param nameInitial
	 *            商品首字母
	 * @param warehouseId
	 *            仓库编号
	 * @param category
	 *            商品类型
	 * @return
	 */
	@RequestMapping(value = "/quickSearch", method = RequestMethod.GET)
	public JSONObject quickSearch(String nameInitial, ProductCategory category, HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(sdbsProductService.quickSearch(nameInitial, 
					clubService.getWarehouseIdByCategoryAndClubId(sm.getClubId(), category), category));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年5月23日 功能:得product和medicine表的拼音首字母
	 * @return
	 */
	@PostMapping("/setFirstSpell")
	public JSONObject setFirstSpell(SdbsProduct product) {
		try {
			List<SdbsProduct> products = sdbsProductService.list(product);
			for (SdbsProduct sdbsProduct : products) {
				String nameintial = ChineseCharToEn.getFirstSpell(sdbsProduct.getName());
				if (StringUtil.isNullOrEmpty(nameintial))
					break;
				SdbsProduct condition = new SdbsProduct();
				condition.setId(sdbsProduct.getId());
				condition.setNameInitial(nameintial);
				sdbsProductService.updateByPrimaryKeySelective(condition);
			}
			List<TcmMedicine> medicines = medicineService.list();
			for (TcmMedicine tcmMedicine : medicines) {
				String nameintial = ChineseCharToEn.getFirstSpell(tcmMedicine.getName());
				if (StringUtil.isNullOrEmpty(nameintial))
					break;
				TcmMedicine condition = new TcmMedicine();
				condition.setId(tcmMedicine.getId());
				condition.setNameInitial(nameintial);
				medicineService.updateByPrimaryKeySelective(condition);
			}
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
