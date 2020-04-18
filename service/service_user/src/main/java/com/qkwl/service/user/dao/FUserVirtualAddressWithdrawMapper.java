package com.qkwl.service.user.dao;

import com.qkwl.service.user.model.FUserVirtualAddressWithdrawDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户虚拟币提现地址数据操作接口
 * @author ZKF
 */
@Mapper
public interface FUserVirtualAddressWithdrawMapper {
	
    /**
     * 新增地址
     * @param record 实体对象
     * @return 成功条数
     */
    int insert(FUserVirtualAddressWithdrawDO record);
    
    /**
     * 更新地址
     * @param record 实体对象
     * @return 成功条数
     */
    int updateByPrimaryKey(FUserVirtualAddressWithdrawDO record);
    
    /**
     * 根据实体查询地址列表
     * @param address 实体对象
     * @return 实体对象列表
     */
    List<FUserVirtualAddressWithdrawDO> getVirtualCoinWithdrawAddressList(FUserVirtualAddressWithdrawDO address);
    
    /**
     * 根据id查询地址
     * @param fid 主键ID
     * @return 实体对象
     */
    FUserVirtualAddressWithdrawDO selectByPrimaryKey(Integer fid);
}