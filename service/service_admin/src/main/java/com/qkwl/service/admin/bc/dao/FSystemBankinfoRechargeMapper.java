package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.system.FSystemBankinfoRecharge;

import java.util.List;
import java.util.Map;

/**
 * 平台充值银行卡-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FSystemBankinfoRechargeMapper {
	
    /**
     * 插入
     * @param record 银行卡实体
     * @return 插入记录数
     */
    int insert(FSystemBankinfoRecharge record);

    /**
     * 查询
     * @param fid 银行卡id
     * @return 银行卡实体
     */
    FSystemBankinfoRecharge selectByPrimaryKey(Integer fid);

    /**
     * 更新
     * @param record 银行卡实体
     * @return 更新记录数
     */
    int updateByPrimaryKey(FSystemBankinfoRecharge record);
   
    
    /**
     * 查询
     * @param type 银行卡类型
     * @return 银行卡列表
     */
    List<FSystemBankinfoRecharge> selectByType(int type);
    
    /**
     * 根据状态查询银行卡
     * @param fstatus 状态
     * @return 银行卡列表
     */
    List<FSystemBankinfoRecharge> selectAllByStatus(int fstatus);
    
    /**
     * 分页查询充值银行信息
     * @param map 参数map
     * @return 银行卡列表
     */
    List<FSystemBankinfoRecharge> getBankPageList(Map<String, Object> map);
    
    /**
     * 分页查询充值银行信息数量
     * @param map 参数map
     * @return 查询记录数
     */
    int countBankByParam(Map<String, Object> map);
    
    /**
     * 根据状态查询银行卡
     */
    List<FSystemBankinfoRecharge> selectAllByStart(int start);
    
}