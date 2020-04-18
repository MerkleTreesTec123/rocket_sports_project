package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FIdentityInfo;

/**
 * 后台身份信息管理接口
 * Created by ZKF on 2017/4/20.
 */
public interface IAdminIdentityService {

    /**
     * 分页查询
     */
    Pagination<FIdentityInfo> selectByPage(Pagination<FIdentityInfo> page, FIdentityInfo info);

    /**
     * 根据id查询身份信息
     */
    FIdentityInfo selectById(Integer fid);

    /**
     * 更新身份信息
     */
    Boolean updateIdentity(FIdentityInfo info);
}
