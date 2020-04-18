package com.qkwl.service.user.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.wallet.UserCoinWallet;

import java.util.List;

import org.apache.ibatis.annotations.Param;



/**
 * 用户钱包数据操作接口
 * @author ZKF
 */
@Mapper
public interface UserCoinWalletMapper {
	
	/**
	 * 插入数据
	 * @param record 实体对象
	 * @return 成功条数
	 */
    int insert(UserCoinWallet record);

    /**
     * 更新
     * @param record 实体对象
     * @return 成功条数
     */
    int updateByPrimaryKey(UserCoinWallet record);
    
    /**
     * 获取用户所有钱包
     * @param uid 用户ID
     * @return 实体对象列表
     */
    List<UserCoinWallet> selectByUid(@Param("uid") Integer uid);

    /**
     * 修改时查询(无锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserCoinWallet selectByUidAndCoin(@Param("uid") Integer uid, @Param("coinId") Integer coinId);
    
    /**
     * 修改时查询(带行级锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserCoinWallet selectByUidAndCoinLock(@Param("uid") Integer uid, @Param("coinId") Integer coinId);

    /**
     * 查询 e_ieo_buy_log 表
     * @param uid
     * @param coinid
     * @return 
     */
    UserCoinWallet selectIeoBuyLog(@Param("uid") Integer uid,@Param("coinId") Integer coinId);

    /**
     * 查询 e_ieo_invite_reward 表
     * 
     * @param uid
     * @param coinId
     * @return
     */
    UserCoinWallet selectIeoInviteReward(@Param("uid") Integer uid,@Param("coinId") Integer coinId);

    

}
