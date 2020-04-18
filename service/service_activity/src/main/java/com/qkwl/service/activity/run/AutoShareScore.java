package com.qkwl.service.activity.run;

import com.qkwl.common.dto.Enum.LogAdminActionEnum;
import com.qkwl.common.dto.Enum.UserStatusEnum;
import com.qkwl.common.dto.log.FLogAdminAction;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.util.AESUtils;
import com.qkwl.common.util.Constant;
import com.qkwl.service.activity.dao.FLogAdminActionMapper;
import com.qkwl.service.activity.dao.FUserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;


@Component
public class AutoShareScore {

    @Autowired
    private FUserMapper userMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private FLogAdminActionMapper logAdminActionMapper;

    // 分享送币活动禁用账号代码，不启用
    //@PostConstruct
    private void init(){
        List<FUser> users = userMapper.listBrush();
        if(users == null || users.size()<=0){
            return;
        }
        for(FUser user : users){
            if(user.getFstatus().equals(UserStatusEnum.FORBBIN_VALUE)){
                continue;
            }
            String currentShareId = AESUtils.AESEncode(Constant.AesSecretKey, user.getFloginname() + "#" + user.getFshowid());
            if (redisHelper.getMemberScore(2,currentShareId)>0){
                redisHelper.deleteMemberScore(2,currentShareId);
                user.setFstatus(UserStatusEnum.FORBBIN_VALUE);
                user.setFupdatetime(new Date());
                userMapper.updateStatus(user);
                FLogAdminAction logAdminAction = new FLogAdminAction();
                logAdminAction.setFadminid(1);
                logAdminAction.setFuid(user.getFid());
                logAdminAction.setFagentid(0);
                logAdminAction.setFtype(LogAdminActionEnum.SYSTEM_USER_BAN_OK.getCode());
                logAdminAction.setFcontent("分享送币2期刷号");
                logAdminAction.setFcreatetime(new Date());
                logAdminAction.setFupdatetime(new Date());
                logAdminActionMapper.insert(logAdminAction);
                System.out.println("禁用用户："+"["+currentShareId+"]"+user.getFid());
            }
        }


    }
}
