/**
 * Created by LY on 2017/3/22.
 */
layui.define(['jquery', 'layer'], function (exports) {
    var $ = layui.jquery
        , layer = layui.layer
        , MOD_NAME = "ajax"
        , SUCCESS = 200
        , Ajax = function () {
        this.config = {}
    }
    Ajax.prototype.set = function (options) {
        var that = this;
        $.extend(true, that.config, options);
        return that;
    };
    Ajax.prototype.post = function (url, param, callback) {
        var cb = function (result) {
            if (result.code == 401) {
                layer.alert(result.msg, {
                                title: "错误",
                                icon: 2
                            }, function (index) {
                               window.location.href="/admin/login.html";
                            }
                );
                return;
            }
            callback.call(this, result);
        }
        $.post(url, param, cb);
    },
        Ajax.prototype.get = function (url, callback) {
            var cb = function (result) {
                if (result.code == 401) {
                    layer.alert(result.msg, {
                        title: "错误",
                        icon: 2
                    }, function (index) {
                        window.location.href="/admin/login.html";
                    });
                    return;
                }
                callback.call(this, result);
            }
            $.get(url, cb);
        }

    var ajax = new Ajax();
    exports(MOD_NAME, function (options) {
        return ajax.set(options);
    });
});