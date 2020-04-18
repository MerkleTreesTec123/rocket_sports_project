package com.qkwl.common.okhttp;

import com.qkwl.common.huobi.request.QueryOrderRequest;
import com.qkwl.common.huobi.response.Account;
import com.qkwl.common.huobi.response.Order;

import java.io.IOException;
import java.util.List;

public class Main {

  //static final String API_KEY = "11411a60-7260ebc2-03b476da-1fe28";
  //static final String API_SECRET = "f69562dd-b9f457c1-7b338f63-bdd34";

  public static void main(String[] args) {
//    try {
//      apiSample();
//    } catch (ApiException e) {
//      System.err.println("API Error! err-code: " + e.getErrCode() + ", err-msg: " + e.getMessage());
//      e.printStackTrace();
//    }
  }

//  static void apiSample() {
//    // create ApiClient using your api key and api secret:
//    ApiClient client = new ApiClient(API_KEY, API_SECRET);
//    // get symbol list:
//    print(client.getSymbols());
//    // get accounts:
//    List<Account> accounts = client.getAccounts();
//    print(accounts);
//
//    QueryOrderRequest request = new QueryOrderRequest();
//    request.symbol = "ethbtc";
//    List<Order> order = client.queryOrderList(request);
//    print(order);
//    /**
//    if (!accounts.isEmpty()) {
//      // find account id:
//      Account account = accounts.get(0);
//      long accountId = account.id;
//      //System.out.println("accountID "+accountId);
//
//      // create order:
//      CreateOrderRequest createOrderReq = new CreateOrderRequest();
//      createOrderReq.accountId = String.valueOf(accountId);
//      createOrderReq.amount = "0.01";
//      //createOrderReq.price = "1100.99";
//      createOrderReq.symbol = "ethbtc";
//      createOrderReq.type = CreateOrderRequest.OrderType.BUY_MARKET;
//      Long orderId = client.createOrder(createOrderReq);
//      print(orderId);f
//      // place order:
//      String r = client.placeOrder(orderId);
//      print(r);
//
//    }**/
//  }
//
//  static void print(Object obj) {
//    try {
//      System.out.println(JsonUtil.writeValue(obj));
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
}
