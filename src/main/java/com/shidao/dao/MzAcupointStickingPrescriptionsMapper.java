package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.MzAcupointStickingPrescription;

public interface MzAcupointStickingPrescriptionsMapper extends IBatisMapper<MzAcupointStickingPrescription>{
    
	List<MzAcupointStickingPrescription> selectByIdList(@Param(value="idList") String idList);
	
	List<String> getDiseases();
}