package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.coin.SystemCoinInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 币种信息
 *
 */
@Mapper
public interface SystemCoinInfoMapper {

    /**
     * 查询所有币种信息
     *
     * @return
     */
    List<SystemCoinInfo> selectAll();

    /******* Admin ********/

    /**
     * 添加币种信息
     * @param record
     * @return
     */
    int insert(SystemCoinInfo record);

    /**
     * 根据条件查询币种信息列表
     *
     * @param map
     *            参数map
     * @return 虚拟币列表
     */
    List<SystemCoinInfo> getSystemCoinInfoList(Map<String, Object> map);

    /**
     * 根据 ID 查询
     * @param id
     * @return
     */
    SystemCoinInfo selectByPrimaryKey(Integer id);

    /**
     * 后台查询币种列表数量，主要是为了分页
     * @param map
     *            参数map
     * @return 查询记录数
     */
    int getSystemCoinInfoCount(Map<String, Object> map);

    /**
     * 更新币种信息
     * @param coin 币种
     * @return 更新记录数
     */
    int updateSystemCoinInfo(SystemCoinInfo coin);

    /**
     * 根据主键删除币种信息
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);


}