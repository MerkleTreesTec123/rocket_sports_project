package com.qkwl.service.common.mapper;

import com.qkwl.common.dto.wallet.UserCoinWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CommonUserCoinWalletMapper {

    @Update("UPDATE user_coin_wallet SET frozen = frozen + #{frozen},total = total + #{total},gmt_modified = #{gmtModified} WHERE id = #{id}")
    int changeFinance(UserCoinWallet wallet);

    @Select("SELECT * from user_coin_wallet WHERE id = #{id}")
    UserCoinWallet selectById(@Param("id") Integer id);

    @Select("SELECT * from user_coin_wallet WHERE uid = #{uid} AND coin_id = #{coinId}")
    UserCoinWallet selectByUidAndCoinId(@Param("uid") Integer uid,@Param("coinId") Integer coinId);
    



}
