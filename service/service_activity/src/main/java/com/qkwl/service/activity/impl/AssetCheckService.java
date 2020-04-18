package com.qkwl.service.activity.impl;

import com.qkwl.common.dto.capital.FWalletCapitalOperationDTO;
import com.qkwl.service.activity.dao.FWalletCapitalOperationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 资产检查
 */
@Service("assetCheckService")
public class AssetCheckService {
    private static final Logger logger = LoggerFactory.getLogger(AssetCheckService.class);
    @Autowired
    private FWalletCapitalOperationMapper walletCapitalOperationMapper;
    /**
     * 根据参数查询记录
     * @param param
     * @return
     */
    public List<FWalletCapitalOperationDTO> listWalletCapitalOperation(Map<String,Object> param){
        return walletCapitalOperationMapper.getWalletCapitalOperationByParam(param);
    }


}
