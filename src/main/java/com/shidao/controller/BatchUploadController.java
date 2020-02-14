package com.shidao.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.shidao.enums.RestControllerStatus;
import com.shidao.model.SdbsProduct;
import com.shidao.service.BatchService;
import com.shidao.util.EmployeeSessionManager;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value = "/batch")
public class BatchUploadController extends BaseController {
	
	private static final String BATCH_FILE_KEY="batchFile";
	
	@Autowired
	BatchService batchService;


	@PostMapping(value = "/product")
	public JSONObject batchUploadProduct(HttpServletRequest request, SdbsProduct baseInfo) {
		try {
			EmployeeSessionManager.checkLogin(request.getSession());
			
			MultipartFile file =  ((MultipartHttpServletRequest) request).getFile(BATCH_FILE_KEY);
			if(file == null) {
				throw new ShidaoException("没有有效的药品文件。");
			}

			String fileUuid =batchService.createProduct(file, baseInfo);
			return fileUuid == null ? 
					JsonUtil.succeedJson():
						JsonUtil.resultWithStatus(RestControllerStatus.failWithFileId, fileUuid);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月30日
	 * 功能:读取Excel表格内容，然后批量插入
	 * @param request
	 * @param modelName
	 * @return
	 */
	@PostMapping(value = "/excel")
	public JSONObject excel(HttpServletRequest request, String modelName) {
		try {
			EmployeeSessionManager sm = new EmployeeSessionManager(request.getSession());
			Map<String, Object> map = new HashMap<>();
			MultipartFile file =  ((MultipartHttpServletRequest) request).getFile(BATCH_FILE_KEY);
			if(file == null) {
				throw new ShidaoException("没有文件啊！");
			}
			
			String uuid =batchService.insertBatch(file, modelName,sm.getClubId(),map);
			if (uuid != null) {
				return JsonUtil.resultWithStatus(RestControllerStatus.failWithFileId, uuid);
			}
			if (map != null && !map.isEmpty()) {
				return JsonUtil.succeedJson(map);
			}
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
