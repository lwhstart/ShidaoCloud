/**
 * 
 */
package com.shidao.controller;
/** 
* @author 作者  yjj: 
* @version 创建时间：2019年10月17日 上午9:22:44 
* @tag 功能：
*/

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.Zangfu;
import com.shidao.model.TcmSyndrome;
import com.shidao.service.TcmSyndromeService;
import com.shidao.util.JsonUtil;

/**
 * @author  yjj 
 * 2019年10月17日 
   *    功能：
 */
@RestController
@RequestMapping("/syndrome")
public class TcmSyndromeController extends BaseController{
	
      @Autowired
      private TcmSyndromeService syndromeService;
      
      @GetMapping(value = "/list")
      public JSONObject list(HttpServletRequest request, TcmSyndrome condition) {
    	  try {
    		  return JsonUtil.succeedJson(syndromeService.list(condition,1,Integer.MAX_VALUE).getList());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
      }
      
      @GetMapping(value = "/list/zangfu")
      public JSONObject list(HttpServletRequest request, Zangfu zangfu) {
    	  try {
    		  return JsonUtil.succeedJson(syndromeService.listByZangfu(zangfu));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
      }

}
