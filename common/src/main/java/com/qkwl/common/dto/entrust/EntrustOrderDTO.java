package com.qkwl.common.dto.entrust;

import com.qkwl.common.dto.Enum.EntrustSourceEnum;
import com.qkwl.common.dto.Enum.MatchTypeEnum;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 委单数据传输
 *
 * @author LY
 */
public class EntrustOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 委单撮合类型
    private MatchTypeEnum matchType;
    // 用户ID
    private Integer userId;
    // 交易类型ID
    private Integer tradeId;
    // 来源
    private EntrustSourceEnum source;
    // 价格
    private BigDecimal prize;
    // 数量
    private BigDecimal count;
    // 交易密码
    private String tradePass;
    // IP地址
    private String ip;

    public EntrustOrderDTO(MatchTypeEnum matchType, Integer userId, Integer tradeId, EntrustSourceEnum source,
                           BigDecimal prize, BigDecimal count, String tradePass, String ip) {
        this.matchType = matchType;
        this.userId = userId;
        this.tradeId = tradeId;
        this.source = source;
        this.prize = prize;
        this.count = count;
        this.tradePass = tradePass;
        this.ip = ip;
    }

    public MatchTypeEnum getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchTypeEnum matchType) {
        this.matchType = matchType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }

    public EntrustSourceEnum getSource() {
        return source;
    }

    public void setSource(EntrustSourceEnum source) {
        this.source = source;
    }

    public BigDecimal getPrize() {
        return prize;
    }

    public void setPrize(BigDecimal prize) {
        this.prize = prize;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public String getTradePass() {
        return tradePass;
    }

    public void setTradePass(String tradePass) {
        this.tradePass = tradePass;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "EntrustDTO{" +
                "matchType=" + matchType.getValue() +
                ", userId=" + userId +
                ", tradeId=" + tradeId +
                ", source=" + source.getValue() +
                ", prize=" + prize +
                ", count=" + count +
                ", tradePass='" + tradePass + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }

}
