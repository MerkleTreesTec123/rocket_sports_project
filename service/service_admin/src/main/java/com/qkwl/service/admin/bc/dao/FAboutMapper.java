package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.web.FAbout;

import java.util.List;
import java.util.Map;

/**
 * 关于我们-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FAboutMapper {

    /**
     * 查询
     * @param fid 关于我们id
     * @return 关于我们实体
     */
    FAbout selectByPrimaryKey(Integer fid);

    /**
     * 更新
     * @param record 关于我们实体
     * @return 更新记录数
     */
    int updateByPrimaryKey(FAbout record);
    
    
    /**
     * 分页查询关于我们
     * @param map 参数map
     * @return	关于我们列表
     */
    List<FAbout> getAboutPageList(Map<String, Object> map);
    
    
    /**
     * 分页查询关于我们的总记录数
     * @param map 参数map
     * @return 关于我们列表总记录数
     */
    int countAboutByParam(Map<String, Object> map);
    
    /**
     * 查询
     * @param ftype 关于我们类型
     * @return 关于我们列表
     */
    List<FAbout> selectByType(int ftype);
}