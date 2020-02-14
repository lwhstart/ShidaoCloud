package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.TcmMedicalRecordDiagnosingHistory;

public interface TcmMedicalRecordDiagnosingHistoryMapper extends IBatisMapper<TcmMedicalRecordDiagnosingHistory> {

	int deleteByPrimaryKey(Integer id);

}