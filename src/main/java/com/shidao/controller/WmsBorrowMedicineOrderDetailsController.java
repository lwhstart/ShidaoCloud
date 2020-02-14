package com.shidao.controller;
/** 
* @author 作者 zzp: 
* @version 创建时间：2019年1月31日 上午11:12:05 
* 类说明 
*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.service.WmsBorrowMedicineOrderDetailsService;
import com.shidao.util.JsonUtil;

@RestController
@RequestMapping(value="/wmsBorrowMedicineOrderDetails")
public class WmsBorrowMedicineOrderDetailsController extends BaseController{
	
	@Autowired
	private WmsBorrowMedicineOrderDetailsService borrowMedicineOrderDetailsService;
	
	/**
	 * 根据id删除
	 * @author 作者zzp: 
	 * @version 创建时间：2019年1月31日 上午11:15:54 
	 * @param id
	 * @return
	 */
	@PostMapping(value="/delete")
	public JSONObject deleteByPrimaryKey(@RequestParam(value="id") Integer id) {
		try {	
			borrowMedicineOrderDetailsService.deleteByPrimaryKey(id);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
