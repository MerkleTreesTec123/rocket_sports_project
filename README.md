##接口描述
###1、获取交易区
简要描述： 
获取交易区

请求URL： https://域名/v1/market/area.html

请求方式：GET

参数： 无

返回示例 如下
```json
{
    "code": 200,
    "msg": "成功",
    "time": 1523433174227,
    "data": [
        {
            "code": 1,
            "name": "GSET"
        },
        {
            "code": 2,
            "name": "BTC"
        },
        {
            "code": 3,
            "name": "ETH"
        }
    ]
}
```
返回参数说明 无

备注 更多返回错误代码请看首页的错误代码描述

###2、获取交易区下面的交易对
简要描述： 获取交易对

请求URL： https://域名/v1/market/list.html?symbol=1

请求方式：GET

参数： 

参数名   |	必选 |	类型	| 说明
------- | ----- |------ |-----     
symbol	|  是	|string |交易区code

返回示例
```json
{
    "code": 200,
    "msg": "成功",
    "time": 1523433478693,
    "data": [
        {
            "id": 8,
            "sortId": 8,
            "type": 1,
            "status": 1,
            "buyCoinId": 9,
            "buySymbol": "",
            "buyName": "游戏币",
            "buyShortName": "GSET",
            "buyWebLogo": "https://hotcoin-static.oss-cn-hangzhou.aliyuncs.com/hotcoin/upload/coin/34219b99e30a495198627a22c7607f19AMP.png",
            "buyAppLogo": null,
            "sellCoinId": 1,
            "sellSymbol": "",
            "sellName": "比特币",
            "sellShortName": "BTC",
            "sellWebLogo": "https://hotcoin-static.oss-cn-hangzhou.aliyuncs.com/hotcoin/upload/coin/8304633eec1c47a1878dfb7ee4e7cab2timg (2).jpg",
            "sellAppLogo": "https://hotcoin-static.oss-cn-hangzhou.aliyuncs.com/hotcoin/upload/coin/50e9ad1babac4e9c8e7e7f6fe49ef8fetimg (2).jpg",
            "isShare": true,
            "isStop": false,
            "openTime": null,
            "stopTime": null,
            "buyFee": 0.002,
            "sellFee": 0.002,
            "remoteId": 0,
            "priceWave": 0,
            "priceRange": 0,
            "minCount": 0,
            "maxCount": 0,
            "minPrice": 0,
            "maxPrice": 0,
            "amountOffset": "1#1",
            "priceOffset": "0#0",
            "digit": "2#8",
            "openPrice": 54598.5000000000,
            "agentId": 0,
            "gmtCreate": 1509691266000,
            "gmtModified": 1520269793000,
            "version": 0,
            "typeName": "对GSET交易区"
        }
    ]
}
```

返回参数说明 
参数名	类型	说明
buyCoinId	int	买方币ID
sellCoinId	int	卖方币ID
digit	String	交易小数点	分隔的字符串，第一个数字是价格，第二个是数量
备注 
更多返回错误代码请看首页的错误代码描述
3、获取实时行情
简要描述： 
用户注册接口
请求URL： 
https://www.hotcoin.top/real/markets.html?symbol=8,9
请求方式：
GET
参数： 
参数名	必选	类型	说明
symbol	是	string	交易对的 ID ，逗号分隔
返回示例
```json
{
    "code": 200,
    "msg": "成功",
    "time": 1523438480280,
    "data": [
        {
            "symbol": "",
            "sellSymbol": "BTC",
            "total": 352.648,
            "buySymbol": "GSET",
            "p_open": 43607.8,
            "p_new": 43311.15,
            "buy": 43311.15,
            "sell": 44201.11
        },
        {
            "symbol": "",
            "sellSymbol": "LTC",
            "total": 21122.1388,
            "buySymbol": "GSET",
            "p_open": 735.37,
            "p_new": 730.19,
            "buy": 730.19,
            "sell": 740.55
        }
    ]
}
```
返回参数说明 
参数名	类型	说明
total	double	量
p_open	double	开盘价
p_new	double	最新的价格
buy	double	买1
buy	double	卖1
备注 
更多返回错误代码请看首页的错误代码描述
4、获取交易对的用户可用资产
简要描述： 
用户在当前交易对下面的可用资产
请求URL： 
https://域名/v1/market/userassets.html?tradeid=1&token=1
请求方式：

GET/POST

备注 

o不需要签名
参数： 
参数名	必选	类型	说明
tradeid	是	string	交易对 ID
token	是	string	令牌
返回示例
```json
{
    "code": 200,
    "msg": "成功",
    "time": 1523440192853,
    "data": {
        "score": 861147,
        "buyCoin": {
            "total": 0.4659662,
            "frozen": 0,
            "borrow": 0,
            "id": 9
        },
        "sellCoin": {
            "total": 1,
            "frozen": 0,
            "borrow": 0,
            "id": 1
        }
    }
}
```

返回参数说明 
参数名	类型	说明
total	double	可用余额
frozen	double	冻结数量
borrow	double	理财，这个暂时用不到
id	int	id，不重要
备注 
更多返回错误代码请看首页的错误代码描述
5、深度、盘口
简要描述： 
深度
请求URL： 
https://www.hotcoin.top/kline/fulldepth.html?symbol=8
请求方式：
GET { "code": 200, "msg": "成功", "time": 1523440677533, "data": { "depth": { "date": 1523440677, "asks": [ [ 44794.41, 0.0157 ], [ 45091.06, 0.0492 ], [ 45387.71, 0.0324 ], [ 45684.36, 0.0315 ], [ 45981.02, 0.0492 ], [ 46277.67, 0.0103 ], [ 46574.32, 0.0489 ], [ 46870.97, 0.0209 ], [ 47167.62, 0.0491 ], [ 47464.27, 0.0455 ], [ 47760.93, 0.0454 ], [ 48057.58, 0.0068 ], [ 48354.23, 0.0489 ], [ 48650.88, 0.0492 ], [ 48947.53, 0.0493 ], [ 49244.19, 0.0491 ], [ 49540.84, 0.049 ], [ 49837.49, 0.049 ], [ 50134.14, 0.0492 ], [ 50430.79, 0.049 ], [ 50727.44, 0.0494 ], [ 51024.1, 0.0491 ], [ 51320.75, 0.049 ], [ 51617.4, 0.0489 ], [ 51914.05, 0.0494 ], [ 52210.7, 0.0491 ], [ 52507.35, 0.0491 ], [ 52804.01, 0.0491 ], [ 53100.66, 0.0493 ], [ 53397.31, 0.0492 ], [ 53693.96, 0.049 ], [ 53977, 0.005 ], [ 53990.61, 0.0493 ], [ 54287.26, 0.0491 ], [ 54583.92, 0.0491 ], [ 54880.57, 0.0489 ], [ 55177.22, 0.049 ], [ 55473.87, 0.049 ], [ 55770.52, 0.0489 ], [ 56067.18, 0.0491 ], [ 56363.83, 0.0494 ], [ 56660.48, 0.0492 ], [ 56957.13, 0.0493 ], [ 57253.78, 0.049 ], [ 57550.43, 0.0491 ], [ 57847.09, 0.0491 ], [ 58143.74, 0.049 ], [ 58440.39, 0.0489 ], [ 58737.04, 0.0489 ], [ 59033.69, 0.0491 ], [ 59330.34, 0.0493 ] ], "bids": [ [ 43607.8, 0.0057 ], [ 43311.15, 0.0492 ], [ 43014.5, 0.0129 ], [ 42717.85, 0.0381 ], [ 42421.19, 0.0369 ], [ 42124.54, 0.0174 ], [ 41827.89, 0.0422 ], [ 41531.24, 0.0489 ], [ 41234.59, 0.0022 ], [ 40987, 0.02 ], [ 40937.94, 0.0354 ], [ 40641.28, 0.022 ], [ 40344.63, 0.049 ], [ 40047.98, 0.0359 ], [ 39751.33, 0.049 ], [ 39454.68, 0.0491 ], [ 39158.03, 0.0492 ], [ 38861.37, 0.049 ], [ 38564.72, 0.0493 ], [ 38268.07, 0.049 ], [ 37971.42, 0.0492 ], [ 37674.77, 0.0494 ], [ 37378.12, 0.0493 ], [ 37081.46, 0.0493 ], [ 36784.81, 0.0492 ], [ 36488.16, 0.0491 ], [ 36191.51, 0.0492 ], [ 35894.86, 0.0493 ], [ 35598.2, 0.049 ], [ 35301.55, 0.0494 ], [ 35004.9, 0.0489 ], [ 34708.25, 0.0489 ], [ 34411.6, 0.0492 ], [ 34114.95, 0.0493 ], [ 33818.29, 0.0492 ], [ 33521.64, 0.0489 ], [ 33224.99, 0.0491 ], [ 32928.34, 0.0492 ], [ 32631.69, 0.0494 ], [ 32335.04, 0.0491 ], [ 32038.38, 0.0492 ], [ 31741.73, 0.0493 ], [ 31445.08, 0.0491 ], [ 31148.43, 0.0491 ], [ 30851.78, 0.0489 ], [ 30555.13, 0.049 ], [ 30258.47, 0.0494 ], [ 29961.82, 0.0491 ] ] } } } 参数： 
参数名	必选	类型	说明
symbol	是	string	交易对id
返回示例

返回参数说明 
数组第一个元素表示 价格

数组第二个元素表示 数量

备注 

更多返回错误代码请看首页的错误代码描述
6、充值码兑换
简要描述： 
充值码兑换
请求URL： 
https://www.hotcoin.top/v1/activity/exchange.html
请求方式：
POST 不需要签名 参数： 
参数名	必选	类型	说明
token	是	string	令牌
code	是	string	充值码
返回示例
{
    "code": 200,
    "msg": "成功",
    "time": 1523433174227,
    "data": {}
}
返回参数说明 
略
备注 
更多返回错误代码请看首页的错误代码描述
7、充值码兑换列表
简要描述： 
充值码兑换列表
请求URL： 
https://域名/v1/activity/index.html?token=BCAPP_LOGIN_4df5bde009073d3ef60da64d736724d6_46DD459F0F004B7BAB78D7F75B0502C8
请求方式：
POST / GET 
参数： 
参数名	必选	类型	说明
token	是	string	令牌
currentPage	是	int	分页
返回示例
{
    "code": 200,
    "msg": "成功",
    "time": 1523517081411,
    "data": {
        "pagin": null,
        "frewardcodes": [
            {
                "fid": 6,
                "fuid": 10687,
                "floginname": "",
                "ftype": 1,
                "ftype_s": "BTC",
                "fcode": "BDF5E8wP4fhTy6CG",
                "famount": 1,
                "fstate": true,
                "fstate_s": "",
                "fcreatetime": 1523516123000,
                "fupdatetime": 1523516166000,
                "version": 1,
                "fbatch": "",
                "fislimituser": true,
                "fislimituse": "",
                "fusenum": "",
                "fusedate": ""
            }
        ]
    }
}
返回参数说明 
参数名	类型	说明
fstate	boolean	true 表示充值成功
ftype	int	充值类型
ftype_s	String	充值类型的字符串表示，就取这个值就可以了
fcode	String	充值码
famount	double	充值数量
fupdatetime	timestamp	充值时间
备注 
更多返回错误代码请看首页的错误代码描述
8、下单
简要描述： 
下单
请求URL： 
https://www.hotcoin.top/v1/entrust/place.html
请求方式：
POST 需要签名 参数： 
参数名	必选	类型	说明
symbol	是	string	交易对的中文名称，下划线分隔，btc_gest
tradeAmount	是	double	数量
tradePrice	是	double	价格
type	是	String	类型，buy 表示买，sell表示卖
tradePwd	是	String	交易密码
返回示例
{
    "code": 200,
    "msg": "下单成功",
    "time": 1523515590145,
    "data": null
}
返回参数说明 
参数名	类型	说明
备注 
更多返回错误代码请看首页的错误代码描述
9、取消订单
简要描述： 
用户注册接口
请求URL： 
https://testest.hotcoin.top/v1/entrust/cancel.html
请求方式：
POST
参数： 
需要签名
参数名	必选	类型	说明
id	是	int	订单id
token	是	String	令牌
返回示例
返回示例
  {
    "code": 200,
    "msg": "成功",
    "time": 1523515590145,
    "data": {}
}
返回参数说明 
参数名	类型	说明
备注 
更多返回错误代码请看首页的错误代码描述
10、获取订单
简要描述： 
获取订单
请求URL： 
https://域名/v1/entrust/list.html?symbol=btc_gset&type=0&token=BCAPP_LOGIN_4df5bde009073d3ef60da64d736724d6_46DD459F0F004B7BAB78D7F75B0502C8
请求方式：
POST 
参数： 
参数名	必选	类型	说明
symbol	是	string	交易对
type	是	int	类型，0全部、1当前订单、2历史订单
token	否	string	令牌
返回示例
{
    "code": 200,
    "msg": "获取成功！",
    "time": 1523626484076,
    "data": {
        "entrutsHis": [
            {
                "types": 1,
                "leftcount": 1,
                "fees": 0,
                "last": 0,
                "count": 1,
                "successamount": 0,
                "source": "WEB",
                "type": 1,
                "price": 500000,
                "buysymbol": "",
                "id": 6665115,
                "time": "2018-04-12 23:27:19",
                "sellsymbol": "",
                "status": 5
            },
            {
                "types": 0,
                "leftcount": 0.0069,
                "fees": 0,
                "last": 0,
                "count": 0.0069,
                "successamount": 0,
                "source": "API",
                "type": 0,
                "price": 69817.52,
                "buysymbol": "",
                "id": 1228084,
                "time": "2018-03-07 11:15:49",
                "sellsymbol": "",
                "status": 5
            },
            {
                "types": 0,
                "leftcount": 0.0069,
                "fees": 0,
                "last": 0,
                "count": 0.0069,
                "successamount": 0,
                "source": "API",
                "type": 0,
                "price": 69817.52,
                "buysymbol": "",
                "id": 1228078,
                "time": "2018-03-07 11:15:45",
                "sellsymbol": "",
                "status": 5
            },
            {
                "types": 0,
                "leftcount": 0.0069,
                "fees": 0,
                "last": 0,
                "count": 0.0069,
                "successamount": 0,
                "source": "API",
                "type": 0,
                "price": 69817.52,
                "buysymbol": "",
                "id": 1228071,
                "time": "2018-03-07 11:15:41",
                "sellsymbol": "",
                "status": 5
            },
            {
                "types": 0,
                "leftcount": 0.0069,
                "fees": 0,
                "last": 0,
                "count": 0.0069,
                "successamount": 0,
                "source": "API",
                "type": 0,
                "price": 69817.52,
                "buysymbol": "",
                "id": 1228063,
                "time": "2018-03-07 11:15:37",
                "sellsymbol": "",
                "status": 5
            },
            {
                "types": 0,
                "leftcount": 0.0069,
                "fees": 0,
                "last": 0,
                "count": 0.0069,
                "successamount": 0,
                "source": "API",
                "type": 0,
                "price": 69817.52,
                "buysymbol": "",
                "id": 1228056,
                "time": "2018-03-07 11:15:33",
                "sellsymbol": "",
                "status": 5
            }
        ],
        "entrutsCur": [
            {
                "types": 1,
                "leftcount": 1,
                "fees": 0,
                "last": 0,
                "count": 1,
                "successamount": 0,
                "source": "WEB",
                "type": 1,
                "price": 1000000.00000000,
                "buysymbol": "",
                "id": 6799305,
                "time": "2018-04-13 21:31:04",
                "sellsymbol": "",
                "status": 1
            }
        ]
    }
}
返回参数说明 
参数名	类型	说明
status	String	1未成交,2部分成交，3完全成交，4撤单处理中，5已撤销
fcount	Double	下单数量
famount	Double	下单总价
fleftcount	Double	未成交数量
type	int	类型，0 买、1卖
备注 
更多返回错误代码请看首页的错误代码描述
11、发送绑定邮件的验证码
简要描述： 
发送绑定邮件的验证码
请求URL： 
https://域名/v1/email/send.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
token	是	string	令牌
address	是	string	邮件地址
返回示例
{
    "code": 200,
    "msg": "成功",
    "time": 1523696627416,
    "data": null
}
返回参数说明 
参数名	类型	说明
groupid	int	用户组id，1：超级管理员；2：普通用户
备注 
更多返回错误代码请看首页的错误代码描述
12、绑定邮件
简要描述： 
绑定邮件
请求URL： 
https://域名/v1/email/add.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
token	是	string	令牌
code	是	string	验证码
返回示例
{
    "code": 200,
    "msg": "success",
    "time": 1523699559213,
    "data": null
}
返回参数说明 
参数名	类型	说明
备注 
更多返回错误代码请看首页的错误代码描述
13、用到的网页URL
简要描述： 
用到的网页URL
费率： 
https://www.hotcoin.top/app/fee.html
货币资料： 
https://www.hotcoin.top/app/coin.html
帮助中心： 

https://www.hotcoin.top/app/help.html

备注 

更多返回错误代码请看首页的错误代码描述
14、人民币充值
简要描述： 
人民币充值接口
请求URL： 
http://crisscross.vip/v1/rmb/recharge.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
coinId	是	string	人民币id，css 里面好像是18
amount	是	string	金额
token	是	string	令牌
返回示例
  {
    "code": 200,
      "msg: "success",
    "data": {
      "seriesNumber": "1",
    }
  }
返回参数说明 
参数名	类型	说明
seriesNumber	String	预付订单号
备注 接口返回的预付订单号，调用这个url直接就可以了 https://pay.zgzop.com/cashier/{预付订单号} 
更多返回错误代码请看首页的错误代码描述
15、获取系统银行卡类型列表
简要描述： 
获取系统银行卡类型列表
请求URL： 
https://域名/v1/system/bankinfo.html?token=BCAPP_LOGIN_4df5bde009073d3ef60da64d736724d6_4005FA7DBCC44AE1B689BD64006A9222
请求方式：
GET 
参数： 
参数名	必选	类型	说明
token	是	string	令牌
返回示例
{
    "code": 200,
    "msg": "成功",
    "time": 1524134010336,
    "data": {
        "bankinfo": [
            {
                "fid": 1,
                "fcnname": "中国建设银行",
                "ftwname": "中国建设银行",
                "fenname": "CCB",
                "flogo": null,
                "ftype": 1,
                "fsort": 1,
                "fstate": true,
                "bankCode": "0"
            }
        ]
    }
}
返回参数说明 
参数名	类型	说明
groupid	int	用户组id，1：超级管理员；2：普通用户
备注 
更多返回错误代码请看首页的错误代码描述
16、绑定银行卡和人民币提现都需要这个验证码
简要描述： 
绑定银行卡和人民币提现都需要这个验证码
请求URL： 
https://域名/v1/user/send_bank_sms.html?token=BCAPP_LOGIN_4df5bde009073d3ef60da64d736724d6_4005FA7DBCC44AE1B689BD64006A9222
请求方式：
GET
参数： 
参数名	必选	类型	说明
token	是	string	令牌
返回示例
 {
    "code": 200,
    "msg": "成功",
    "time": 1524134897378,
    "data": null
}
返回参数说明 
参数名	类型	说明
备注 
更多返回错误代码请看首页的错误代码描述
17、自己已经绑定的银行卡
简要描述： 
自己已经绑定的银行卡
请求URL： 
https://域名/v1/user/bankinfo.html?symbol=9&token=BCAPP_LOGIN_4df5bde009073d3ef60da64d736724d6_4005FA7DBCC44AE1B689BD64006A9222
请求方式：
GET 
参数： 
参数名	必选	类型	说明
token	是	string	令牌
symbol	是	int	币id ，目前只有GSET，应该9注意哈。
返回示例
{
    "code": 200,
    "msg": "成功",
    "time": 1524138228100,
    "data": {
        "bankinfo": [
            {
                "fid": 173,
                "fuid": 10687,
                "fname": "中国建设银行",
                "fbanknumber": "6217003760035061910",
                "fbanktype": 1,
                "fbanktype_s": "中国建设银行",
                "fcreatetime": 1524137477000,
                "fstatus": 1,
                "version": 0,
                "init": true,
                "faddress": "万寿路",
                "frealname": "杨雪峰",
                "fprov": "重庆市",
                "fcity": "南岸区",
                "ftype": 0,
                "fdist": ""
            }
        ]
    }
}
返回参数说明 
参数名	类型	说明
备注 
更多返回错误代码请看首页的错误代码描述
18、绑定银行卡
简要描述： 
用户注册接口
请求URL： 
https://域名/v1/user/save_bankinfo.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
account	是	string	账号
phoneCode	是	string	手机验证码
totpCode	否	string	google验证码
openBankType	否	string	银行卡类型id，见：银行卡类型接口
address	否	string	详细地址
prov	否	string	省
city	否	string	市
dist	否	string	区
payeeAddr	否	string	开户人
token	否	string	令牌
返回示例
 {
    "code": 200,
    "msg": "成功",
    "time": 1524137477416,
    "data": null
}
返回参数说明 
参数名	类型	说明
备注 
更多返回错误代码请看首页的错误代码描述
19、人民币提现申请
简要描述： 
人民币提现申请
请求URL： 
https://域名/v1/user/cny_manual.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
tradePwd	是	string	交易密码
withdrawBalance	是	string	提现金额
totpCode	否	string	Google验证码
phoneCode	否	string	手机验证码
withdrawBlank	否	string	提现银行卡的ID
symbol	否	string	提现币的ID，目前只有GSET是人民币类的
token	否	string	令牌
返回示例
 {
    "code": 200,
    "msg": "提交成功",
    "time": 1524138190335,
    "data": null
}
返回参数说明 
参数名	类型	说明
备注 
更多返回错误代码请看首页的错误代码描述
20、人民币提现记录
简要描述： 
人民币提现记录，需要注意的是人民币和虚拟币的提现记录是不一样的
请求URL： 
https://域名/v1/user/cny_list.html?symbol=9&token=BCAPP_LOGIN_4df5bde009073d3ef60da64d736724d6_4005FA7DBCC44AE1B689BD64006A9222
请求方式：
GET
参数： 
参数名	必选	类型	说明
symbol	是	int	币id
token	是	string	令牌
返回示例
{
    "code": 200,
    "msg": "成功",
    "time": 1524138232397,
    "data": [
        {
            "fid": 359,
            "fsysbankid": 173,
            "fuid": 10687,
            "fcoinid": 9,
            "fcreatetime": 1524138190000,
            "famount": 99.5,
            "finouttype": 2,
            "ftype": 2,
            "ftype_s": "人民币提现",
            "fstatus": 1,
            "fstatus_s": "等待提现",
            "fremittancetype": null,
            "fremark": null,
            "fbank": "中国建设银行",
            "faccount": "6217003760035061910",
            "fpayee": "杨雪峰",
            "fbankCode": "0",
            "fphone": "15823488981",
            "fupdatetime": 1524138190000,
            "fadminid": null,
            "ffees": 0.5,
            "version": 0,
            "fischarge": null,
            "faddress": "重庆市 南岸区  万寿路",
            "fsource": 1,
            "fsource_s": "WEB",
            "fplatform": 1,
            "fplatform_s": "热币网",
            "fserialno": null,
            "floginname": null,
            "fnickname": null,
            "frealname": null,
            "fadminname": null,
            "fagentid": 0,
            "level": 0
        }
    ]
}
返回参数说明 
参数名	类型	说明
famount	double	实际金额
ffees	double	本次提现手续费
fstatus	int	状态，1等待提现、2正在处理、3提现成功、4用户取消、5等待银行到账。这个字段的具体中文显示，fstatus_s字段已经做了，可以直接取值显示。
备注 
更多返回错误代码请看首页的错误代码描述
21、获取相关币种的提现手续费
简要描述： 
获取相关币种的提现手续费
请求URL： 
https://域名/v1/system/fee.html?symbol=1&token=BCAPP_LOGIN_4df5bde009073d3ef60da64d736724d6_106B0CAD763C41C08B7FC18164A841EE
请求方式：
GET 
参数： 
参数名	必选	类型	说明
symbol	是	int	币种id
token	是	String	令牌
返回示例
{
    "code": 200,
    "msg": "成功",
    "time": 1524211646627,
    "data": {
        "withdrawSetting": {
            "id": 5,
            "coinId": 1,
            "levelVip": 4,
            "withdrawMax": 0,
            "withdrawMin": 0.0001,
            "withdrawFee": 0.001,
            "withdrawTimes": 5,
            "withdrawDayLimit": 20,
            "gmtCreate": 1506482238000,
            "gmtModified": 1520250081000,
            "version": 2
        }
    }
}
返回参数说明 
参数名	类型	说明
withdrawFee	double	手续费
备注 
更多返回错误代码请看首页的错误代码描述
22、获取 QQ 充值客服
简要描述： 
获取 QQ 充值客服
请求URL： 
https://域名/v1/user/qq.html?token=BCAPP_LOGIN_4df5bde009073d3ef60da64d736724d6_106B0CAD763C41C08B7FC18164A841EE
请求方式：
POST 
参数： 
参数名	必选	类型	说明
token	是	string	令牌
返回示例
{
    "code": 200,
    "msg": "成功",
    "time": 1524220186120,
    "data": [
        "2742369241",
        "1022265830",
        "1874078024"
    ]
}
返回参数说明 
参数名	类型	说明
groupid	int	用户组id，1：超级管理员；2：普通用户
备注 
更多返回错误代码请看首页的错误代码描述
23、找回密码验证码
简要描述： 
手机端找回密码短信
请求URL： 
/v1/validate/send.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
area	是	string	区域号 例如：86
phone	是	string	手机号 例如：19923467896
type	否	int	业务类型 112手机端注册、102绑定手机、109找回登录密码-手机找回、107找回交易密码
返回示例
 {
    "code": 200,
    "msg": "成功",
    "time": 1535967297821,
}
备注 
更多返回错误代码请看首页的错误代码描述
24、手机端修改密码
简要描述： 
手机短信找回密码
请求URL： 
/v1/user/resetpwd.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
phone	是	string	手机号
area	是	string	区域号
code	否	string	验证码
totpCode	否	string	Google 验证码
newPassword	否	string	密码
newPassword2	否	string	确认密码
返回示例
{
    "code": 200,
    "msg": "成功",
    "time": 1535967311884,
}
备注 
更多返回错误代码请看首页的错误代码描述
25、找回交易密码接口
简要描述： 
找回交易密码接口
请求URL： 
/v1/user/findtradepwd.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
phone	是	string	手机号
area	是	string	区域号
code	是	string	验证码
totpCode	否	string	Google 验证码（选填）
newPassword	是	string	密码
newPassword2	是	string	密码
token	是	string	令牌
返回示例
  {
    "code": 200,
    "msg":"成功"
  }
返回参数说明 
参数名	类型	说明
groupid	int	用户组id，1：超级管理员；2：普通用户
备注 
更多返回错误代码请看首页的错误代码描述
26、设置/修改 交易密码和支付密码
/v1/user/password.html
code： 300 需要身份证号（不要根据code判断） 1009 需要绑定手机或google验证码
参数： params.put("token", appKey); params.put("originPwd", oldPassword); params.put("newPwd", password); params.put("reNewPwd", password_); params.put("phoneCode", messageCode); params.put("totpCode", googleCode); params.put("identityCode", idCard); params.put("pwdType", String.valueOf(pwdType)) 0:修改登录密码，1：设置或修改交易密码都可以 
27、登录
用户注册接口
请求URL： 
http://xx./v1/user/login.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
loginName	是	string	用户名
password	是	string	密码
type	是	string	1：手机 2：邮箱
area	是	string	地区码
返回示例
  {
    "error_code": 0,
    "data": {
      "uid": "1",
      "username": "12154545",
      "name": "吴系挂",
      "groupid": 2 ,
      "reg_time": "1436864169",
      "last_login_time": "0",
    }
  }
返回参数说明 
参数名	类型	说明
groupid	int	用户组id，1：超级管理员；2：普通用户
备注 
更多返回错误代码请看首页的错误代码描述
28、发送不需要签名的短信 POST (非签名)
简要描述： 
发送不需要签名的短信 POST (非签名)
请求URL： 
http://xx.com/v1/validate/send.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
type	是	string	112手机端注册，102绑定手机
area	是	string	地区编码
phone	是	string	电话号码
返回示例
  {
    "code": 300,
    "msg": "已经存在了",
    "time": 1550630673807,
    "data": null
}
备注 
更多返回错误代码请看首页的错误代码描述
29、手机注册 POST (非签名)
简要描述： 
手机注册 POST (非签名)
请求URL： 
http://xx.com/v1/user/register.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
regName	是	string	用户名
password	是	string	密码
regType	是	string	0固定的
pcode	是	string	短信验证码
ecode	是	string	空字符串（固定的）
area	是	string	地区编码
intro_user	是	string	邀请码
返回示例
  {
    "error_code": 0,
    "data": {
      "uid": "1",
      "username": "12154545",
      "name": "吴系挂",
      "groupid": 2 ,
      "reg_time": "1436864169",
      "last_login_time": "0",
    }
  }
返回参数说明 
参数名	类型	说明
groupid	int	用户组id，1：超级管理员；2：普通用户
备注 
更多返回错误代码请看首页的错误代码描述
30、虚拟币提现
简要描述： 
虚拟币提现
请求URL： 
/v1/coin/withdraw.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
withdrawAddr	是	int	地址id
withdrawAmount	是	string	提现数量
symbol	是	string	币种
totpCode	是	string	google验证码
phoneCode	是	string	手机号码
memo	是	string	备注
返回示例
 {
    "code": 200,
    "msg": "提交成功",
    "time": 1523515590145,

}
备注 
更多返回错误代码请看首页的错误代码描述
31、获取工单类型
简要描述： 
获取工单类型列表
请求URL： 
/v1/question/type.html
请求方式：
GET
参数： 
返回示例
{
    "code": 200,
    "msg": "成功",
    "time": 1559620898278,
    "data": [
        {
            "code": 1,
            "value": "充值/提现问题"
        },
        {
            "code": 2,
            "value": "业务问题"
        },
        {
            "code": 3,
            "value": "绑定问题"
        },
        {
            "code": 4,
            "value": "其他问题"
        },
        {
            "code": 5,
            "value": "活动相关"
        }
    ]
}
返回参数说明 
备注 
更多返回错误代码请看首页的错误代码描述
32、提交工单
简要描述： 
提交工单
请求URL： 
/v1/question/submit.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
token	是	string	凭证
questiontype	是	int	工单类型id，参见工单类型列表接口
questiondesc	是	string	工单描述
返回示例
{
    "code": 200,
    "msg": "成功",
    "time": 1559620898278
    }
返回参数说明 
参数名	类型	说明
groupid	int	用户组id，1：超级管理员；2：普通用户
备注 
更多返回错误代码请看首页的错误代码描述
33、获取工单列表
简要描述： 
获取工单列表
请求URL： 
/v1/question/list.html
请求方式：
GET
参数： 
参数名	必选	类型	说明
token	是	string	凭证
返回示例
  {
    "code": 200,
    "msg": "成功",
    "time": 1559624683272,
    "data": [
        {
            "fid": 1359,
            "ftype": 1,
            "ftype_s": "充值/提现问题",
            "ftelephone": "15823488981",
            "fuid": 43310,
            "fstatus": 1,
            "fstatus_s": "未解决",
            "fcreatetime": 1559624675000,
            "fupdatetime": null,
            "faid": null,
            "fname": null,
            "version": 0,
            "fcid": null,
            "fisanswer": 0,
            "fdesc": "测试",
            "fanswer": null,
            "fadmin": null
        }
    ]
}
返回参数说明 
参数名	类型	说明
ftype	int	工单类型，参见工单类型类别
ftype_s	string	工单类型文字说明
fstatus	int	状态，1：未解决；2：解决；3：删除
fstatus_s	string	状态说明
fisanswer	int	是否回复，0：未回复；1：已回复
fanswer	string	回复内容
备注 
更多返回错误代码请看首页的错误代码描述
34、删除工单
简要描述： 
删除工单接口
请求URL： 
/v1/question/del.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
fid	是	int	工单id
token	是	string	凭证
返回示例
  {
    "code": 200,
    "msg": "成功",
    "time": 1559632714748,
    "data": null
}
返回参数说明 
参数名	类型	说明
备注 
更多返回错误代码请看首页的错误代码描述
35、删除提现地址
简要描述： 
删除提现地址接口
请求URL： 
http://xx.com/v1/coin/address/del.html
请求方式：
POST 
参数： 
参数名	必选	类型	说明
addressId	是	int	地址 Id
token	是	string	令牌
返回示例
  {
    "code": 200,
    "msg":"成功"
  }
返回参数说明 
参数名	类型	说明
groupid	int	用户组id，1：超级管理员；2：普通用户
备注 
更多返回错误代码请看首页的错误代码描述