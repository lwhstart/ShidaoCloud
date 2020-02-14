package com.shidao.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.MRPrescriptionCategory;
import com.shidao.model.SdbsProduct;
import com.shidao.model.TcmMedicalRecordPrescription;
import com.shidao.model.TcmMedicalRecordPrescriptionItem;
import com.shidao.service.SdbsProductService;
import com.shidao.service.TcmMedicalRecordPrescriptionItemService;
import com.shidao.service.TcmMedicalRecordPrescriptionService;
import com.shidao.service.TcmMedicalRecordService;
import com.shidao.service.WmsInventoryService;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value = "/medicalRecordPrescriptionItem")
public class TcmMedicalRecordPrescriptionItemController extends BaseController {

	@Autowired
	private TcmMedicalRecordPrescriptionItemService medicalRecordPrescriptionItemService;

	@Autowired
	private WmsInventoryService inventoryService;

	@Autowired
	private TcmMedicalRecordService medicalRecordService;
	
	@Autowired
	private SdbsProductService productService;
	
	@Autowired
	private TcmMedicalRecordPrescriptionService medicalRecordPrescriptionService;

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(TcmMedicalRecordPrescriptionItem item,MRPrescriptionCategory category,String medicalRecordUuid) {
		try {
			JSONObject jsonObject = null;
			switch (medicalRecordPrescriptionItemService.addOrUpdate(item,medicalRecordUuid,category)) {
			case "插入":
				jsonObject = JsonUtil.succeedJson(item.getId());
				jsonObject.put("isInventorySufficient",
						inventoryService.isInventorySufficient(item,category));
				TcmMedicalRecordPrescriptionItem key = medicalRecordPrescriptionItemService.selectByPrimaryKey(item.getId());
				if (key != null && key.getProductId() != null) {
					SdbsProduct product = productService.selectByPrimaryKey(key.getProductId());
					jsonObject.put("chargeUnit", product.getChargeUnit());
					jsonObject.put("specification", product.getSpecification());
				}
				break;
			case "修改":
				jsonObject = new JSONObject();
				jsonObject.put("status", "update");
				jsonObject.put("id", item.getId());
				jsonObject.put("quantity", item.getQuantity());
				jsonObject.put("isInventorySufficient",
						inventoryService.isInventorySufficient(item,category));
				break;
			default:
				return JsonUtil.emptyJson();
			}
			//获取处方amount总价
			jsonObject.put("amount", medicalRecordPrescriptionService.
					getAmount(medicalRecordPrescriptionItemService.selectByPrimaryKey(item.getId()).getPrescriptionId()));
			jsonObject.put("prescriptionId", item.getPrescriptionId());
			return jsonObject;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(TcmMedicalRecordPrescriptionItem medicalRecordPrescriptionItem) {
		try {
			Map<String, Object> map = new HashMap<>();
			Integer medicalRecordPrescriptionItemId =medicalRecordPrescriptionItem.getId();
			if (medicalRecordPrescriptionItemId == null)
				throw new ShidaoException("指定编号");
			medicalRecordService.checkPrescriptionEditable(null, null, medicalRecordPrescriptionItemId);
			medicalRecordPrescriptionItemService.updateByPrimaryKeySelective(medicalRecordPrescriptionItem);
			TcmMedicalRecordPrescriptionItem item = medicalRecordPrescriptionItemService
					.selectByPrimaryKey(medicalRecordPrescriptionItemId);
			
			TcmMedicalRecordPrescription recordPrescription = medicalRecordPrescriptionService.selectByPrimaryKey(item.getPrescriptionId());
			map.put("amount", medicalRecordPrescriptionService.getAmount(item.getPrescriptionId()));
			map.put("isInventorySufficient", inventoryService.isInventorySufficient(item,recordPrescription.getCategory()));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public JSONObject delect(
			@RequestParam(value = "medicalRecordPrescriptionItemId") Integer medicalRecordPrescriptionItemId) {
		try {
			Map<String, Object> map = new HashMap<>();
			medicalRecordService.checkPrescriptionEditable(null, null, medicalRecordPrescriptionItemId);			
			
			TcmMedicalRecordPrescriptionItem item = medicalRecordPrescriptionItemService
					.selectByPrimaryKey(medicalRecordPrescriptionItemId);
			medicalRecordPrescriptionItemService.deleteByPrimaryKey(medicalRecordPrescriptionItemId);
			//获取处方amount总价
			map.put("amount", medicalRecordPrescriptionService.getAmount(item.getPrescriptionId()));
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

}
