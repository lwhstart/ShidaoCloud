package com.shidao.dao;

import java.util.List;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.TcmMaizhen;
import com.shidao.model.TcmPulseSolution;

public interface TcmMaizhenMapper extends IBatisMapper<TcmMaizhen> {

	public TcmMaizhen selectByMai(String mai);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月18日<br>
	 * 功能:
	 * @param pulseCode
	 * @return
	 */
	TcmMaizhen selectByPulseCode(String pulseCode);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月26日<br>
	 * 功能:五位一体
	 * @param pulseCode
	 * @return
	 */
	List<TcmPulseSolution> listPulseSolutionByPulseCode(String pulseCode);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月28日<br>
	 * 功能:五位一体
	 * @param mai
	 * @return
	 */
	List<TcmPulseSolution> listPulseSolutionByMai(String yunmai);
	
	/*void insertPulseSolution(TcmPulseSolution pulseSolution);*/
}
