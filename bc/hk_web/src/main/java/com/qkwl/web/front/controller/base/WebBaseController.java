package com.qkwl.web.front.controller.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.i18n.LuangeHelper;
import com.qkwl.common.model.KeyValues;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.web.FFriendLink;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.web.front.comm.AutoCache;


public class WebBaseController extends RedisBaseControll {

    @Autowired
    private IUserService userService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private AutoCache autoCache;

    // for menu
    @ModelAttribute
    public void menuSelect() {
        // banner菜单
        String selectMenu = null;
        String uri = sessionContextUtils.getContextRequest().getRequestURI();
        if (uri.startsWith("/trade/") || uri.startsWith("/lever")) {
            selectMenu = "trade";
        } else if (uri.startsWith("/activity/shareToCoin")) {
            selectMenu = "activityShare";
        } else if (uri.startsWith("/market") || uri.startsWith("/trademarket")) {
            selectMenu = "market";
        } else if (uri.startsWith("/financial") || uri.startsWith("/deposit") || uri.startsWith("/resultonlinepay/")
                || uri.startsWith("/withdraw") || uri.startsWith("/user_ico") || uri.startsWith("/activity/rewardcoin")
                || uri.startsWith("/activity/activities")) {
            selectMenu = "financial";
        } else if (uri.startsWith("/user") || uri.startsWith("/activity") || uri.startsWith("/huobi")
                || uri.startsWith("/assets/apply")) {
            selectMenu = "security";
        } else if (uri.startsWith("/about/about")) {
            selectMenu = "about";
        } else if (uri.startsWith("/ico")) {
            selectMenu = "ico";
        } else if (uri.startsWith("/business")) {
            selectMenu = "business";
        } else if (uri.startsWith("/assets/list") || uri.startsWith("/assets/details")) {
            selectMenu = "assets";
        } else {
            selectMenu = "index";
        }
        sessionContextUtils.getContextRequest().setAttribute("selectMenu", selectMenu);

        // 左侧菜单
        if (uri.startsWith("/trade") ||
                uri.startsWith("/lever") ||
                uri.startsWith("/user") ||
                uri.startsWith("/activity") ||
                uri.startsWith("/financial") ||
                uri.startsWith("/deposit") ||
                uri.startsWith("/withdraw") ||
                uri.startsWith("/user_ico") ||
                uri.startsWith("/huobi") ||
                uri.startsWith("/assets")) {
            String leftMenu = null;
            int selectGroup = 1;
            if (uri.startsWith("/user/info")) {
                leftMenu = "userinfo";
                selectGroup = 3;
            } else if (uri.startsWith("/user/security")) {
                leftMenu = "security";
                selectGroup = 3;
            } else if (uri.startsWith("/finance/record")) {
                leftMenu = "record";
                selectGroup = 2;
            } else if (uri.startsWith("/deposit")) {
                leftMenu = "recharge";
                selectGroup = 2;
            } else if (uri.startsWith("/activity/rewardcoin")) {
                leftMenu = "reward";
                selectGroup = 2;
            } else if (uri.startsWith("/withdraw")) {
                leftMenu = "withdraw";
                selectGroup = 2;
            } else if (uri.startsWith("/trade/cny_entrust")) {
                leftMenu = "entrust";
                selectGroup = 1;
            } else if (uri.startsWith("/financial/accountalipay") || uri.startsWith("/financial/accountbank") || uri.startsWith("/financial/accountcoin")) {
                leftMenu = "accountassets";
                selectGroup = 2;
            } else if (uri.startsWith("/financial/record")) {
                leftMenu = "record";
                selectGroup = 2;
            } else if (uri.startsWith("/financial/index")) {
                leftMenu = "financial";
                selectGroup = 2;
            } else if (uri.startsWith("/financial/commission")) {
                leftMenu = "commission";
                selectGroup = 2;
            } else if (uri.startsWith("/financial/push")) {
                leftMenu = "push";
                selectGroup = 2;
            } else if (uri.startsWith("/lever")) {
                leftMenu = "lever";
                selectGroup = 2;
            } else if (uri.startsWith("/user/score")) {
                leftMenu = "levelpoint";
                selectGroup = 2;
            } else if (uri.startsWith("/user/real_name_auth")) {
                leftMenu = "userreal";
                selectGroup = 2;
            } else if (uri.startsWith("/activity/index")) {
                leftMenu = "activity";
                selectGroup = 2;
            } else if (uri.startsWith("/user/user_loginlog") || uri.startsWith("user/user_settinglog")) {
                leftMenu = "userlog";
                selectGroup = 2;
            } else if (uri.startsWith("/user_ico")) {
                leftMenu = "userico";
                selectGroup = 2;
            } else if (uri.startsWith("/huobi")) {
                leftMenu = "huobi";
                selectGroup = 2;
            } else if (uri.startsWith("/assets/apply")) {
                leftMenu = "assetsapply";
                selectGroup = 2;
            } else if (uri.startsWith("/user/priceclock")) {
                leftMenu = "priceclock";
                selectGroup = 2;
            } else if (uri.startsWith("/user/intro")) {
                leftMenu = "intro";
                selectGroup = 2;
            } else if (uri.startsWith("/financial/finances")) {
                leftMenu = "finances";
                selectGroup = 2;
            } else if (uri.startsWith("/activity/activities")) {
                leftMenu = "activities";
                selectGroup = 2;
            }
            sessionContextUtils.getContextRequest().setAttribute("selectGroup", selectGroup);
            sessionContextUtils.getContextRequest().setAttribute("leftMenu", leftMenu);
        }
    }

    /**
     * 此方法会在每个controller前执行
     */
    @ModelAttribute
    public void addConstant() {
        // 语言资源文件
        String localeStr = LuangeHelper.getLan(sessionContextUtils.getContextRequest());
        sessionContextUtils.getContextRequest().setAttribute("localeStr", localeStr);
        sessionContextUtils.getContextRequest().setAttribute("staticurl", redisHelper.getSystemArgs("staticurl"));

        //设置指定键对值的系统属性
        // 前端常量
        sessionContextUtils.getContextRequest().setAttribute("constant", redisHelper.getSystemArgsList());
        // 首条公告
        List<KeyValues> articles = autoCache.getArticles(localeStr);
        sessionContextUtils.getContextRequest().setAttribute("headerarticles", articles);

        //友情链接
        List<FFriendLink> ffriendlinks = redisHelper.getFFriendLinkList();
        sessionContextUtils.getContextRequest().setAttribute("ffriendlinks", ffriendlinks);
        //用户资产
        FUser fuser = getCurrentUserInfoByToken();
        if (fuser != null) {
            fuser = userService.selectUserById(fuser.getFid());
            sessionContextUtils.getContextRequest().setAttribute("userInfo", fuser);
        }
    }

    /**
     * 获取多语言
     *
     * @param key 键值
     * @return
     */
    public String GetR18nMsg(String key) {
        return LuangeHelper.GetR18nMsg(sessionContextUtils.getContextRequest(), key);
    }

    /**
     * 获取多语言
     *
     * @param key  键值
     * @param args 参数
     * @return
     */
    public String GetR18nMsg(String key, Object... args) {
        return LuangeHelper.GetR18nMsg(sessionContextUtils.getContextRequest(), key, args);
    }

    /**
     * 获取语言枚举
     *
     * @return
     */
    public LocaleEnum getLanEnum() {
        String localeStr = LuangeHelper.getLan(sessionContextUtils.getContextRequest());
        for (LocaleEnum locale : LocaleEnum.values()) {
            if (locale.getName().equals(localeStr)) {
                return locale;
            }
        }
        return null;
    }

    public String getIpAddr() {
        return Utils.getIpAddr(sessionContextUtils.getContextRequest());
    }
}
