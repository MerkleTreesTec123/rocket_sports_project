layui.define(['jquery', 'layer', 'ajax', 'laypage','form','grid'], function (exports) {
    "use strict";
    var $ = layui.jquery, layer = layui.layer, ajax = layui.ajax(), MOD_NAME = "lookup", grid = layui.grid,
        _lookup = "", layerIndex = 0, lookup = {
            openLook: function (that) {
                var $that = $(that), lay_href = $that.attr("lay-href");
                if (typeof (lay_href) === "undefined") {
                    layer.msg("链接错误！", {
                        icon: 2
                    });
                    return;
                }
                ajax.post(lay_href, {}, function (result) {
                    _lookup = $that;
                    layerIndex = layer.open({
                                                title: "查找带回",
                                                type: 1,
                                                skin: 'layui-layer-rim', // 加上边框
                                                area: ['800px', '600px'], // 宽高
                                                content: result,
                                                success: function (layero) {
                                                    grid.render(layero.find(".layui-layer-content"));
                                                }
                                            });
                });

            },
            backLook: function (options) {
                var parents = _lookup.parents(".layui-form-item");
                layui.each(options, function (key, value) {
                    parents.find("input[lay-lookup=\"" + key + "\"]").val(value);
                });
                layer.close(layerIndex);
                _lookup = "";
                layerIndex = 0;
            },
        };
    exports(MOD_NAME, lookup);
});