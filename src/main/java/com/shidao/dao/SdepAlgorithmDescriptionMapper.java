package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.AlgorithmName;
import com.shidao.model.SdepAlgorithmDescription;

public interface SdepAlgorithmDescriptionMapper extends IBatisMapper<SdepAlgorithmDescription>{
    void deleteByPrimaryKey(Integer id);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年11月14日
     * 功能:
     * @param name
     * @return
     */
    String getDescription(AlgorithmName name);
}