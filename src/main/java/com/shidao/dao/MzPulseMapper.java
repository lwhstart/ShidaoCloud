package com.shidao.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;


@Repository
public interface MzPulseMapper {
	
    List<Map<String, String>> getYunmai();
    
    List<String> getPulses();
}