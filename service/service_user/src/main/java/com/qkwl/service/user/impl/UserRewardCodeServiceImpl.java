package com.qkwl.service.user.impl;

import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.capital.FRewardCodeDTO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserRewardCodeService;
import com.qkwl.common.util.PojoConvertUtil;
import com.qkwl.common.util.Utils;
import com.qkwl.service.user.dao.FRewardCodeMapper;
import com.qkwl.service.user.model.FRewardCodeDO;
import com.qkwl.service.user.tx.RewardCodeServiceTx;
import com.qkwl.service.user.utils.MQSendUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 充值码实现
 */
@Service("userRewardCodeService")
public class UserRewardCodeServiceImpl implements IUserRewardCodeService {

    private static final Logger logger = LoggerFactory.getLogger(UserRewardCodeServiceImpl.class);

    @Autowired
    private FRewardCodeMapper rewardCodeMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private MQSendUtils mqSendUtils;
    @Autowired
    private RewardCodeServiceTx rewardCodeServiceTx;

    @Override
    public Pagination<FRewardCodeDTO> listRewardeCode(Pagination<FRewardCodeDTO> page, FRewardCodeDTO code) {
        Map<String, Object> map = new HashMap<>();
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        map.put("fuid", code.getFuid());
        map.put("fcode", code.getFcode());
        map.put("fstate", code.getFstate());
        int count = rewardCodeMapper.countListByCode(map);
        if (count > 0) {
            List<FRewardCodeDO> codeList = rewardCodeMapper.selectListByCode(map);
            Map<Integer, String> coinMap = redisHelper.getCoinTypeNameMap();
            for (FRewardCodeDO fRewardCode : codeList) {
                if (fRewardCode.getFtype() > 0) {
                    fRewardCode.setFamount(MathUtils.toScaleNum(fRewardCode.getFamount(), MathUtils.DEF_COIN_SCALE));
                } else {
                    fRewardCode.setFamount(MathUtils.toScaleNum(fRewardCode.getFamount(), MathUtils.DEF_CNY_SCALE));
                }
                if (fRewardCode.getFtype() != null) {
                    fRewardCode.setFtype_s(coinMap.get(fRewardCode.getFtype()));
                }
            }
            page.setData(PojoConvertUtil.convert(codeList, FRewardCodeDTO.class));
        }
        page.setTotalRows(count);
        page.generate();
        return page;
    }

    @Override
    public Result UseRewardCode(Integer userId, String code, String ip) {
        if (userId == null) {
            return Result.param("userId is null");
        }
        if (code == null || "".equals(code.trim())) {
            return Result.param("code is null");
        }
        if (ip == null || "".equals(ip.trim())) {
            return Result.param("ip is null");
        }
        FRewardCodeDO frewardcode = rewardCodeMapper.selectByCode(code);
        // 兑换码不存在
        if (frewardcode == null) {
            return Result.failure(1000, "充值码错误");
        }
        // 兑换码已使用
        if (frewardcode.getFstate()) {
            return Result.failure(1001, "充值码已激活");
        }
        // 非本人兑换码
        if (frewardcode.getFislimituser() != null && frewardcode.getFislimituser()) {
            if (frewardcode.getFuid() != null && !frewardcode.getFuid().equals(userId)) {
                return Result.failure(1002, "充值码已绑定用户");
            }
        }
        if (frewardcode.getFislimituse() != null && frewardcode.getFislimituse()) {
            Map<String, Object> map = new HashMap<>();
            map.put("fuid", userId);
            map.put("fbatch", frewardcode.getFbatch());
            map.put("ftype", frewardcode.getFtype());
            int count = rewardCodeMapper.countUseCodeByUser(map);
            // 同批次兑换次数超出
            if (count >= frewardcode.getFusenum()) {
                return Result.failure(1003, "您本次活动已多次使用充值码");
            }
        }
        // 兑换时间已过期
        if (frewardcode.getFusedate() != null && frewardcode.getFusedate().before(Utils.getTimestamp())) {
            return Result.failure(1004, "充值码已过期");
        }
        frewardcode.setFuid(userId);
        frewardcode.setFstate(true);
        frewardcode.setFupdatetime(Utils.getTimestamp());
        try {
            if (!rewardCodeServiceTx.useRewardCode(frewardcode)) {
                return Result.failure("使用失败");
            }
        } catch (Exception e) {
            logger.error("UseRewardCode is err userId:{},code{},ip{}", userId, code, ip, e);
        }
        mqSendUtils.SendUserAction(userId, LogUserActionEnum.USE_CODE, frewardcode.getFamount(), ip);
        return Result.success("使用成功",frewardcode);
    }
}
