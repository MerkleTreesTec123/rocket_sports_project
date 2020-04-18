package com.qkwl.service.admin.bc.impl;

import com.qkwl.common.util.Utils;
import com.qkwl.service.admin.bc.dao.FUserFinancesMapper;
import com.qkwl.service.admin.bc.dao.UserCoinWalletMapper;
import com.qkwl.common.dto.Enum.UserFinancesStateEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FUserFinancesDTO;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.admin.IAdminUserFinances;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("adminUserFinances")
public class AdminUserFinancesService implements IAdminUserFinances {

    @Autowired
    private FUserFinancesMapper userFinancesMapper;
    @Autowired
    private UserCoinWalletMapper userCoinWalletMapper;

    public Pagination<FUserFinancesDTO> selectUserFinancesByPage(Pagination<FUserFinancesDTO> page,
                                                                 FUserFinancesDTO userFinances) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        map.put("keyword", page.getKeyword());
        map.put("orderField", page.getOrderField());
        map.put("orderDirection", page.getOrderDirection());

        map.put("fuid", userFinances.getFuid());
        map.put("fcoinid", userFinances.getFcoinid());
        map.put("fstate", userFinances.getFstate());
        map.put("fcreatetime", userFinances.getFcreatetime());

        int count = userFinancesMapper.selectUserFinancesCount(map);
        if(count > 0) {
            List<FUserFinancesDTO> list = userFinancesMapper.selectUserFinancesList(map);
            page.setData(list);
        }
        page.setTotalRows(count);

        return page;
    }

    @Override
    public FUserFinancesDTO selectUserFinancesBalance(Integer fuid, Integer fcoinid, Integer fstate) {
        return userFinancesMapper.selectUserFinancesBalance(fuid, fcoinid, fstate);
    }

    @Override
    public List<Map<String, Object>> selectUserFinancesTotal() {
        return userFinancesMapper.selectUserFinancesTotal();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean updateUserFinances(Integer fid) throws Exception {
        FUserFinancesDTO userFinances = userFinancesMapper.select(fid);
        if (userFinances == null) {
            return false;
        }
        if (userFinances.getFstate().equals(UserFinancesStateEnum.SEND.getCode())) {
            return false;
        }
        if (userFinances.getFstate().equals(UserFinancesStateEnum.CANCEL.getCode())) {
            return false;
        }
        if (userFinances.getFstate().equals(UserFinancesStateEnum.REDEEM.getCode())) {
            return false;
        }
        UserCoinWallet wallet = userCoinWalletMapper.selectByUidAndCoinLock(userFinances.getFuid(), userFinances.getFcoinid());
        if (wallet == null) {
            return false;
        }
        if (wallet.getBorrow().compareTo(userFinances.getFamount()) < 0) {
            throw new Exception("理财冻结余额不足！");
        }
        // 计算赎回收益
        Date curDate = new Date();
        Integer planDays = Utils.daysBetween(userFinances.getFcreatetime(), userFinances.getFupdatetime());
        BigDecimal dayAmount = MathUtils.div(userFinances.getFplanamount(), new BigDecimal(planDays));
        Integer realDays = Utils.daysBetween(userFinances.getFcreatetime(), curDate);
        BigDecimal planAmount = MathUtils.toScaleNum(MathUtils.mul(dayAmount, new BigDecimal(realDays)), MathUtils.DEF_FEE_SCALE);
        // 更新钱包
        wallet.setTotal(MathUtils.add(wallet.getTotal(), MathUtils.add(userFinances.getFamount(),planAmount)));
        wallet.setBorrow(MathUtils.sub(wallet.getBorrow(), userFinances.getFamount()));
        wallet.setGmtModified(new Date());
        if (userCoinWalletMapper.updateByPrimaryKey(wallet) <= 0) {
            return false;
        }
        userFinances.setFplanamount(planAmount);
        userFinances.setFstate(UserFinancesStateEnum.REDEEM.getCode());
        userFinances.setFupdatetime(new Date());
        if (userFinancesMapper.update(userFinances) <= 0) {
            throw new Exception();
        }
        return true;
    }

    @Override
    public FUserFinancesDTO selectUserFinances(Integer fid) {
        return userFinancesMapper.select(fid);
    }

}
