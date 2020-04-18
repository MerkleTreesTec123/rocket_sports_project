package com.qkwl.web.front.controller;


import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.capital.FRewardCodeDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserRewardCodeService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.web.front.controller.base.JsonBaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@Controller
public class FrontActivityController extends JsonBaseController {

    private static Logger logger = LoggerFactory.getLogger(FrontActivityController.class);

    @Autowired
    private IUserRewardCodeService rewardCodeService;
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisHelper redisHelper;

    /**
     * 使用激活码
     *
     * @param code
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/activity/exchange")
    public ReturnResult codeExchange(@RequestParam(required = true, defaultValue = "") String code) throws Exception {
        // 传送消息
        if (code == "" || code.length() != 16) {
            return ReturnResult.FAILUER(GetR18nMsg("com.activity.error.10005"));
        }
        FUser fUser = super.getCurrentUserInfoByToken();
        fUser = userService.selectUserById(fUser.getFid());
        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
        Result flag = null;
        try {
            flag = rewardCodeService.UseRewardCode(fUser.getFid(), code, ip);
            if (flag.getSuccess()) {
                return ReturnResult.SUCCESS(flag.getMsg());
            } else {
                return ReturnResult.FAILUER(flag.getMsg());
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ReturnResult.FAILUER(GetR18nMsg("com.activity.error.10002"));
        }
    }

    @ResponseBody
    @RequestMapping(value = "/activity/index_json")
    public ReturnResult activityGo(@RequestParam(required = false, defaultValue = "1") Integer currentPage) throws Exception {
        int pagesize = 5;
        FUser fuser = super.getCurrentUserInfoByToken();
        fuser = userService.selectUserById(fuser.getFid());

        Pagination<FRewardCodeDTO> page = new Pagination<FRewardCodeDTO>(currentPage, pagesize, "/activity/index_json.html?");
        FRewardCodeDTO code = new FRewardCodeDTO();
        code.setFuid(fuser.getFid());
        code.setFstate(true);
        page = rewardCodeService.listRewardeCode(page, code);
        System.out.println(page);
        Collection<FRewardCodeDTO> list = page.getData();
        if (list != null) {
            for (FRewardCodeDTO fRewardCode : list) {
                SystemCoinType coinType = redisHelper.getCoinType(fRewardCode.getFtype());
                fRewardCode.setFtype_s(coinType.getShortName());
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pagin", page.getPagin());
        jsonObject.put("frewardcodes", list);
        return ReturnResult.SUCCESS(jsonObject);
    }
}
