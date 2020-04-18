package com.qkwl.admin.layui.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.LimitTypeEnum;
import com.qkwl.common.framework.limit.LimitHelper;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.util.ReturnResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ZKF on 2017/7/15.
 */
@Controller
public class LimitController {

    private static Logger logger = LoggerFactory.getLogger(LimitController.class);

    @Autowired
    private MemCache memCache;

    @RequestMapping(value = "/admin/limitList")
    public ModelAndView limitList(
            @RequestParam(value = "ip", required = false, defaultValue = "") String ip,
            @RequestParam(value = "type", required = true, defaultValue = "0") int type){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("security/countLimitList");

        JSONArray array = new JSONArray();
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(0, "全部");
        for(LimitTypeEnum e: LimitTypeEnum.values()){
            map.put(e.getCode(), e.getValue());
            String key = LimitHelper.prefix + ip + "_" + e.getCode();
            Integer limit = getLimit(key);
            if(limit != null){
                JSONObject obj = new JSONObject();
                obj.put("key", ip + "_" + e.getCode());
                obj.put("ip", ip);
                obj.put("type", e.getCode());
                obj.put("type_s", e.getValue());
                obj.put("limit", limit);
                array.add(obj);
            }
        }

        modelAndView.addObject("ip", ip);
        modelAndView.addObject("type", type);
        modelAndView.addObject("array", array);
        modelAndView.addObject("typeMap", map);
        return modelAndView;
    }

    @RequestMapping(value = "/admin/deleteLimit")
    @ResponseBody
    public ReturnResult deleteLimit(
            @RequestParam(value = "key", required = false, defaultValue = "") String key
    ){
        try{
            key = LimitHelper.prefix + key;
            memCache.delete(LimitHelper.db, key);
            return ReturnResult.SUCCESS("解除成功！");
        } catch (Exception e){
            logger.error("删除ip限制异常，key:"+key, e);
            return ReturnResult.FAILUER("解除失败！");
        }
    }

    /**
     * 获取缓存数据
     */
    private Integer getLimit(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        String rest = memCache.get(LimitHelper.db, key);
        if (StringUtils.isEmpty(rest)) {
            return null;
        }
        return Integer.valueOf(rest);
    }

}
