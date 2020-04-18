package com.qkwl.service.activity.dao;

import com.qkwl.common.dto.user.FUserPriceclock;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FUserPriceclockMapper {
	
	/**
	 * 查找相符
	 * @param pclock
	 * @return
	 */
    List<FUserPriceclock> selectByCoin(FUserPriceclock pclock);
}