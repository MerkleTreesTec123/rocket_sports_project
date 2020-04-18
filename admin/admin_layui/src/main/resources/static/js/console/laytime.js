/**
 * Created by ZKF on 2017/4/5.
 */
layui.define(['laydate'], function(exports) {
    "use strict";

    var $ = layui.jquery, laydate = layui.laydate, MOD_NAME = "laytime",
        laytime = {
            format: function (that) {
                laydate({
                    elem: that,
                    istime: true,
                    format: 'YYYY-MM-DD hh:mm:ss',
                    choose: function (dates) { //选择好日期的回调
                        var time = dates.split(" ")[1];
                        $(that).val(time);
                    }
                });
            }
        }

    exports(MOD_NAME, laytime);
});