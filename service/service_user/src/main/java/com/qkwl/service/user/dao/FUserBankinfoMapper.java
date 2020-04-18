package com.qkwl.service.user.dao;


import com.qkwl.service.user.model.FUserBankinfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户银行卡数据操作接口
 * @author ZKF
 */
@Mapper
public interface FUserBankinfoMapper {
	
    /**
     * 新增用户银行卡
     * @param record 实体对象
     * @return 成功条数
     */
    int insert(FUserBankinfoDO record);

    /**
     * 删除用户银行卡
     * @param fid   主键id
     * @param fuid  用户id
     * @return
     */
    int delete(@Param("fid") int fid, @Param("fuid") int fuid);

    /**
     * 根据id查询用户银行卡
     * @param fid 主键ID
     * @return 实体对象
     */
    FUserBankinfoDO selectByPrimaryKey(Integer fid);

    /**
     * 更新用户银行卡
     * @param record 实体对象
     * @return 成功条数
     */
    int updateByPrimaryKey(FUserBankinfoDO record);

    /**
     * 根据用户和类型查询银行卡
     * @param fuid 用户ID
     * @param ftype 银行卡类型
     * @return 实体对象列表
     */
    List<FUserBankinfoDO> getBankInfoListByUser(@Param("fuid") int fuid, @Param("ftype") Integer ftype);

    /**
     * 根据实体查询银行卡
     * @param fBankinfo 实体对象
     * @return 实体对象列表
     */
    List<FUserBankinfoDO> getBankInfoListByBankInfo(FUserBankinfoDO fBankinfo);

    /**
     * 根据参数查询银行卡个数
     * @param map 实体对象
     * @return 实体对象列表
     */
    Integer getBankInfoByCount(Map<String, Object> map);

    
}