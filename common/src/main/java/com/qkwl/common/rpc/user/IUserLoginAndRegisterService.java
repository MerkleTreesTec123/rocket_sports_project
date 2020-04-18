package com.qkwl.common.rpc.user;

import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.dto.Enum.UserLoginType;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.LoginResponse;
import com.qkwl.common.dto.user.RequestUserInfo;

/**
 * Created by ZKF on 2017/4/18.
 */
public interface IUserLoginAndRegisterService {

    /**
     * 用户登陆
     * @param userinfo 请求用户信息的参数实体
     * @param type 登录类型
     * @param ip 登录IP地址
     * @return 登录后响应的用户信息实体对象
     * @throws BCException 执行失败
     */
    public LoginResponse updateCheckLogin(RequestUserInfo userinfo, UserLoginType type, String ip) throws BCException;

    /**
     * 用户注册
     * @param fuser 用户实体对象
     * @param ip 注册IP地址
     * @param type 用户登录类型
     * @return 登录后响应的用户信息实体对象
     * @throws BCException 执行失败
     */
    public LoginResponse insertRegister(FUser fuser, String ip, UserLoginType type) throws BCException;

}
