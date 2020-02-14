package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdepPayMethod;

/** 
* @author 作者 zzp: 
* @version 创建时间：2019年8月6日 下午1:49:21 
* 类说明 
*/
public interface SdepPayMethodMapper extends IBatisMapper<SdepPayMethod> {

	public List<String> selectAllMethodName();
	
	/**
	 * 判断支付方式名字是否使用过
	 * @param name
	 * @param clubId
	 * @return
	 * @author yjj 2019年9月27日
	 */
	boolean isNameUsed(@Param("clubId")Integer clubId,@Param("name")String name);
	
	/**
	  *  删除支付方式
	 * @param uuid
	 * @author yjj 2019年9月30日
	 */
	void deleteByUuid(String uuid);
	
	/**
	  * 通过 uuid查询支付方式记录是否存在
	 * @retuuid
	 * @pararn
	 * @author yjj 2019年10月9日
	 */
	SdepPayMethod selectByUuid(String uuid);
	
	/**
	  *  查询当前支付方式是否被使用中
	 * @param uuid
	 * @author yjj 2019年10月8日
	 */
	boolean isPayMethodUsed(String uuid);
	
	
}
