package com.qkwl.service.match.dao;

import com.qkwl.common.dto.pkg.UserPackage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserPackageMapper {

    int changeUserPackage(UserPackage userPackage);

    UserPackage getUserPackage(@Param("uid") int uid);

}
