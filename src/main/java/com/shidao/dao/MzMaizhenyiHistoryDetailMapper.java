package com.shidao.dao;

import java.util.List;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.MzMaizhenyiHistoryDetail;
import com.shidao.vo.MaizhenyiDetailVO;

public interface MzMaizhenyiHistoryDetailMapper extends IBatisMapper<MzMaizhenyiHistoryDetail> {

	public List<MaizhenyiDetailVO> getDetailsAnalysis(Integer historyId);
}