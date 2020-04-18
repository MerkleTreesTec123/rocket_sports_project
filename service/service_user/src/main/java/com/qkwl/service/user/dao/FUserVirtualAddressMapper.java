package com.qkwl.service.user.dao;

import com.qkwl.service.user.model.FUserVirtualAddressDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户虚拟币地址数据操作接口
 *
 * @author ZKF
 */
@Mapper
public interface FUserVirtualAddressMapper {

    /**
     * 根据用户和币种查询提现地址
     *
     * @param fuid    用户ID
     * @param fcoinid 币种ID
     * @return 实体对象
     */
    FUserVirtualAddressDO selectByUserAndCoin(@Param("fuid") Integer fuid, @Param("fcoinid") Integer fcoinid);

    /**
     * 根据地址和币种查询提现地址
     *
     * @param fcoinid   币种ID
     * @param fadderess 地址
     * @return 实体对象
     */
    FUserVirtualAddressDO selectByCoinAndAddress(@Param("fcoinid") Integer fcoinid, @Param("fadderess") String fadderess);

    /**
     * 根据用户查询用户的所有地址
     *
     * @param fuid 用户ID
     * @return 实体对象列表
     */
    List<FUserVirtualAddressDO> selectByUser(Integer fuid);

    /**
     * 新增用户的所有地址
     *
     * @param record 实体对象
     * @return 成功条数
     */
    int insert(FUserVirtualAddressDO record);

    /**
     * 根据地址查询用户
     */
    FUserVirtualAddressDO selectByAddress(@Param("faddress") String faddress);

}