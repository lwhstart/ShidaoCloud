package com.shidao.controller;
/** 
* @author 作者 zzp: 
* @version 创建时间：2019年1月29日 下午6:13:47 
* 类说明 
*/

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.WmsBorrowMedicineOrder;
import com.shidao.service.WmsBorrowMedicineOrderService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value="/wmsBorrowMedicineOrder")
public class WmsBorrowMedicineOrderController extends BaseController{
	@Autowired
	private WmsBorrowMedicineOrderService borrowMedicineOrderService;

	/**
	 * 根据uuid删除整张借药单
	 * @author 作者zzp: 
	 * @version 创建时间：2019年2月1日 下午3:35:24 
	 * @param uuid
	 * @return
	 */
	@PostMapping(value="/deleteByUuid")
	public JSONObject deleteByUuid(@RequestParam(value="uuid") String uuid) {
		try {
			borrowMedicineOrderService.deleteByUuid(uuid);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 更新借药单的
	 * @author 作者zzp: 
	 * @version 创建时间：2019年2月1日 下午3:36:15 
	 * @param uuid
	 * @param lendWarehouseId
	 * @param lendVendorId
	 * @return
	 */
	@PostMapping(value="/updateBorrowMedicine")
	public JSONObject updateBorrowMedicine(@RequestParam(value="uuid") String uuid,@RequestParam(value="lendWarehouseId") Integer lendWarehouseId,@RequestParam(value="lendVendorId") Integer lendVendorId) {
		try {
			borrowMedicineOrderService.updateBorrowMedicine(uuid, lendWarehouseId, lendVendorId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping(value="/update")
	public JSONObject update(WmsBorrowMedicineOrder borrowMedicineOrder) {
		try {
			borrowMedicineOrderService.updateByPrimaryKeySelective(borrowMedicineOrder);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping(value="/approveMedicineBorrowOrder/{uuid}")
	public JSONObject approveMedicineBorrowOrder(HttpServletRequest request,@PathVariable(value="uuid") String uuid) {
		try {
			EmployeeSessionManager eManager = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(borrowMedicineOrderService.approveMedicineBorrowOrder(uuid, eManager.getEmployeeId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@PostMapping(value="/insertBorrowMedicine")
	public JSONObject ads(HttpServletRequest request,@RequestParam(value="deliveryVoucherId") Integer deliveryVoucherId) {
		try {
			EmployeeSessionManager eManager = new EmployeeSessionManager(request.getSession());
			return JsonUtil.succeedJson(borrowMedicineOrderService.insertBorrowMedicine(deliveryVoucherId,eManager.getEmployeeId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
