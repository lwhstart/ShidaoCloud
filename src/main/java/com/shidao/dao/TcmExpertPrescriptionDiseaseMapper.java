package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.TcmExpertPrescriptionDisease;

public interface TcmExpertPrescriptionDiseaseMapper extends IBatisMapper<TcmExpertPrescriptionDisease> {
	TcmExpertPrescriptionDisease selectByDiseaseId(Integer diseaseId);
}