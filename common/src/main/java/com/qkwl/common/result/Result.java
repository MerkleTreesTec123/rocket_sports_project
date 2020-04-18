package com.qkwl.common.result;

import java.io.Serializable;

/**
 * 服务端返回消息
 */
public class Result implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 成功
     */
    public static final Integer SUCCESS = 200;
    /**
     * 失败
     */
    public static final Integer FAILURE = 300;
    /**
     * 参数错误
     */
    public static final Integer PARAM = 400;
    /**
     * 执行状态
     */
    private Boolean isSuccess;
    /**
     * 错误码
     */
    private int code;
    /**
     * 错误消息
     */
    private String msg;
    /**
     * 时间
     */
    private Long time;
    /**
     * 附加消息
     */
    private Object data;
    /**
     * 是否发送通知消息
     */
    private boolean isSendMsg = false;

    /**
     * 成功
     */
    public static Result success() {
        return success(SUCCESS, null, null,false);
    }

    /**
     * 成功
     * @param isSendMsg 是否发送通知消息
     * @return
     */
    public static Result success(boolean isSendMsg) {
        return success(SUCCESS, null, null, isSendMsg);
    }

    /**
     * 成功
     *
     * @param msg 消息
     */
    public static Result success(String msg) {
        return success(SUCCESS, msg, null,false);
    }

    /**
     * 成功
     *
     * @param code  消息
     * @param data 携带数据
     */
    public static Result success(int code, Object data) {
        return success(code, "", data,false);
    }

    /**
     * 成功
     *
     * @param msg  消息
     * @param data 携带数据
     */
    public static Result success(String msg, Object data) {
        return success(SUCCESS, msg, data,false);
    }

    /**
     * 成功
     *
     * @param code 消息码
     * @param msg  消息
     * @param data 携带数据
     */
    public static Result success(Integer code, String msg, Object data, boolean isSendMsg) {
        Result result = new Result();
        result.setSuccess(true);
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        result.setSendMsg(isSendMsg);
        result.setTime(System.currentTimeMillis());
        return result;
    }

    /**
     * 失败
     */
    public static Result failure() {
        Result result = new Result();
        result.setSuccess(false);
        result.setTime(System.currentTimeMillis());
        return result;
    }

    /**
     * 失败
     *
     * @param msg 错误消息
     */
    public static Result failure(String msg) {
        return failure(FAILURE, msg, null);
    }

    /**
     * 失败
     *
     * @param code 错误码
     * @param msg  错误消息
     */
    public static Result failure(int code, String msg) {
        return failure(code, msg, null);
    }

    /**
     * 失败
     *
     * @param code 错误码
     * @param msg  错误消息
     * @param data 附加消息
     */
    public static Result failure(int code, String msg, Object data) {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        result.setTime(System.currentTimeMillis());
        return result;
    }

    /**
     * 参数错误
     *
     * @param msg 错误消息
     */
    public static Result param(String msg) {
        return failure(PARAM, msg, null);
    }


    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Object getData() {
        return data == null ? "" : data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean getIsSendMsg() {
        return isSendMsg;
    }

    public void setSendMsg(boolean sendMsg) {
        isSendMsg = sendMsg;
    }
}
