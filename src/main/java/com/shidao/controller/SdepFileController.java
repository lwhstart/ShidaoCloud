package com.shidao.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.SdepFile;
import com.shidao.service.SdepFileService;
import com.shidao.util.ApplicationResource;
import com.shidao.util.JsonUtil;
import com.shidao.util.QRCodeUtil;

@RestController
@RequestMapping("/sdepFile")
public class SdepFileController extends BaseController {

	@Autowired
	SdepFileService fileService;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public JSONObject update(HttpServletRequest request, String category) {
		try {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 获取multiRequest 中所有的文件名
			Map<String, SdepFile> result = fileService.upload(multiRequest, category);
			JSONObject jsonObject = JsonUtil.succeedJson(result.values());
			jsonObject.put("detail", result);
			return jsonObject;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月30日
	 * 功能:单个上传文件
	 * @param request
	 * @param category
	 * @return
	 */
	@RequestMapping(value = "/upload/{category}/single", method = RequestMethod.POST)
	public JSONObject updateSingle(HttpServletRequest request, @PathVariable("category")String category ) {
		try {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 获取multiRequest 中所有的文件名
			SdepFile values = fileService.upload(multiRequest, category).values().stream().findFirst().orElse(null);
			Map<String, Object> map = JsonUtil.succeedJson();
			map.put("id", values.getId());
			map.put("uuid", values.getUuid());
			return JsonUtil.succeedJson(map);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/upload/{category}", method = RequestMethod.POST)
	public JSONObject updateWithCategory(HttpServletRequest request, @PathVariable String category) {
		try {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 获取multiRequest 中所有的文件名
			Map<String, SdepFile> result = fileService.upload(multiRequest, category);
			JSONObject jsonObject = JsonUtil.succeedJson(result.values());
			jsonObject.put("detail", result);
			return jsonObject;
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/{fileId}", method = RequestMethod.GET)
	public void test(HttpServletResponse response, @PathVariable(value = "fileId") Integer fileId) {
		try (ServletOutputStream servletOutputStream = response.getOutputStream();){
			servletOutputStream.write(fileService.getFileContent(fileId));
		} catch (Exception e) {
			_Logger.error(fileId);
			_Logger.error(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月26日
	 * @param response
	 * @param uuid
	 */
	@GetMapping(value = "/img/{uuid}")
	public void imgUuid(HttpServletResponse response, @PathVariable(value = "uuid") String uuid) {
		try (ServletOutputStream servletOutputStream = response.getOutputStream();){
			servletOutputStream.write(fileService.getFileContent(uuid));
		} catch (Exception e) {
			_Logger.error(uuid);
			_Logger.error(e);
		}
	}
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月26日
	 * 功能:下载图片
	 * @param response
	 * @param uuid
	 */
	@GetMapping(value = "/download/{uuid}")
	public void downloadUuid(HttpServletResponse response, @PathVariable(value = "uuid") String uuid) {
		try (ServletOutputStream servletOutputStream = response.getOutputStream();){
			SdepFile fileInfo = fileService.selectByUuid(uuid);
			response.setHeader("Content-Type", "application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileInfo.getFileFullName());
			servletOutputStream.write(fileService.getFileContent(uuid));
		} catch (Exception e) {
			_Logger.error(uuid);
			_Logger.error(e);
		}
	}

	@GetMapping(value = "/qrCode")
	public void getQrCode(HttpServletRequest request,HttpServletResponse response,@RequestParam(value="msg") String msg) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
			 ServletOutputStream servletOutputStream = response.getOutputStream();){
			_Logger.error("MSG:" + msg);
			String path = request.getServletContext().getRealPath("");
			String imagePath = path+ApplicationResource.resource.getString("logo");
			// 产生msg对应的二维码
			BufferedImage image = QRCodeUtil.createImage(msg,imagePath);
			ImageIO.write(image, "jpg", out);
			servletOutputStream.write(out.toByteArray());
		} catch (Exception e) {
			_Logger.error(msg);
			_Logger.error(e);
		}
	}
	
	@GetMapping("/download1/{uuid}")
	public void downloadWithUuid(HttpServletResponse response, @PathVariable(value = "fileId") String uuid) {
		try ( ServletOutputStream servletOutputStream = response.getOutputStream();){			
			response.setContentType(uuid);
			SdepFile fileInfo = fileService.selectByUuid(uuid);
			response.setHeader("content-disposition","attachment; filename="+fileInfo.getFileFullName());
			servletOutputStream.write(fileService.getFileContent(uuid));
			response.flushBuffer();
		} catch (Exception e) {
			_Logger.error(uuid);
			_Logger.error(e);
		}
	}
}
