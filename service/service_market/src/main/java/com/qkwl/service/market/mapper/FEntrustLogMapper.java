package com.qkwl.service.market.mapper;

import com.qkwl.common.dto.entrust.FEntrustLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 委单日志
 * @author TT
 */
@Mapper
public interface FEntrustLogMapper {

	/**
	 * 查询
	 * @param tradeId
	 * @param time
	 * @return
	 */
	List<FEntrustLog> selectByDate(@Param("ftradeid") int tradeId, @Param("time") String time);
	
	/**
	 * 查询
	 * @param tradeId
	 * @param max
	 * @return
	 */
	List<FEntrustLog> selectSuccess(@Param("ftradeid") int tradeId, @Param("max") int max);
	
	/**
	 * 查询24内总量,最高价,最低价
	 * @param tradeId
	 * @param time
	 * @return
	 */
	Map<String, Object> select24ByDate(@Param("ftradeid") int tradeId, @Param("time") String time);
	
	/**
	 * 查询开盘价
	 * @param tradeId 交易ID
	 * @param nowZero 今日0点
	 * @param yesterZero 昨日零点
	 * @return
	 */
	FEntrustLog selectKaiByData(@Param("ftradeid") int tradeId, @Param("nowZero") String nowZero, @Param("yesterZero") String yesterZero);
	
	/**
	 * 查询最新成交价
	 * @param tradeId
	 * @return
	 */
	FEntrustLog selectLastByData(@Param("ftradeid") int tradeId);
}