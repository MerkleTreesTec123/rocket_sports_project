package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.api.FApiAuth;

import java.util.List;

/**
 * api授权-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FApiAuthMapper {

    /**
     * 增加api
     *
     * @param record
     *            API实体对象
     * @return 成功条数
     */
    int insert(FApiAuth record);

    /**
     * 查询api
     * @param fuid 用户id
     * @return api账户列表
     */
    List<FApiAuth> selectByPage(Integer fuid);

    
    /**
     * 更新api
     * @param api api账户实体
     * @return 更新记录数
     */
    int updateByUser(FApiAuth api);
    
    
    /**
     * 查询api
     * @param fid api账户id
     * @return api账户实体
     */
    FApiAuth selectByPrimaryKey(Integer fid);

    /**
     * 获取所有的授权
     * @return
     */
    List<FApiAuth> selectAll();
}