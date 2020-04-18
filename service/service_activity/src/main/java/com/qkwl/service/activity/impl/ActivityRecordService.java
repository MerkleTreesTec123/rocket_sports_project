package com.qkwl.service.activity.impl;

import com.qkwl.common.dto.Enum.ReferrerRecordStateEnum;
import com.qkwl.common.dto.activity.FActivityRecord;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.Utils;
import com.qkwl.service.activity.dao.FActivityRecordMapper;
import com.qkwl.service.activity.dao.UserCoinWalletMapper;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service("activityRecordService")
public class ActivityRecordService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ActivityRecordService.class);

    @Autowired
    private FActivityRecordMapper activityRecordMapper;

    //@Autowired
    //private UserCoinWalletMapper userCoinWalletMapper;

   // @Transactional(isolation = Isolation.REPEATABLE_READ, propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateWork() throws Exception {
//        Map<String,Object> param = new HashedMap();
//        param.put("orderField", "fcreatetime");
//        param.put("orderDirection", "desc");
//        param.put("fstate", ReferrerRecordStateEnum.Non_Release.getCode());
//        List<FActivityRecord> activityRecordList = activityRecordMapper.selectActivityRecordList(param);
//        for(FActivityRecord record : activityRecordList){
//            if(!record.getFstate().equals(ReferrerRecordStateEnum.Non_Release.getCode())){
//                continue;
//            }
//            //查询钱包
//            UserCoinWallet wallet = userCoinWalletMapper.selectLock(record.getFuid(),record.getFcoinid());
//            if(wallet == null){
//                logger.error("用户ID:{} 的钱包ID:{} 没有查询到",record.getFuid(),record.getFcoinid());
//                continue;
//            }
//            wallet.setTotal(MathUtils.add(wallet.getTotal(), record.getFamount()));
//            wallet.setGmtModified(Utils.getTimestamp());
//            if(userCoinWalletMapper.update(wallet) <= 0){
//                throw new Exception("更新钱包失败");
//            }
//            record.setFstate(ReferrerRecordStateEnum.Released.getCode());
//            if(activityRecordMapper.updateStatusByPrimaryKey(record) <= 0){
//                throw new Exception("更新奖励记录失败");
//            }
//        }
    }


}
