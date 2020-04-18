package com.qkwl.web.front.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;
import com.qkwl.common.dto.capital.FUserVirtualAddressWithdrawDTO;
import com.qkwl.common.dto.capital.UserFinancesOrderDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemCoinTypeVO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FUserFinancesDTO;
import com.qkwl.common.dto.finances.FVirtualFinances;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserDTO;
import com.qkwl.common.dto.wallet.CurrencyRate;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserCapitalAccountService;
import com.qkwl.common.rpc.capital.IUserFinancesService;
import com.qkwl.common.rpc.capital.IUserPushService;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.web.front.controller.base.JsonBaseController;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 理财相关
 */
@Controller
public class FrontFinancialJsonController extends JsonBaseController {
    private static final Logger logger = LoggerFactory.getLogger(FrontFinancialJsonController.class);
    @Autowired
    private IUserWalletService userWalletService;
    @Autowired
    private IUserFinancesService userFinancesService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserCapitalAccountService userCapitalAccountService;

    /**
     * 提交理财
     */
    @ResponseBody
    @RequestMapping("/submit_finances")
    public ReturnResult submitFinances(
            @RequestParam(value = "symbol") Integer coinid,
            @RequestParam(value = "type") Integer financesid,
            @RequestParam(value = "count") BigDecimal count,
            @RequestParam(value = "tradepwd") String tradepwd,
            @RequestParam(value = "phonecode", required = false) String phonecode,
            @RequestParam(value = "googlecode", required = false) String googlecode) {
        count = MathUtils.toScaleNum(count, MathUtils.ENTER_COIN_SCALE);
        if (count.compareTo(new BigDecimal(100)) < 0) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.finances.com.1000"));
        }
        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
        FUser user = super.getCurrentUserInfoByToken();
        SystemCoinType coinType = redisHelper.getCoinType(coinid);
        if (coinType == null || !coinType.getIsFinances()
                || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.finances.com.1001"));
        }
        if (financesid == null) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.finances.com.1002"));
        }
        if (StringUtils.isEmpty(phonecode) && user.getFistelephonebind()) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10114"));
        }
        if (StringUtils.isEmpty(googlecode) && user.getFgooglebind()) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10115"));
        }
        if (StringUtils.isEmpty(tradepwd)) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10116"));
        }
        try {
            tradepwd = Utils.MD5(tradepwd);
        } catch (Exception e) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10117"));
        }
        UserFinancesOrderDTO order = new UserFinancesOrderDTO();
        order.setUserId(user.getFid());
        order.setCoinId(coinType.getId());
        order.setCount(count);
        order.setPhoneCode(phonecode);
        order.setGoogleCode(googlecode);
        order.setIp(ip);
        order.setFinancesId(financesid);
        order.setTradePass(tradepwd);
        order.setPlatformId(PlatformEnum.BC.getCode());
        try {
            Result result = userFinancesService.createUserFinancesOrder(order);
            if (result.getSuccess()) {
                return ReturnResult.SUCCESS(GetR18nMsg("capital.finances.create." + result.getCode().toString()));
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("submitFinances {}", result.getMsg());
            } else if (result.getCode() < 10000) {
                return ReturnResult.FAILUER(GetR18nMsg("capital.finances.create." + result.getCode().toString()));
            } else {
                return ReturnResult.FAILUER(GetR18nMsg("com.error." + result.getCode().toString(), result.getData().toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("submitFinances error");
        }
        return ReturnResult.FAILUER(GetR18nMsg("com.error.10117"));
    }

    /**
     * 理财获取数据
     */
    @ResponseBody
    @RequestMapping("/get_finances")
    public ReturnResult getFinances(
            @RequestParam(value = "symbol") Integer coinid) {
        SystemCoinType coinType = redisHelper.getCoinType(coinid);
        if (coinType == null || !coinType.getIsFinances() ||
                coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.finances.com.1001"));
        }
        FUser user = getCurrentUserInfoByToken();
        // 钱包
        UserCoinWallet userWallet = userWalletService.getUserCoinWallet(user.getFid(), coinType.getId());
        List<FVirtualFinances> type = redisHelper.getVirtualFinancesList(coinType.getId());
        JSONObject result = new JSONObject();
        result.put("total", userWallet.getTotal());
        JSONArray typeList = new JSONArray();
        if (type == null) {
            result.put("typeList", typeList);
            return ReturnResult.SUCCESS(result);
        }
        for (FVirtualFinances virtualFinances : type) {
            JSONObject typeJson = new JSONObject();
            typeJson.put("id", virtualFinances.getFid());
            typeJson.put("rate", virtualFinances.getFrate());
            typeJson.put("name", virtualFinances.getFname());
            typeList.add(typeJson);
        }
        result.put("typeList", typeList);
        return ReturnResult.SUCCESS(result);
    }

    /**
     * 取消理财
     */
    @ResponseBody
    @RequestMapping("/cancel_finances")
    public ReturnResult cancelFinances(
            @RequestParam(value = "fid") Integer id) {
        try {
            FUser user = super.getCurrentUserInfoByToken();
            Result result = userFinancesService.cancleUserFinancesOrder(id, user.getFid());
            if (result.getSuccess()) {
                return ReturnResult.SUCCESS(GetR18nMsg("capital.finances.cancle." + result.getCode().toString()));
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("cancelFinances {}", result.getMsg());
            } else if (result.getCode() < 10000) {
                return ReturnResult.FAILUER(GetR18nMsg("capital.finances.cancle." + result.getCode().toString()));
            } else {
                return ReturnResult.FAILUER(GetR18nMsg("com.error." + result.getCode().toString(), result.getData().toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("cancelFinances error");
        }
        return ReturnResult.FAILUER(GetR18nMsg("com.error.10117"));
    }

    /**
     * 个人资产
     */
    @ResponseBody
    @RequestMapping("/financial/index_json")
    public ReturnResult index() throws Exception {
        FUser fuser = getCurrentUserInfoByToken();
        List<UserCoinWallet> userWalletList = userWalletService.listUserCoinWallet(fuser.getFid());
        // 数据
        Iterator iterator = userWalletList.iterator();
        while (iterator.hasNext()) {
            UserCoinWallet wallet = (UserCoinWallet) iterator.next();
            if (!redisHelper.hasCoinId(wallet.getCoinId())) {
                iterator.remove();
            }
        }
        JSONObject jsonObject = new JSONObject();

        String base = "CNY";
        CurrencyRate currencyRateObj;
        String currencyJson = redisHelper.get("currency_" + base);
        if (TextUtils.isEmpty(currencyJson)) {
            currencyRateObj = userWalletService.getCurrencyRate(base);
            if (currencyRateObj != null) {
                redisHelper.setNoExpire("currency_" + base, JSON.toJSONString(currencyRateObj));
            }
        } else {
            currencyRateObj = JSON.parseObject(currencyJson,CurrencyRate.class);
        }

        if (currencyRateObj == null) {
            Map<String, Object> systemArgsList = redisHelper.getSystemArgsList();
            String rechargeUSDTPrice = systemArgsList.get("rechargeUSDTPrice").toString();
            BigDecimal cnyPrice = new BigDecimal("6.5");
            if (!TextUtils.isEmpty(rechargeUSDTPrice)) {
                cnyPrice = new BigDecimal(rechargeUSDTPrice);
            }

            currencyRateObj = new CurrencyRate();
            currencyRateObj.setRate(cnyPrice);
        }


        BigDecimal netAssets = getNetAssets(userWalletList);
        jsonObject.put("netassets", MathUtils.mul(netAssets,currencyRateObj.getRate()));
        jsonObject.put("totalassets", getTotalAssets(userWalletList));
        jsonObject.put("userWalletList", userWalletList);
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 资金帐号-虚拟币地址管理
     */
    @ResponseBody
    @RequestMapping("/financial/accountcoin_json")
    public ReturnResult accountcoin(
            @RequestParam(required = false, defaultValue = "0") int symbol
    ) throws Exception {
        FUser fuser = getCurrentUserInfoByToken();
        fuser = userService.selectUserById(fuser.getFid());
        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getType().equals(SystemCoinTypeEnum.COIN.getCode())) {
            coinType = redisHelper.getCoinTypeIsWithdrawFirst(SystemCoinTypeEnum.COIN.getCode());
        }
        JSONObject jsonObject = new JSONObject();
        if (coinType != null) {
            List<FUserVirtualAddressWithdrawDTO> withdraws = userCapitalAccountService.listCoinAddressWithdraw(fuser.getFid(), coinType.getId());
            jsonObject.put("fuser", ModelMapperUtils.mapper(fuser, FUserDTO.class));
            jsonObject.put("coinType", ModelMapperUtils.mapper(coinType, SystemCoinTypeVO.class));
            jsonObject.put("fvirtualaddressWithdraws", withdraws);
        }
        jsonObject.put("coinTypeList", ModelMapperUtils.mapper(
                redisHelper.getCoinTypeIsWithdrawList(SystemCoinTypeEnum.COIN.getCode()), SystemCoinTypeVO.class));
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 存币理财
     */
    @ResponseBody
    @RequestMapping("/financial/finances_json")
    public ReturnResult finances(
            @RequestParam(required = false, defaultValue = "1") Integer currentPage
    ) {
        // 币种
        List<SystemCoinType> coinTypes = redisHelper.getCoinTypeFinancesList();
        if (coinTypes == null || coinTypes.size() == 0) {
            return ReturnResult.FAILUER("");
        }
        // 默认第一条
        SystemCoinType coinType = coinTypes.get(0);
        // 记录
        FUser fuser = getCurrentUserInfoByToken();
        Pagination<FUserFinancesDTO> page = new Pagination<>(currentPage, Constant.webPageSize,
                "/financial/finances.html?");
        FUserFinancesDTO userFinances = new FUserFinancesDTO();
        userFinances.setFuid(fuser.getFid());
        page = userFinancesService.ListUserFinances(page, userFinances);
        // 钱包
        UserCoinWallet userWallet = userWalletService.getUserCoinWallet(fuser.getFid(), coinType.getId());
        Map<Integer, String> financesCoinMap = new LinkedHashMap<>();
        for (SystemCoinType systemCoinType : coinTypes) {
            financesCoinMap.put(systemCoinType.getId(), systemCoinType.getName());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userWallet", userWallet);
        jsonObject.put("page", page);
        jsonObject.put("financesCoinMap", financesCoinMap);
        jsonObject.put("typeList", redisHelper.getVirtualFinancesList(coinType.getId()));
        return ReturnResult.SUCCESS(jsonObject);
    }
}
