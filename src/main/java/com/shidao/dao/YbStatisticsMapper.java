package com.shidao.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.shidao.model.DataAnalysis;

/** 
* @author 作者 zzp: 
* @version 创建时间：2018年12月17日 下午2:09:59 
* 类说明 
*/
@Repository
public interface YbStatisticsMapper {

	/**
	 * 医保订单合计
	 * @author 作者zzp: 
	 * @version 创建时间：2018年12月17日 下午4:17:42 
	 * @param dateStart
	 * @param dateEnd
	 * @param clubId
	 * @return
	 */
	Map<String, Object> getYbSalesOrderStatistics(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd, @Param("clubId") Integer clubId);
	
	/**
	 * 医保客户消费分析
	 * @author 作者zzp: 
	 * @version 创建时间：2018年12月12日 下午2:56:06 
	 * @param dataAnalysis
	 * @return
	 */
	List<Map<String, Object>> getYbCustomerConsumptionAnalysis(DataAnalysis dataAnalysis);
	
	/**
	 * 治疗和年龄段的统计
	 * @author 作者zzp: 
	 * @version 创建时间：2018年12月13日 下午2:54:52 
	 * @param dataAnalysis
	 * @return
	 */
	List<Map<String, Object>> getYbLiliaoDataAnalysisByAge(DataAnalysis dataAnalysis);
	
	/**
	 * 治疗和性别的统计
	 * @author 作者zzp: 
	 * @version 创建时间：2018年12月17日 下午4:17:06 
	 * @param dataAnalysis
	 * @return
	 */
	List<Map<String, Object>> getYbLiliaoDataAnalysisByGender(DataAnalysis dataAnalysis);
	
	/**
	 * 处方对应疾病年龄段分析
	 * @author 作者zzp: 
	 * @version 创建时间：2018年12月18日 下午5:01:30 
	 * @param dataAnalysis
	 * @return
	 */
	List<Map<String, Object>> getYbPrescriptionAnalysisByAge(DataAnalysis dataAnalysis);
	
	/**
	 * 处方对应疾病性别分析
	 * @author 作者zzp: 
	 * @version 创建时间：2018年12月18日 下午8:01:03 
	 * @param dataAnalysis
	 * @return
	 */
	List<Map<String, Object>> getYbPrescriptionAnalysisByGender(DataAnalysis dataAnalysis);
	
	/**
	 * 根据条件获取医保列表
	 * @author 作者zzp: 
	 * @version 创建时间：2018年12月18日 下午9:10:30 
	 * @param dataAnalysis
	 * @return
	 */
	List<Map<String, Object>> getDiseaseName(DataAnalysis dataAnalysis);
}
