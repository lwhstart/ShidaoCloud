package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdepFile;

public interface SdepFileMapper extends IBatisMapper<SdepFile> {
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月25日
	 * @param uuid
	 * @return
	 */
	SdepFile selectByUuid(String uuid);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月26日
	 * @param uuid
	 * @param path
	 * @return
	 */
	List<SdepFile> selectByUuidAndPath(@Param("uuid")String uuid,@Param("path")String path);
}