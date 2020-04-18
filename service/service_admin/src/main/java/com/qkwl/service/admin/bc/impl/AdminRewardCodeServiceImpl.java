package com.qkwl.service.admin.bc.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qkwl.service.admin.bc.dao.SystemCoinTypeMapper;
import com.qkwl.common.dto.coin.SystemCoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.capital.FRewardCodeDTO;
import com.qkwl.common.rpc.admin.IAdminRewardCodeService;
import com.qkwl.service.admin.bc.dao.FRewardCodeMapper;

/**
 * 兑换码接口实现
 * @author ZKF
 */
@Service("adminRewardCodeService")
public class AdminRewardCodeServiceImpl implements IAdminRewardCodeService{

	@Autowired
	private FRewardCodeMapper rewardCodeMapper;
	@Autowired
	private SystemCoinTypeMapper systemCoinTypeMapper;
	
	/**
	 * 分页查询兑换码
	 * @param page 分页参数
	 * @param rc 实体参数
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminRewardCodeService#selectRewardCodePageList(com.qkwl.common.dto.common.Pagination, FRewardCodeDTO)
	 */
	@Override
	public Pagination<FRewardCodeDTO> selectRewardCodePageList(Pagination<FRewardCodeDTO> page, FRewardCodeDTO rc) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderField", page.getOrderField());
		map.put("orderDirection", page.getOrderDirection());
		
		map.put("ftype", rc.getFtype());
		map.put("fbatch", rc.getFbatch());
		map.put("fstate", rc.getFstate());
		map.put("floginname", rc.getFloginname());
		
		map.put("begindate", page.getParam().get("startCreateDate"));
		map.put("enddate", page.getParam().get("endCreateDate"));
		map.put("beginusedate", page.getParam().get("startUseDate"));
		map.put("endusedate", page.getParam().get("endUseDate"));

		int count = rewardCodeMapper.countRewardCodePageList(map);
		page.setTotalRows(count);
		if(count <= 0) {
			return page;
		}
		List<FRewardCodeDTO> rcList = rewardCodeMapper.getRewardCodePageList(map);
		for (FRewardCodeDTO fRewardCode : rcList) {
			if(fRewardCode.getFtype() != null){
				SystemCoinType coin = systemCoinTypeMapper.selectByPrimaryKey(fRewardCode.getFtype());
				if(coin != null){
					fRewardCode.setFtype_s(coin.getName());
				}else if(fRewardCode.getFtype()==0){
					fRewardCode.setFtype_s("人民币");
				}else{
					fRewardCode.setFtype_s("未知");
				}
			}
		}
		page.setData(rcList);
		return page;
	}

	/**
	 * 新增兑换码
	 * @param rc 兑换码实体
	 * @return 是否新增成功
	 * @see com.qkwl.common.rpc.admin.IAdminRewardCodeService#insertRewardCode(FRewardCodeDTO)
	 */
	@Override
	public boolean insertRewardCode(FRewardCodeDTO rc) {
		int i = rewardCodeMapper.insert(rc);
		return i > 0 ? true : false;
	}

	/**
	 * 批量新增兑换码
	 * @param list 兑换码列表
	 * @return 是否执行成功
	 * @see com.qkwl.common.rpc.admin.IAdminRewardCodeService#insertRewardCodeList(java.util.List)
	 */
	@Override
	public boolean insertRewardCodeList(List<FRewardCodeDTO> list) {
		int i = rewardCodeMapper.insertList(list);
		return i > 0 ? true : false;
	}

	/**
	 * 删除兑换码
	 * @param id 兑换码id
 	 * @return 是否删除成功
	 * @see com.qkwl.common.rpc.admin.IAdminRewardCodeService#deleteRewardCode(int)
	 */
	@Override
	public boolean deleteRewardCode(int id) {
		int i = rewardCodeMapper.deleteByPrimaryKey(id);
		return i > 0 ? true : false;
	}

	/**
	 * 查询用户兑换总量
	 * @param fuid 用户id
	 * @param type 币种
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 总量
	 * @see com.qkwl.common.rpc.admin.IAdminRewardCodeService#selectWalletTotalAmount(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date)
	 */
	@Override
	public BigDecimal selectWalletTotalAmount(Integer fuid, Integer type, Date start, Date end) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fuid", fuid);
		map.put("type", type);
		map.put("start", start);
		map.put("end", end);

		BigDecimal total = rewardCodeMapper.getTotalAmount(map);
		
		return total;
	}

}
