package com.qkwl.service.user.dao;


import com.qkwl.service.user.model.FPoolDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 虚拟地址数据操作接口
 *
 * @author ZKF
 */
@Mapper
public interface FPoolMapper {
    /**
     * 查询一个充值地址
     *
     * @param fcoinid 币种ID
     * @return 实体对象
     */
    FPoolDO selectOneFpool(Integer fcoinid);

    /**
     * 更新充值地址为已使用状态（0-未使用，1-已使用）
     *
     * @param fpool 实体对象
     * @return 成功条数
     */
    int updatePoolStatus(FPoolDO fpool);

}