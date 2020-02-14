/**
 * 
 */
package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdepPayCategory;
import com.shidao.model.SdepPayMethod;
import com.shidao.service.SdepPayCategoryService;
import com.shidao.service.SdepPayMethodService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;

/** 
* @author 作者  yjj: 
* @version 创建时间：2019年9月24日 下午3:33:16 
* @tag 功能：
*/

/**
 * @author  yjj 
 * 2019年9月24日 
   *    功能：添加uuid返回uuid
 */
@RestController
@RequestMapping("/payCategory")
public class SdepPayCategoryController extends BaseController{
	
	@Autowired
	private SdepPayCategoryService sdepPayCategoryService;
	
	@Autowired
	private SdepPayMethodService sdepPayMethodService;
	
	/**
	  *  插入支付类型
	 * @param category
	 * @param request
	 * @return
	 * @author yjj 2019年9月25日
	 */
	@PostMapping(value="/insert")
	public JSONObject insert(SdepPayCategory category,HttpServletRequest request) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			category.setClubId(es.getClubId());
			sdepPayCategoryService.inserSelective(category);
			return JsonUtil.succeedJson(sdepPayCategoryService.selectByPrimaryKey(category.getId()));  
		}catch (Exception e) {
			return JsonUtil.errjson(e);
	    }
   }
	/**
	 * 删除支付类型
	 * @param payCategoryUuid
	 * @param request
	 * @return
	 * @author yjj 2019年9月25日
	 */
	@PostMapping(value = "delete")
	public JSONObject delete(String uuid,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			sdepPayCategoryService.deleteByUuid(uuid);
			return JsonUtil.succeedJson();
		}catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * 切换支付方式enabled状态
	 * @param sdepPayMethod
	 * @param request
	 * @return
	 * @author yjj 2019年9月29日
	 */
	@PostMapping(value = "/payMethod/update")
	public JSONObject updateByUuidEnabled(SdepPayMethod sdepPayMethod,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
	        sdepPayMethodService.updateByPrimaryKeySelective(sdepPayMethod);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 插入支付方式
	 * @param uuid
	 * @param payCategoryUuid
	 * @param sdepPayMethod
	 * @param request
	 * @return
	 * @author yjj 2019年9月29日
	 */
	@PostMapping(value = "{uuid}/payMethod/insert")
	public JSONObject insertPayMethod(SdepPayMethod payMethod,HttpServletRequest request) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			payMethod.getPayCategory().setClubId(es.getClubId());
			sdepPayMethodService.insertSelective(payMethod);
			return JsonUtil.succeedJson(sdepPayMethodService.selectByPrimaryKey(payMethod.getId()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	/**
	 * 删除支付方式
	 * @param payMethod
	 * @param request
	 * @return
	 * @author yjj 2019年9月30日
	 */
	@PostMapping(value = "/payMethod/delete")
	public JSONObject deletePayMethod(String uuid,HttpServletRequest request) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
		    sdepPayMethodService.deleteByUuid(uuid);
		    return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * 修改结算类型名字
	 * @param id
	 * @return
	 * @author yjj 2019年10月10日
	 */
	@PostMapping(value = "/update")
	public JSONObject updateByPrimaryKeySelective(SdepPayCategory payCategory,HttpServletRequest request) {
		try {
			EmployeeSessionManager es = new EmployeeSessionManager(request.getSession());
			payCategory.setClubId(es.getClubId());
			sdepPayCategoryService.updateByPrimaryKeySelective(payCategory);
			return JsonUtil.succeedJson(sdepPayCategoryService.selectByUuid(payCategory.getUuid()));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
