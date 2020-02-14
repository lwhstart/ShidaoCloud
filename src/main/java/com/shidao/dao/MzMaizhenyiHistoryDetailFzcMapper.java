package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.MzMaizhenyiHistoryDetailFzc;

public interface MzMaizhenyiHistoryDetailFzcMapper extends IBatisMapper<MzMaizhenyiHistoryDetailFzc>{
	
    void deleteByPrimaryKey(Integer id);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年3月13日<br>
     * 功能:删除HISTORY_ID， QIMAI，PULSE_CHECK_METHOD 这三个列一样的记录
     */
    void deleteDuplicate(MzMaizhenyiHistoryDetailFzc condition);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年3月14日<br>
     * 功能:根据historyId，qimai，pulseCheckMethod
     * @param historyId
     * @param qimai
     * @param pulseCheckMethod
     * @return
     */
    MzMaizhenyiHistoryDetailFzc selectByThreeKey(@Param("historyId")Integer historyId,@Param("qimai")String qimai,@Param("pulseCheckMethod")String pulseCheckMethod);

}