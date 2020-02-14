package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdcomRelationDoctorClub;

public interface SdcomRelationDoctorClubMapper extends IBatisMapper<SdcomRelationDoctorClub>{
	
	/**
	 * 删除医生和门店的关系
	 * @author yzl 2018年6月21日
	 * @param doctorId
	 * @param clubId
	 */
	void removeDotorRelation(@Param("doctorId") Integer doctorId, @Param("clubId") Integer clubId);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月5日<br>
	 * 功能:把助理设置为null
	 * @param assistantId
	 * @param clubId
	 */
	void setAssistantNull(@Param("assistantId") Integer assistantId, @Param("clubId") Integer clubId);
	
}