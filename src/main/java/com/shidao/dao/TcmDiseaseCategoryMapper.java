package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.TcmDiseaseCategory;

public interface TcmDiseaseCategoryMapper extends IBatisMapper<TcmDiseaseCategory>{
	
    /**
     * @author 创建人:liupengyuan,时间:2018年5月10日
     * @param id
     */
    void deleteByPrimaryKey(Integer id);
}