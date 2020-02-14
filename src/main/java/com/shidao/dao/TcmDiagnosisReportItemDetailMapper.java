package com.shidao.dao;

import java.util.List;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.TcmDiagnosisReportItemDetail;

public interface TcmDiagnosisReportItemDetailMapper extends IBatisMapper<TcmDiagnosisReportItemDetail> {
    int deleteByPrimaryKey(Integer id);
    
    /**
     * 批量插入诊断条目的详情
     *	@param list
     * @author yzl , Created at 2019年10月17日
     *
     */
    void insertBatch(List<TcmDiagnosisReportItemDetail> list);
}