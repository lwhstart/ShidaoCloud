package com.shidao.dao;

import java.util.List;
import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.ExtConsumptionOrder;
import com.shidao.model.ExtTreatmentDistribution;

public interface ExtTreatmentDistributionMapper extends IBatisMapper<ExtTreatmentDistribution> {
    int deleteByPrimaryKey(Integer id);
    int deleteByUuid(String uuid);

    int insert(ExtTreatmentDistribution record);

    Integer insertSelective(ExtTreatmentDistribution record);

    ExtTreatmentDistribution selectByPrimaryKey(Integer id,String uuid);

    Integer updateByPrimaryKeySelective(ExtTreatmentDistribution record);

    int updateByPrimaryKey(ExtTreatmentDistribution record);
    
    Boolean isAmoutAcceptable(ExtTreatmentDistribution record);
    
    List<ExtTreatmentDistribution> selectTreatmentByDate(ExtConsumptionOrder order);
    
}