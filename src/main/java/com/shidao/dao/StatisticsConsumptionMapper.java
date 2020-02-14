package com.shidao.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.shidao.enums.ProductCategory;
import com.shidao.enums.SalesOrderCategory;
import com.shidao.model.DataAnalysis;
import com.shidao.vo.ClubDailyReportVo;
import com.shidao.vo.ClubIncomeVo;
import com.shidao.vo.CustomerConsumptionDetailVo;
import com.shidao.vo.CwDinggaoVo;

@Repository
public interface StatisticsConsumptionMapper {

	List<Map<String, Object>> getMemberStatistics(@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("clubId") Integer clubId);
	
	List<Map<String, Object>> getCustomerMember(@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("clubId") Integer clubId);
	
	Map<String, Object> getMemberStatisticsByWeek(@Param("clubId") Integer clubId);
	
	Map<String, Object> getNoMemberStatisticsByWeek(@Param("clubId") Integer clubId);
	
	List<Map<String, Object>> getCustomerFlowByDate(@Param("dateFrom") Date dateFrom,@Param("dateTo") Date dateTo,@Param("clubId") Integer clubId);
	
	List<ClubIncomeVo> getClubIncome(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd, @Param("clubId") Integer clubId);
	
	List<Map<String, Object>> getCustomerConsumptionAmount(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd, @Param("clubId") Integer clubId);
	
	List<CustomerConsumptionDetailVo> getCustomerConsumptionDetail(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd, @Param("clubId") Integer clubId, @Param("salesId") Integer salesId);
	
	List<ClubDailyReportVo> getClubDailyReport(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd, @Param("clubId") Integer clubId);
	
	List<Map<String, Object>> getCustomerConsumptionRatio(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd, @Param("clubId") Integer clubId);
	
	List<Map<String, Object>> getDoctorAnalysis(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd, @Param("clubId") Integer clubId);
	
	/**
	 * 获取制定时间段某个类型商品的销售详情
	 * @author Administrator yzl, 2018-4-8
	 * @param dateStart 开始日期
	 * @param dateEnd 结束日期
	 * @param clubId 门店编号
	 * @param category 销售类型
	 * @return
	 */
	List<Map<String, Object>> getSalesDetailOfCategory(@Param("dateStart") Date dateStart,
			@Param("dateEnd") Date dateEnd, 
			@Param("clubId") Integer clubId,
			@Param("category") SalesOrderCategory category);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月28日
	 * 功能:获取指定时间段某个商品的销售详情
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @param productId
	 * @return
	 */
	List<Map<String, Object>> getSalesDetailOfCategoryDetail(@Param("dateStart") Date dateStart,
			@Param("dateEnd") Date dateEnd, 
			@Param("clubId") Integer clubId,
			@Param("productId") Integer productId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月28日
	 * 功能:得到销售详情里的员工id
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @param productId
	 * @return
	 */
	List<Map<String, Object>> getSalesDetailOfCategoryDetailEmployeeId(@Param("dateStart") Date dateStart,
			@Param("dateEnd") Date dateEnd, 
			@Param("clubId") Integer clubId,
			@Param("productId") Integer productId);
	
	List<Map<String, Object>> getPrescriptionFlow(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd, @Param("clubId") Integer clubId);
	
	List<Map<String, Object>> getPrescriptionAmount(DataAnalysis dataAnalysis);
	
	List<CwDinggaoVo> selectByPayDate(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd,@Param("clubId") Integer clubId);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月8日<br>
	 * 功能:根据支付类型计算总价
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @return
	 */
	List<Map<String, Object>> selectbyPayMethod(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd,@Param("clubId") Integer clubId);
	
	List<Map<String, Object>> getPrescriptionSummary(DataAnalysis dataAnalysis);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月9日<br>
	 * 功能:处方开药统计
	 * @param dateStart
	 * @param dateEnd
	 * @param warehouseId
	 * @param category
	 * @return
	 */
	List<Map<String, Object>> getInventoryByMedicine(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd,
			@Param("warehouseId") Integer warehouseId,@Param("category")ProductCategory category);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月10日<br>
	 * 功能:单个药开方详情
	 * @param dateStart
	 * @param dateEnd
	 * @param warehouseId
	 * @param category
	 * @param medicineId
	 * @return
	 */
	List<Map<String, Object>> getInventoryMedicineDetail(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd,
			@Param("warehouseId") Integer warehouseId,@Param("category")ProductCategory category,@Param("medicineId")Integer medicineId);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月25日<br>
	 * 功能:业务经理各个门店的药品使用量统计
	 * @param dateStart
	 * @param dateEnd
	 * @param employeeId
	 * @param category
	 * @return
	 */
	List<Map<String, Object>> getManager4ClubMedicineAmount(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd,
			@Param("employeeId") Integer employeeId,@Param("category")ProductCategory category);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月25日<br>
	 * 功能:业务经理单个门店的药品使用量统计
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @param category
	 * @return
	 */
	List<Map<String, Object>> getManager4ClubMedicineAmountDetail(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd,
			@Param("clubId") Integer clubId,@Param("category")ProductCategory category);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月28日<br>
	 * 功能:财务查看各个门店的销售价和成本价
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @return
	 */
	List<Map<String, Object>> getVendorsStatistics(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd,
			@Param("clubId") Integer clubId);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月28日<br>
	 * 功能:财务查看配药详情
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @return
	 */
	List<Map<String, Object>> getDispensingDetailStatistics(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd,
			@Param("clubId") Integer clubId);
}
