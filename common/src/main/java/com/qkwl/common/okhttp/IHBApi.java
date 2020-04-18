package com.qkwl.common.okhttp;

import com.qkwl.common.huobi.request.CreateOrderRequest;
import com.qkwl.common.huobi.request.QueryKLineRequest;
import com.qkwl.common.huobi.request.QueryOrderRequest;
import com.qkwl.common.huobi.response.Account;
import com.qkwl.common.huobi.response.KLine;
import com.qkwl.common.huobi.response.Order;

import java.util.List;

/**
 * 火币接口
 */
public interface IHBApi {

    /**
     * 
     * 获取账号
     *
     * @return
     */
    List<Account> getAccountList();

    /**
     * 创建订单
     *
     * @param request
     * @return
     */
    long createOrder(CreateOrderRequest request);

    /**
     * 查询订单
     *
     * @param request
     * @return
     */
    List<Order> queryOrderList(QueryOrderRequest request);


    /************ 行情相关*************/

    List<KLine> queryKLineList(QueryKLineRequest request);









}
