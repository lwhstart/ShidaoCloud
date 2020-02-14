package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.ExtSetting;

public interface ExtSettingMapper extends IBatisMapper<ExtSetting> {
    int deleteByPrimaryKey(Integer id);

    ExtSetting selectByPrimaryKey(Integer id);

	int insertValueIfNotExist(ExtSetting setting);

}