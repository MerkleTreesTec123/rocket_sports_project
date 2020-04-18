package com.qkwl.service.entrust.dao;

import com.qkwl.service.entrust.model.EntrustDO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 当前委单数据操作接口
 *
 * @author LY
 */
@Mapper
public interface FEntrustMapper {

    /**
     * 插入委单
     *
     * @param entrust 委单实体对象
     * @return 成功条数
     */
    int insert(EntrustDO entrust);

    /**
     * 更新委单
     *
     * @param entrust 委单实体对象
     * @return 成功条数
     */
    int updateByfId(EntrustDO entrust);

    /**
     * 根据ID获取委单
     *
     * @param uId 用户ID
     * @param Id  委单ID
     * @return 委单实体对象
     */
    EntrustDO selectById(@Param("fuid") int uId, @Param("fid") BigInteger Id);

    /**
     * 根据ID获取委单(带锁)
     *
     * @param uId 用户ID
     * @param Id  委单ID
     * @return 委单实体对象
     */
    EntrustDO selectByIdLocal(@Param("fuid") int uId, @Param("fid") BigInteger Id);

    /**
     * 分页查询委单
     *
     * @param map 条件MAP
     * @return 委单实体对象列表
     */
    List<EntrustDO> selectPageList(Map<String, Object> map);

    /**
     * 分页查询委单条数
     *
     * @param map 条件MAP
     * @return 委单数量
     */
    int selectPageCount(Map<String, Object> map);
}