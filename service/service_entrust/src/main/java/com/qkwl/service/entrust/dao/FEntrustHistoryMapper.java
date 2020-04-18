package com.qkwl.service.entrust.dao;

import com.qkwl.service.entrust.model.EntrustHistoryDO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 历史委单数据操作接口
 *
 * @author LY
 */
@Mapper
public interface FEntrustHistoryMapper {

    /**
     * 根据ID获取历史委单
     *
     * @param Id  委单ID
     * @param uId 用户id
     * @return 历史委单实体对象
     */
    EntrustHistoryDO selectById(@Param("fuid") int uId, @Param("fid") BigInteger Id);

    /**
     * 根据真正的委单ID获取历史委单
     *
     * @param Id  委单ID
     * @param uId 用户id
     * @return 历史委单实体对象
     */
    EntrustHistoryDO selectByEntrustId(@Param("fuid") int uId, @Param("fentrustid") BigInteger Id);

    /**
     * 分页查询委单
     *
     * @param map 条件MAP
     * @return 委单实体对象列表
     */
    List<EntrustHistoryDO> selectPageList(Map<String, Object> map);

    /**
     * 分页查询委单条数
     *
     * @param map 条件MAP
     * @return 委单数量
     */
    int selectPageCount(Map<String, Object> map);
}