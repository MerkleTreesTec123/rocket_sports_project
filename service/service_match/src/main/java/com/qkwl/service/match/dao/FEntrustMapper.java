package com.qkwl.service.match.dao;

import com.qkwl.common.dto.entrust.FEntrust;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface FEntrustMapper {
	
	/**
	 * 删除委单
	 * @param fid 委单ID
	 * @return 
	 */
    int deleteByfId(BigInteger fid);

    /**
     * 更新委单
     * @param entrust 委单
     * @return
     */
    int updateEntrust(FEntrust entrust);
    
    /**
     * 查找委单
     * @param fid 委单ID
     * @return
     */
    FEntrust selectEntrust(@Param("fid") BigInteger fid);
    
    /**
     * 查找未成交买单
     * @param coinId 虚拟币ID
     * @param max 最大数目
     * @return 委单列表
     */
    List<FEntrust> selectGoingBuyEntrust(@Param("ftradeid") int ftradeid, @Param("max") int max);

    /**
     * 查找未成交买单
     * @param coinId 虚拟币ID
     * @param max 最大数目
     * @return 委单列表
     */
    List<FEntrust> selectGoingBuyEntrustByApi(@Param("ftradeid") int ftradeid, @Param("max") int max);

    /**
     * 查找未成交卖单
     * @param coinId 虚拟币ID
     * @param max 最大数目
     * @return 委单列表
     */
    List<FEntrust> selectGoingSellEntrust(@Param("ftradeid") int ftradeid, @Param("max") int max);

    /**
     * 查找未成交卖单
     * @param coinId 虚拟币ID
     * @param max 最大数目
     * @return 委单列表
     */
    List<FEntrust> selectGoingSellEntrustByApi(@Param("ftradeid") int ftradeid, @Param("max") int max);

    /**
     * 查找待撤单委单
     * @param coinId 虚拟币ID
     * @param max 最大数目
     * @return 委单列表
     */
    List<FEntrust> selectWaitCancelEntrust(@Param("ftradeid") int ftradeid, @Param("max") int max);

    /**
     * 查找待撤单委单
     * @param coinId 虚拟币ID
     * @param max 最大数目
     * @return 委单列表
     */
    List<FEntrust> selectHistoryEntrust(@Param("ftradeid") int ftradeid, @Param("max") int max);
}