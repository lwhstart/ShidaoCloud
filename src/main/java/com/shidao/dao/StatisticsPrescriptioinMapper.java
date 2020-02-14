package com.shidao.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.shidao.enums.PDFPrescriptionCategory;
import com.shidao.vo.PrescriptionUsageDetailVO;

@Repository
public interface StatisticsPrescriptioinMapper{

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月23日 功能:处方使用统计表
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @return
	 */
	List<Map<String, Object>> getPrescriptionUsage(@Param("dateStart") Date dateStart, @Param("dateEnd") Date dateEnd,
			@Param("clubId") Integer clubId);

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月23日 功能:系统处方使用一览
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @return
	 */
	List<Map<String, Object>> getSysPrescriptionUsage(@Param("dateStart") Date dateStart,
			@Param("dateEnd") Date dateEnd, @Param("clubId") Integer clubId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月28日
	 * 功能:点击系统处方的数量，弹出开方详情
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @param doctorId
	 * @return
	 */
	List<Map<String, Object>> getSysPrescriptionUsageInfo(@Param("dateStart") Date dateStart,
			@Param("dateEnd") Date dateEnd, @Param("clubId") Integer clubId,@Param("doctorId")Integer doctorId);

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月23日 功能:系统处方【专家秘方】调方一览
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @return
	 */
	List<Map<String, Object>> getSysrescriptionModification(@Param("dateStart") Date dateStart,
			@Param("dateEnd") Date dateEnd, @Param("clubId") Integer clubId,
			@Param("category") PDFPrescriptionCategory category);

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月23日
	 * 功能:自动处方复用统计表
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @return
	 */
	List<Map<String, Object>> getSysPrescriptionReuse(@Param("dateStart") Date dateStart,
			@Param("dateEnd") Date dateEnd, @Param("clubId") Integer clubId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月2日
	 * 功能:所有病的数量
	 * @return
	 */
	Integer getDiseaseCount();
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月23日
	 * 功能:自动处方使用详情
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @param diseaseId
	 * @return
	 */
	List<Map<String, Object>> getSysPrescriptionUsageDetail(@Param("dateStart") Date dateStart,
			@Param("dateEnd") Date dateEnd, @Param("clubId") Integer clubId,@Param("diseaseId") Integer diseaseId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月30日
	 * 功能:手动开方列表
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @param doctorId
	 * @return
	 */
	List<PrescriptionUsageDetailVO> getManualPrescriptionUsageDetail(PrescriptionUsageDetailVO condition);
}
