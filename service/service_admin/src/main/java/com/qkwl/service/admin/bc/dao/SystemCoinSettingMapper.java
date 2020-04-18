package com.qkwl.service.admin.bc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.coin.SystemCoinSetting;
import org.apache.ibatis.annotations.Param;

/**
 * 币种等级设置数据操作接口
 * @author LY
 *
 */
@Mapper
public interface SystemCoinSettingMapper {

	/**
	 * 插入
	 * @param record
	 * @return
	 */
    int insert(SystemCoinSetting record);

    /**
     * 查询
     * @return
     */
    List<SystemCoinSetting> selectAll();

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByPrimaryKey(SystemCoinSetting record);
    
    /**
     * 根据等级和币种查询
     * @param coinId
     * @param levelVip
     * @return
     */
    SystemCoinSetting selectSystemCoinSetting(@Param("coinId") Integer coinId, @Param("levelVip") Integer levelVip);
    
    /**
     * 根据币种ID查询设置
     * @param coinId
     * @return
     */
    List<SystemCoinSetting> selectListByCoinId(@Param("coinId") Integer coinId);
}