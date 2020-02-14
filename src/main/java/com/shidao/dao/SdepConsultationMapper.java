package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdepConsultation;

/**
 * @author liupengyuan
 *
 */
public interface SdepConsultationMapper extends IBatisMapper<SdepConsultation>{
    void deleteByPrimaryKey(Integer id);
    
    /**
     * 查看本帖详情
     * @param id
     * @return
     */
    List<SdepConsultation> listDetailById(Integer id);
    
    SdepConsultation getProblem(Integer id);
    List<SdepConsultation> getOneConsultationDetail(@Param("id") Integer id,@Param("customerId")Integer customerId,@Param("maxId") Integer maxId);
    
    /**
     * 顾客查询自己的咨询信息列表
     * @param customerId
     * @return
     */
    List<SdepConsultation> listConsultAboutCustomer(SdepConsultation consultation);
    
    /**
     * 看诊系统，咨询列表
     * @param consultation
     * @return
     */
    List<SdepConsultation> listConsultAboutEmployee(SdepConsultation consultation);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年7月13日
     * 功能:门店咨询列表
     * @param consultation
     * @return
     */
    List<SdepConsultation> listConsultAboutClub(SdepConsultation consultation);
    
    /**
     * 医生的咨询服务
     * @param consultation
     * @return
     */
    List<SdepConsultation> getDetailAboutEmployee(SdepConsultation consultation);
    
    /**
     * 医生更新标记为已读
     * @param id
     */
    void readQuestion(Integer id);
    
    /**
     * 顾客更新标记为已读
     * @param id
     */
    void readAnswer(Integer id);
    
    /**
     * 获得未读咨询的数量
     * @author yzl 2018年9月29日
     * @param customerId 客户编号
     * @return 未读咨询的数量
     */
    Integer getCustomerUnreadCount(Integer customerId);
}