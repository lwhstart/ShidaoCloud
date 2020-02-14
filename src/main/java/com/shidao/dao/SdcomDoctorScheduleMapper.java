package com.shidao.dao;

import java.util.List;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsDoctorAvaiablePeriod;
import com.shidao.model.SdcomDoctorSchedule;
import com.shidao.model.TcmDiseaseCategory;

public interface SdcomDoctorScheduleMapper extends IBatisMapper<SdcomDoctorSchedule>{
    void deleteByPrimaryKey(Integer id);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年5月8日
     * @param condition
     * @return
     */
    List<TcmDiseaseCategory> listDoctorSchedule(SdcomDoctorSchedule condition);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年5月14日
     * 功能:远程预约看诊排班表
     * @param condition
     * @return
     */
    List<TcmDiseaseCategory> listRemoteDiagnosticAppointment(SdcomDoctorSchedule condition);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年5月9日
     * 功能:插入时间段表
     * @param condition
     */
    void insertDoctorAvaiable(SdbsDoctorAvaiablePeriod condition);
}