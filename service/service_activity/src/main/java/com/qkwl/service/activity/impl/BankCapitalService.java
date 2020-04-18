package com.qkwl.service.activity.impl;

import com.qkwl.common.dto.Enum.CapitalOperationInOutTypeEnum;
import com.qkwl.common.dto.Enum.CapitalOperationInStatus;
import com.qkwl.common.dto.capital.FWalletCapitalOperationDTO;
import com.qkwl.common.util.Utils;
import com.qkwl.service.activity.dao.FWalletCapitalOperationMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户资产计算接口实现
 *
 * @author ZKF
 */
@Service
public class BankCapitalService {

    private static final Logger logger = LoggerFactory.getLogger(BankCapitalService.class);

    @Autowired
    private FWalletCapitalOperationMapper walletCapitalOperationMapper;

    /**
     * 取消人民币充值（过期的充值状态）
     */
    public boolean cancelCnyRecharge() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> param = new HashMap<>();
        List<Integer> statusList = new ArrayList<>();
        statusList.add(CapitalOperationInStatus.NoGiven);
        statusList.add(CapitalOperationInStatus.WaitForComing);
        param.put("fstatusList", statusList);
        param.put("finouttype", CapitalOperationInOutTypeEnum.IN.getCode());
        param.put("enddate", simpleDateFormat.format(new Date(Utils.getTimestamp().getTime() - (60 * 60 * 1000L))));

        List<FWalletCapitalOperationDTO> walletCapitalOperationDOList = walletCapitalOperationMapper.getWalletCapitalOperationStatusByParam(param);
        for (FWalletCapitalOperationDTO item : walletCapitalOperationDOList) {
            //判断状态
            if (!item.getFstatus().equals(CapitalOperationInStatus.NoGiven) && !item.getFstatus().equals(CapitalOperationInStatus.WaitForComing)) {
                continue;
            }
            item.setFstatus(CapitalOperationInStatus.Expired);
            item.setFupdatetime(Utils.getTimestamp());
            walletCapitalOperationMapper.updateStatusByPrimaryKey(item);
        }
        return true;
    }

}
