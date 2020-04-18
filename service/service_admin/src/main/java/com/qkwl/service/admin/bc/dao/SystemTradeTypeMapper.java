package com.qkwl.service.admin.bc.dao;


import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.coin.SystemTradeType;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/*
 * 交易信息数据操作接口
 */
@Mapper
public interface SystemTradeTypeMapper {
    /**
     * 删除
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入
     * @param record
     * @return
     */
    int insert(SystemTradeType record);

    /**
     * 查找
     * @param id
     * @return
     */
    SystemTradeType selectByPrimaryKey(Integer id);

    /**
     * 查询所有
     * @return
     */
    List<SystemTradeType> selectAll();

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByPrimaryKey(SystemTradeType record);

    /**
     * 分页查询列表
     * @param map
     * @return
     */
    List<SystemTradeType> selectSystemTradeTypeList(Map<String, Object> map);

    /**
     * 分页查询条数
     * @param map
     * @return
     */
    int selectSystemTradeTypeCount(Map<String, Object> map);

    /**
     * 根据 sellCoinId 和 buyCoinId 查询交易
     * @param sellCoinId
     * @param buyCoinId
     * @return
     */
    SystemTradeType selectTradeType(@Param("sellCoinId") Integer sellCoinId,@Param("buyCoinId") Integer buyCoinId);
}