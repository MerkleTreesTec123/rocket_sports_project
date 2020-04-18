package com.qkwl.web.front.controller;


import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.dto.Enum.SystemTradeBlockEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.coin.SystemTradeTypeVO;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.model.KeyValues;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.front.comm.AutoCache;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.utils.WebConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FrontIndexJsonController extends JsonBaseController {

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private AutoCache autoCache;
    @ResponseBody
    @RequestMapping(value = "/")
    public String checkHealth() {
        return "Good!";
    }

    @ResponseBody
    @RequestMapping(value = "/heath/index")
    public String checkHealthIndex() {
        return "Good!";
    }

    @ResponseBody
    @RequestMapping(value = "/index_json")
    public ReturnResult indexs() throws Exception {
        //现有交易板块
        Map<Integer, Object> blockMap = new LinkedHashMap<>();
        // 交易分类
        Map<Integer, Object> typeMap = new LinkedHashMap<>();
        //交易区对应的交易对
        Map<Integer, Map<Integer, List<SystemTradeTypeVO>>> tradeBlockListMap = new LinkedHashMap<>();

        //所有交易对
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
        LocaleEnum lanEnum = getLanEnum();
        for (SystemTradeBlockEnum blockEnum : SystemTradeBlockEnum.values()) {
            if (lanEnum.getCode().equals(LocaleEnum.EN_US.getCode())) {
                blockMap.put(blockEnum.getCode(), blockEnum.getEnglishName());
            } else {
                blockMap.put(blockEnum.getCode(),blockEnum.getValue());
            }
            //交易区对应的交易对
            Map<Integer, List<SystemTradeTypeVO>> tradeTypeListMap = new LinkedHashMap<>();
            for (SystemTradeTypeEnum typeEnum : SystemTradeTypeEnum.values()) {
                if (typeEnum.getSymbol().equals("ETH") || typeEnum.getSymbol().equals("BTC")) continue;
                typeMap.put(typeEnum.getCode(), typeEnum.getSymbol());
                List<SystemTradeType> tempTradeTypeList = new ArrayList<>();
                for (SystemTradeType stt : tradeTypeList) {
                    if (stt.getType() == typeEnum.getCode() && stt.getTradeBlock() == blockEnum.getCode()) {
                        stt.setBuyShortName(stt.getBuyShortName().toLowerCase());
                        stt.setSellShortName(stt.getSellShortName().toLowerCase());
                        tempTradeTypeList.add(stt);
                    }
                }
                tradeTypeListMap.put(typeEnum.getCode(), ModelMapperUtils.mapper(tempTradeTypeList, SystemTradeTypeVO.class));
            }
            tradeBlockListMap.put(blockEnum.getCode(),tradeTypeListMap);
        }

        Map<Integer, String> SymbolMap = new LinkedHashMap<>();
        for (SystemTradeTypeEnum typeEnum : SystemTradeTypeEnum.values()) {
            typeMap.put(typeEnum.getCode(), typeEnum.getValue());
            SymbolMap.put(typeEnum.getCode(), typeEnum.getSymbol());
        }

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("typeFirst", SystemTradeTypeEnum.USDT.getCode());
        jsonObject.put("blockFirst", SystemTradeBlockEnum.MAIN.getCode());
        jsonObject.put("tradeTypeListMap", tradeBlockListMap);
        jsonObject.put("tradeTypeList",tradeTypeList);
        jsonObject.put("typeMap", typeMap);
        jsonObject.put("blockMap", blockMap);
        jsonObject.put("SymbolMap", SymbolMap);
        // bank info

        return ReturnResult.SUCCESS(jsonObject);
    }

    @ResponseBody
    @RequestMapping(value = "/articles_json")
    public ReturnResult getarticles(@RequestParam(required = false, defaultValue = "1") Integer locale) throws Exception {
        //en,cn,tw
        String localeStr = "zh_TW";
        switch (locale) {
            case 1:
                localeStr = "zh_TW";
                break;  //繁体
            case 2:
                localeStr = "en_US";
                break;
            case 3:
                localeStr = "zh_CN";
                break;
        }

        List<KeyValues> articles = autoCache.getArticles(localeStr);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("articles", articles);
        // bank info
        return ReturnResult.SUCCESS(jsonObject);
    }

}
