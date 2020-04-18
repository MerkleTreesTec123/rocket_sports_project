package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.exceptions.BCException;

import java.util.List;

public interface IAdminApiAuthService {

    FApiAuth insertAuth(Integer fuid) throws BCException;

    List<FApiAuth> selectFApiAuthListByUID(Integer fuid);

    List<FApiAuth> selectFApiAuthListByID(Integer fid);

    List<FApiAuth> selectAll();

    int updateByUser(FApiAuth api);


}
