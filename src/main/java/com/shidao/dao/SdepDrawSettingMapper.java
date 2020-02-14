package com.shidao.dao;


import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdepDrawSetting;


/**
 * @author lwh
 *
 */
public interface SdepDrawSettingMapper extends IBatisMapper<SdepDrawSetting> {
    
    int deleteByPrimaryKey(Integer id);
    
    int insert(SdepDrawSetting record);
    
    SdepDrawSetting selectByPrimaryKey(Integer id);
   
}