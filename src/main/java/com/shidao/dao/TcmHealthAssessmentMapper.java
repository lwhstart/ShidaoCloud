package com.shidao.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.MzDisease;
import com.shidao.model.TcmHealthAssessment;

@Repository
public interface TcmHealthAssessmentMapper extends IBatisMapper<TcmHealthAssessment> {

	TcmHealthAssessment getLastAssessmentRecords(@Param(value="customerId") Integer customerId);
	
	Map<String, String> getTizhiManagement(Integer assessmentId);
	
	/**
	 * 获取自我检测对应的 五位一体解决方案
	 * @param assessmentId
	 * @return
	 */
	Map<String, String> getSolutions(Integer assessmentId);
	
	List<MzDisease> getDisease(Integer assessmentId);
	
	List<String> getDiseasesOfCustomer(@Param(value="customerId") Integer customerId);
	
	List<TcmHealthAssessment> getHealthAssessmentMaizhenyi(Integer customerId);
	
	
}
