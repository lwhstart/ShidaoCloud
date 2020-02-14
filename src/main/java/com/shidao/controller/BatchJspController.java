package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shidao.enums.ProductCategory;
import com.shidao.service.SdcomClubService;
import com.shidao.service.SdepFileService;
import com.shidao.service.WmsWarehouseService;
import com.shidao.util.EmployeeSessionManager;

@Controller
@RequestMapping(value = "/batch")
public class BatchJspController extends BaseController  {
	
	private static final String WORKSPACE_PATH = "/display/workspace/";

	@Autowired
	WmsWarehouseService warehoseService;

	@Autowired
	SdepFileService fileService;
	
	@Autowired
	private SdcomClubService clubService;

	@GetMapping(value = "/product/{category}")
	public String batchUploadProduct(HttpServletRequest request, Model model, @PathVariable ProductCategory category) {
		try {
			EmployeeSessionManager esManager = new EmployeeSessionManager(request.getSession());
			Integer warehouseId = clubService.getWarehouseIdByCategoryAndClubId(esManager.getClubId(), category);
			model.addAttribute("templateFileUuid", fileService.getSystemFileUuid(category.name() + "Batch", "template"));
			model.addAttribute("category", category);
			model.addAttribute("vendors", warehoseService.listVendorsByWarehouseId(warehouseId));
		} catch (Exception e) {
			model.addAttribute("errorMsg", e.getMessage());
		}

		return WORKSPACE_PATH + "batchProduct";
	}
}
