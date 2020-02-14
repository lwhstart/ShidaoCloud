package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.TcmDiagnosisSetting;

public interface TcmDiagnosisSettingMapper extends IBatisMapper<TcmDiagnosisSetting> {
    int deleteByPrimaryKey(Integer id);
}