/**
 * AutoLeanMapper.java
 * Created by :yzl. Created date: 2019年11月6日
 */
package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.shidao.vo.AutoLearnPrescriptionVO;

/**
 *@author yzl 2019年11月6日
 * 
 */

@Repository
public interface AutoLearnMapper {
	List<AutoLearnPrescriptionVO> listPrescription(@Param("diseases") Integer[] diseases, @Param("syndromes")Integer[] syndromes);
}
