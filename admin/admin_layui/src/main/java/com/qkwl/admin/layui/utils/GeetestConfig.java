package com.qkwl.admin.layui.utils;

public class GeetestConfig {

    // 填入自己的captcha_id和private_key
    private static final String geetest_id = "8d070b041aae40fbd475c86467f9d796";
    private static final String geetest_key = "2984b183220f24baea029e8739697e9e";
    private static final boolean newfailback = true;

    public static final String getGeetest_id() {
        return geetest_id;
    }

    public static final String getGeetest_key() {
        return geetest_key;
    }

    public static final boolean isnewfailback() {
        return newfailback;
    }

}
