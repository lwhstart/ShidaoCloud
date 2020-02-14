package com.shidao.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.shidao.model.DataAnalysis;
import com.shidao.vo.FuZhenCondition;
import com.shidao.vo.FuZhenVO;
import com.shidao.vo.MedicineProfitComparisonVO;
import com.shidao.vo.PrescriptionProfitDetailVO;

@Repository
public interface DataAnalysisMapper{
	
	List<Map<String, Object>> getCustomerConsumptionAnalysis(DataAnalysis dataAnalysis);
	
	List<Map<String, Object>> getDoctorDataAnalysis(DataAnalysis dataAnalysis);
	
	List<Map<String, Object>> getProductAnalysis(DataAnalysis dataAnalysis);
	
	List<Map<String, Object>> getLiliaoDataAnalysisByAge(DataAnalysis dataAnalysis);
	
	List<Map<String, Object>> getLiliaoDataAnalysisByGender(DataAnalysis dataAnalysis);
	
	List<Map<String, Object>> listLiliaoInUsing(DataAnalysis dataAnalysis);
	
	List<Map<String, Object>> getPrescriptionSummary(DataAnalysis dataAnalysis);
	
	List<Map<String, Object>> getPrescriptionAnalysisByAge(DataAnalysis dataAnalysis);
	
	List<Map<String, Object>> getPrescriptionAnalysisByGender(DataAnalysis dataAnalysis);
	
	List<Map<String, Object>> getDiseaseName(DataAnalysis dataAnalysis);
	
	List<Map<String, Object>> getDiseaseRatioAnalysisByAge(DataAnalysis dataAnalysis);
	
	List<Map<String, Object>> getDiseaseRatioAnalysisByGender(DataAnalysis dataAnalysis);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月23日
	 * 功能:处方利润率 
	 * @param clubId
	 * @param date
	 * @return
	 */
	List<Map<String, Object>> listPrescriptionProfit(@Param("clubId")Integer clubId,@Param("date")Date date);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月5日<br>
	 * 功能:获取利润率（出去支付金额为0的数据）
	 * @param clubId
	 * @param date
	 * @return
	 */
	String getProfitRatio(@Param("clubId")Integer clubId,@Param("date")Date date);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月26日
	 * 功能:处方利润率详情
	 * @param prescriptionId
	 * @return
	 */
	PrescriptionProfitDetailVO getPrescriptionProfitDetail(Integer prescriptionId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月26日
	 * 功能:对比供应商药方详情
	 * @param prescriptionId
	 * @return
	 */
	List<Map<String, Object>> getComparisonVendorPrescription(@Param("prescriptionId") Integer prescriptionId,
			@Param("comparisonVendorId") Integer comparisonVendorId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月26日
	 * 功能:利润率统计
	 * @param startDate
	 * @param endDate
	 * @param clubId
	 * @return
	 */
	Map<String, Object> getprescriptionProfitSummary(@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("clubId")Integer clubId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年12月4日
	 * 功能:单个药对比
	 * @param medicineId
	 * @param clubId
	 * @return
	 */
	MedicineProfitComparisonVO getMedicineProfitComparison(@Param("medicineId")Integer medicineId,@Param("warehouseId")Integer warehouseId);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月25日<br>
	 * 功能:医保复诊率列表
	 * @return
	 */
	List<Map<String, Object>> getFuzhenlv4YbDisease(@Param("clubId")Integer clubId,@Param("monthStart")String monthStart,@Param("monthEnd")String monthEnd);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月25日<br>
	 * 功能:本地复诊列表
	 * @return
	 */
	List<Map<String, Object>> getFuzhenlv4LocalDisease(@Param("clubId")Integer clubId,@Param("monthStart")String monthStart,@Param("monthEnd")String monthEnd);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月25日<br>
	 * 功能:医保复诊客户列表
	 * @param disease
	 * @return
	 */
	List<FuZhenVO> getYbFuzhenList(FuZhenCondition condition);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月26日<br>
	 * 功能:本地复诊客户列表
	 * @param condition
	 * @return
	 */
	List<FuZhenVO> getLocalFuzhenList(FuZhenCondition condition);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月26日<br>
	 * 功能:医保复诊客户列表里的医生下拉框
	 * @param disease
	 * @return
	 */
	List<Map<String, Object>> getYbFuzhenDoctorList(@Param("diseaseName")String diseaseName,@Param("monthStart")String monthStart,@Param("monthEnd")String monthEnd);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月26日<br>
	 * 功能:本地复诊客户列表里的医生下拉框
	 * @param diseaseId
	 * @return
	 */
	List<Map<String, Object>> getLocalFuzhenDoctorList(@Param("diseaseId")Integer diseaseId,@Param("monthStart")String monthStart,@Param("monthEnd")String monthEnd);
}
