package com.qkwl.service.user.impl;

import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.dto.Enum.UserFinancesStateEnum;
import com.qkwl.common.dto.capital.UserFinancesOrderDTO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FUserFinancesDTO;
import com.qkwl.common.dto.finances.FVirtualFinances;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.pre.PreValidationHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserFinancesService;
import com.qkwl.common.util.PojoConvertUtil;
import com.qkwl.common.util.Utils;
import com.qkwl.service.common.mapper.UserCommonMapper;
import com.qkwl.service.user.dao.FUserFinancesMapper;
import com.qkwl.service.user.dao.UserCoinWalletMapper;
import com.qkwl.service.user.model.FUserFinancesDO;
import com.qkwl.service.user.tx.FinancesServiceTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userFinancesService")
public class UserFinancesServiceImpl implements IUserFinancesService {

    private static final Logger logger = LoggerFactory.getLogger(UserFinancesServiceImpl.class);

    @Autowired
    private FUserFinancesMapper userFinancesMapper;
    @Autowired
    private UserCoinWalletMapper userCoinWalletMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private FinancesServiceTx financesService;
    @Autowired
    private PreValidationHelper preValidationHelper;
    @Autowired
    private ValidationCheckHelper validationCheckHelper;
    @Autowired
    private UserCommonMapper userCommonMapper;

    @Override
    public Result createUserFinancesOrder(UserFinancesOrderDTO order) {
        if (order.getUserId() == null) {
            return Result.param("userId is null");
        }
        if (order.getCoinId() == null) {
            return Result.param("coinId is null");
        }
        if (order.getFinancesId() == null) {
            return Result.param("financesId is null");
        }
        if (order.getCount() == null || order.getCount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.param("count is null");
        }
        if (StringUtils.isEmpty(order.getPhoneCode()) && StringUtils.isEmpty(order.getGoogleCode())) {
            return Result.param("phoneCode and googleCode is null");
        }
        if (StringUtils.isEmpty(order.getTradePass())) {
            return Result.param("tradePass is null");
        }
        if (StringUtils.isEmpty(order.getIp())) {
            return Result.param("ip is null");
        }
        if (order.getPlatformId() == null) {
            return Result.param("platformId is null");
        }
        order.setCount(MathUtils.toScaleNum(order.getCount(), MathUtils.ENTER_COIN_SCALE));
        FUser user = userCommonMapper.selectOneById(order.getUserId());
        if (user == null) {
            return Result.param("user is not found");
        }
        Result preResult = preValidationHelper.validateCapital(user, "coin");
        if (!preResult.getSuccess()) {
            return preResult;
        }
        FVirtualFinances virtualFinances = redisHelper.getVirtualFinances(order.getFinancesId(), order.getCoinId());
        if (virtualFinances == null) {
            return Result.failure(1000, "存币类型不存在");
        }
        UserCoinWallet wallet = userCoinWalletMapper.selectByUidAndCoin(order.getUserId(), order.getCoinId());
        if (wallet == null) {
            if (logger.isErrorEnabled()) {
                logger.error("wallt is null  userId:{},coinId:{} ", order.getUserId(), order.getCoinId());
            }
            return Result.failure(10118, "余额不足");
        }
        if (wallet.getTotal().compareTo(order.getCount()) < 0) {
            return Result.failure(10118, "余额不足");
        }
        Result validateResult = validationCheckHelper.getCapitalCheck(user, order.getPhoneCode(), BusinessTypeEnum.SMS_FINANCES.getCode(),
                order.getGoogleCode(), order.getTradePass(), order.getIp(), order.getPlatformId());
        if (!validateResult.getSuccess()) {
            return validateResult;
        }
        BigDecimal fplanamount = MathUtils.toScaleNum(MathUtils.mul(order.getCount(), virtualFinances.getFrate()), MathUtils.DEF_FEE_SCALE);
        Date fsendtime = Utils.getCurTimestamp(virtualFinances.getFdays());
        FUserFinancesDO userFinances = new FUserFinancesDO();
        userFinances.setFamount(order.getCount());
        userFinances.setFcoinid(order.getCoinId());
        userFinances.setFcreatetime(new Date());
        userFinances.setFname(virtualFinances.getFname());
        userFinances.setFplanamount(fplanamount);
        userFinances.setFrate(virtualFinances.getFrate());
        userFinances.setFupdatetime(fsendtime);
        userFinances.setFstate(UserFinancesStateEnum.FROZEN.getCode());
        userFinances.setFuid(order.getUserId());
        userFinances.setVersion(0);
        try {
            if (financesService.createUserFinancesOrder(userFinances)) {
                return Result.success("存币订单创建成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("createUserFinancesOrder {} ", order.toString());
        }
        return Result.failure("存币订单创建失败");
    }

    @Override
    public Result cancleUserFinancesOrder(Integer id, Integer userId) {
        if (id == null) {
            return Result.param("id is null");
        }
        FUserFinancesDO userFinances = userFinancesMapper.select(id);
        if (userFinances == null || !userFinances.getFuid().equals(userId)) {
            return Result.failure(1000, "存币记录不存在");
        }
        if (userFinances.getFstate().equals(UserFinancesStateEnum.SEND.getCode())) {
            return Result.failure(1001, "存币记录已发放");
        }
        if (userFinances.getFstate().equals(UserFinancesStateEnum.CANCEL.getCode())) {
            return Result.failure(1002, "存币记录已取消");
        }
        userFinances.setFplanamount(BigDecimal.ZERO);
        userFinances.setFstate(UserFinancesStateEnum.CANCEL.getCode());
        userFinances.setFupdatetime(new Date());
        try {
            if (financesService.cancleUserFinances(userFinances)) {
                return Result.success("存币记录取消成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("cancleUserFinances id:{},userId:{}", id, userId);
        }
        return Result.failure("存币记录取消失败");
    }

    @Override
    public Pagination<FUserFinancesDTO> ListUserFinances(Pagination<FUserFinancesDTO> page, FUserFinancesDTO userFinances) {
        Map<String, Object> map = new HashMap<>();
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        map.put("fuid", userFinances.getFuid());
        map.put("fcoinid", userFinances.getFcoinid());

        int count = userFinancesMapper.selectUserFinancesCount(map);
        if (count > 0) {
            List<FUserFinancesDO> list = userFinancesMapper.selectUserFinancesList(map);
            for (FUserFinancesDO finances : list) {
                finances.setFamount(MathUtils.toScaleNum(finances.getFamount(), MathUtils.DEF_COIN_SCALE));
                finances.setFplanamount(MathUtils.toScaleNum(finances.getFplanamount(), MathUtils.DEF_COIN_SCALE));
            }
            page.setData(PojoConvertUtil.convert(list, FUserFinancesDTO.class));
        }
        page.setTotalRows(count);
        page.generate();
        return page;
    }

}
