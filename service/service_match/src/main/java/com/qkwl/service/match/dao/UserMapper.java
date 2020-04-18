package com.qkwl.service.match.dao;

import com.qkwl.common.dto.user.FUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    FUser getIntroByUID(Integer uid);

}
