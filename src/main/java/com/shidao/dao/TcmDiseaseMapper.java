package com.shidao.dao;

import java.util.List;
import java.util.Map;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.TcmDisease;

public interface TcmDiseaseMapper extends  IBatisMapper<TcmDisease> {
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月26日
	 * 功能:自动处方查询疾病
	 * @param disease
	 * @return
	 */
	List<Map<String, Object>> listDiseaseAi(TcmDisease disease);
}