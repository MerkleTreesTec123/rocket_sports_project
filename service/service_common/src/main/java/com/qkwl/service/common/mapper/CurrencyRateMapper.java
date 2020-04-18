package com.qkwl.service.common.mapper;

import com.qkwl.common.dto.wallet.CurrencyRate;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CurrencyRateMapper {

    @Select("SELECT * FROM currency_rate")
    List<CurrencyRate> getAllCurrencyRate();

    @Update("UPDATE currency_rate set update_time = #{updateTime},rate = #{rate} WHERE currency = #{currency}")
    int update(CurrencyRate currencyRate);

    @Select("SELECT * FROM currency_rate WHERE currency = #{currency}")
    CurrencyRate get(String currency);

    @Insert("INSERT INTO currency_rate(currency,rate,update_time) VALUES(#{currency},#{rate},#{updateTime})")
    int insert(CurrencyRate currencyRate);

}
