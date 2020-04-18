package com.qkwl.common.huobi.response;


import com.qkwl.common.okhttp.ApiException;

public class ApiResponseWithTick<T> {

  public String status;
  public String errCode;
  public String errMsg;
  public T tick;

  public T checkAndReturn() {
    if ("ok".equals(status)) {
      return tick;
    }
    throw new ApiException(errCode, errMsg);
  }
}
