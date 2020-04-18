package com.qkwl.service.admin.bc.dao;


import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.coin.SystemCoinType;

import java.util.List;
import java.util.Map;

@Mapper
public interface SystemCoinTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SystemCoinType record);

    SystemCoinType selectByPrimaryKey(Integer id);

    List<SystemCoinType> selectAll();

    int updateByPrimaryKey(SystemCoinType record);

    /******* Admin ********/

    /**
     * 后台查询币种列表
     *
     * @param map
     *            参数map
     * @return 虚拟币列表
     */
    List<SystemCoinType> getSystemCoinTypeList(Map<String, Object> map);

    /**
     * 后台查询币种列表数量
     *
     * @param map
     *            参数map
     * @return 查询记录数
     */
    int getSystemCoinTypeCount(Map<String, Object> map);

    /**
     * 更新币状态
     * @param coin 币种
     * @return 更新记录数
     */
    int updateSystemCoinTypeStatus(SystemCoinType coin);

    /**
     * 更新币种信息
     * @param coin 币种
     * @return 更新记录数
     */
    int updateSystemCoinType(SystemCoinType coin);

    /**
     * 更新币钱包链接信息
     * @param coin 币种
     * @return 更新记录数
     */
    int updateSystemCoinTypeLink(SystemCoinType coin);

}