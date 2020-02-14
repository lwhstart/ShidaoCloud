package com.shidao.dao;

import java.util.List;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.TcmRelationQimaiZangfu;
import com.shidao.model.TcmSyndrome;

/**
 * 
 *@author yzl 2019年11月21日
 *
 */
public interface TcmSyndromeMapper extends IBatisMapper<TcmSyndrome> {
	List<TcmRelationQimaiZangfu> listQimaiZangfu();
}