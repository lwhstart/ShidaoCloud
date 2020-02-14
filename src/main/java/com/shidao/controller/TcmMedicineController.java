package com.shidao.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.PriceStrategy;
import com.shidao.enums.ProductCategory;
import com.shidao.model.TcmMedicine;
import com.shidao.service.SdcomClubService;
import com.shidao.service.TcmMedicineService;
import com.shidao.setting.ClubSetting;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value = "/tcmMedicine")
public class TcmMedicineController extends BaseController {

	@Autowired
	private TcmMedicineService tMedicineService;
	
	@Autowired
	private SdcomClubService clubService;

	/**
	 * 以首字母来搜索药品，包括别名
	 * 
	 * @param nameInitial
	 *            首字母
	 * @param method
	 *            药的种类
	 * @return
	 */
	@RequestMapping(value = "/quickSearch", method = RequestMethod.GET)
	public JSONObject getTcmmedicineName(String nameInitial, ProductCategory category,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			ClubSetting clubSetting = ClubSetting.getSetting(sm.getClubId());
			return JsonUtil.succeedJson(tMedicineService.quickSearch(nameInitial, category,
					clubService.getWarehouseIdByCategoryAndClubId(sm.getClubId(), category),clubSetting.getquickSearchStrategy().name()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年2月18日<br>
	 * 功能:只查询药名，不管库存里有没有药
	 * @param nameInitial
	 * @param category
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/quickSearchMeicineName", method = RequestMethod.GET)
	public JSONObject quickSearchMeicineName(String nameInitial, ProductCategory category,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(tMedicineService.quickSearchMeicineName(nameInitial, category,
					clubService.getWarehouseIdByCategoryAndClubId(sm.getClubId(), category)));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(HttpServletRequest request, TcmMedicine tcmMedicine) {
		try {
			Integer employeeId = (Integer) request.getSession().getAttribute("employeeId");
			tcmMedicine.setLastModifiedBy(employeeId);
			tMedicineService.insert(tcmMedicine);
			return JsonUtil.succeedJson(tcmMedicine.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/enable/{id}", method = RequestMethod.GET)
	public JSONObject enable(HttpServletRequest request, @PathVariable(value = "id") Integer id) {
		try {
			tMedicineService.setEnabled(id, 1);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/disable/{id}", method = RequestMethod.GET)
	public JSONObject disable(HttpServletRequest request, @PathVariable(value = "id") Integer id) {
		try {
			tMedicineService.setEnabled(id, 0);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(HttpServletRequest request, TcmMedicine tcmMedicine) {
		try {
			tMedicineService.update(tcmMedicine);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(HttpServletRequest request, Integer id) {
		try {
			tMedicineService.delete(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 根据拼音获取MedicineName和MedicineId包括别名
	 * @param nameInitial
	 * @return
	 */
	@RequestMapping(value="/medicineNames/{category}/{nameInitial}", method = RequestMethod.GET)
	public JSONObject getMedicineNames(@PathVariable(value="nameInitial") String nameInitial,
			@PathVariable(value="category") String category,
			HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			PriceStrategy priceStrategy = ClubSetting.getSetting(sm.getClubId()).getPriceStrategy4Keliji();
			List<TcmMedicine> medicineNames = new ArrayList<>();
			if (priceStrategy==PriceStrategy.Indepent)
				medicineNames = tMedicineService.getMedicineNames(nameInitial);
			if (priceStrategy==PriceStrategy.WarehouseSamePerGHerbal)
				medicineNames = tMedicineService.getMedicineNames(nameInitial,sm.getWarehouseId(),category);
			if (priceStrategy==PriceStrategy.ClubTypeSamePerGHerbal)
				medicineNames = tMedicineService.getMedicineNames(nameInitial,sm.getClubType(),category);
			return JsonUtil.succeedJson(medicineNames);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年8月6日
	 * @param id
	 * @param price
	 * @return
	 */
	@PostMapping(value="/updatePerGherbalPrice")
	public JSONObject updatePerGherbalPrice(Integer id,BigDecimal price,HttpServletRequest request) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			tMedicineService.updatePerGherbalPrice(id, price,sm.getEmployeeId());
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
