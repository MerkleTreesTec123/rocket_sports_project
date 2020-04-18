package com.qkwl.service.user.dao;

import com.qkwl.service.user.model.FRewardCodeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 兑换券数据操作接口
 * @author ZKF
 */
@Mapper
public interface FRewardCodeMapper {

    /**
     * 根据id查询兑换券
     * @param fid 主键ID
     * @return 实体对象
     */
    FRewardCodeDO selectByPrimaryKey(Integer fid);

    /**
     * 更新兑换券
     * @param record 实体对象
     * @return 成功条数
     */
    int updateByPrimaryKey(FRewardCodeDO record);
    
    /**
     * 根据实体查询兑换券
     * @param map 条件MAP
     * @return 实体对象列表
     */
    List<FRewardCodeDO> selectListByCode(Map<String, Object> map);

    /**
     * 根据实体查询兑换券的总记录数
     * @param map 条件ＭＡＰ
     * @return　记录总数
     */
    int countListByCode(Map<String, Object> map);
    
    /**
     * 根据兑换码查询兑换券
     * @param code　兑换码
     * @return　实体对象
     */
    FRewardCodeDO selectByCode(@Param("fcode") String code);
    
    /**
     * 根据用户查询兑换券使用次数
     * @param map　条件MAP
     * @return　使用次数
     */
    int countUseCodeByUser(Map<String, Object> map);
    
}