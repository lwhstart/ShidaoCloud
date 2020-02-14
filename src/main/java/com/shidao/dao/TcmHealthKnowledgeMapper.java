package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.TcmHealthKnowledge;

@Repository
public interface TcmHealthKnowledgeMapper extends IBatisMapper<TcmHealthKnowledge>{

	public List<TcmHealthKnowledge> listByCondition(@Param(value="diseaseName")String diseaseName);
}