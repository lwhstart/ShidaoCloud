package com.shidao.dao;

import java.util.List;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdcomRelationDiseaseCategoryDoctor;
import com.shidao.model.TcmDiseaseCategory;

public interface SdcomRelationDiseaseCategoryDoctorMapper extends IBatisMapper<SdcomRelationDiseaseCategoryDoctor>{
    void deleteByPrimaryKey(Integer id);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年5月9日
     * 功能:科室和医生二级联动
     * @param condition
     * @return
     */
    List<TcmDiseaseCategory> listDiseaseCategoryOfDoctor(SdcomRelationDiseaseCategoryDoctor condition);
}