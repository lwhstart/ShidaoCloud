package com.shidao.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdcomClubOperationHistory;
import com.shidao.vo.ClubOperationStaticsVO;

public interface SdcomClubOperationHistoryMapper extends IBatisMapper<SdcomClubOperationHistory>{
	
	List<ClubOperationStaticsVO> getStatis(ClubOperationStaticsVO clubOperationStaticsVO);
	
	List<Map<String, Object>> getClubOperationHistoryStatistics(@Param("clubId") Integer clubId);
}