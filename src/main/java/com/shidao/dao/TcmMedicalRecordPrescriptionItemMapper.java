package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.TcmMedicalRecordPrescriptionItem;

public interface TcmMedicalRecordPrescriptionItemMapper extends IBatisMapper<TcmMedicalRecordPrescriptionItem>{
    
	void deleteByPrimaryKey(Integer id);
}