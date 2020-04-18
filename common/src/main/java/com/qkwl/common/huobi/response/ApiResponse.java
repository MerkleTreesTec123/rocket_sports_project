package com.qkwl.common.huobi.response;


import com.qkwl.common.okhttp.ApiException;

public class ApiResponse<T> {

  public String status;
  public String errCode;
  public String errMsg;
  public T data;

  public T checkAndReturn() {
    if ("ok".equals(status)) {
      return data;
    }
    throw new ApiException(errCode, errMsg);
  }
}
