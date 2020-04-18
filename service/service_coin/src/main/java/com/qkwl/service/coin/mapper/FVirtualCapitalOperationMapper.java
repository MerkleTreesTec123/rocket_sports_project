package com.qkwl.service.coin.mapper;

import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 虚拟币充值提现记录
 * @author TT
 */
@Mapper
public interface FVirtualCapitalOperationMapper {
	
	/**
	 * 新增记录
	 * @param record
	 * @return
	 */
    int insert(FVirtualCapitalOperationDTO record);

    /**
     * 根据id查询记录
     * @param funiquenumber
     * @return
     */
    List<FVirtualCapitalOperationDTO> selectByTxid(@Param("funiquenumber") String funiquenumber);

    /**
     * 更新记录
     * @param record
     * @return
     */
    int updateByPrimaryKey(FVirtualCapitalOperationDTO record);

    /**
     * 查找未到账
     * @param fcoinid
     * @return
     */
    List<FVirtualCapitalOperationDTO> seletcGoing(@Param("fcoinid") int fcoinid, @Param("fconfirmations") int fconfirmations);

    /**
     * 查询记录总条数
     * @param map 参数map
     * @return 查询记录数
     */
    int countVirtualCapitalOperation(Map<String, Object> map);
}