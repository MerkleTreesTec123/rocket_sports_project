layui.define(['jquery', 'ajax', 'layer'], function (exports) {
    "use strict";

    var $ = layui.jquery
        , layer = layui.layer
        , ajax = layui.ajax()
        , layform = layui.form()
        , laypage = layui.laypage
        , MOD_NAME = "grid"
        , grid = function () {
        this.config = {
            cache: {
                eventInit: false, //事件初始化状态
                othis:null, //当前操作网格对象
                layerIndex:0 //关闭layer弹窗
            }
        }
    }, _self = new grid();
    /**
     * 初始化加载
     */
    grid.prototype.init = function (lay_id) {
        var parents = $(".layui-tab[lay-filter=right_tab]")
            , cur = parents.find("li[lay-id=" + lay_id + "]").index()
            , item = parents.children('.layui-tab-content').children('.layui-tab-item')
            , othis = item.eq(cur);
        // 赋值唯一ID
        othis.attr("lay-id", lay_id);
        // 渲染
        _self.render(othis);

    };
    /**
     * 网格渲染
     */
    grid.prototype.render = function (othis) {
        // 表格渲染
        _self.table().render(othis);
        // 初始化事件
        _self.sort().init(othis);
        // 初始化排序
        _self.onEvent().init(_self.config);
        // 绑定滚动条
        _self.onEvent().scroller(othis);
        // 绑定选择事件
        _self.onEvent().checkbox(othis);
        // 初始化查询
        _self.search().init(othis);
        // 初始化按钮
        _self.button().init(othis);
        // 初始化分页
        _self.page().init(othis);
        // 初始化resize
        if (typeof (othis.attr("lay-id")) !== "undefined") {
            _self.resize(othis.attr("lay-id"));
        }
    };
    /**
     * 网格缩放
     */
    grid.prototype.resize = function (lay_id) {
        var parents, othis, height;
        if (typeof (lay_id) === "object") {
            parents = $(".layui-tab[lay-filter=right_tab]");
            othis = parents.find(".layui-form");
        } else {
            parents = $(".layui-tab[lay-filter=right_tab]");
            var cur = parents.find("li[lay-id=" + lay_id + "]").index(),
                item = parents.children('.layui-tab-content').children('.layui-tab-item');
            othis = item.eq(cur)
        }
        height = parents.outerHeight();
        height = height - parents.find(".layui-tab-title").outerHeight(true);
        parents.find(".layui-tab-content").height(height - 20);
        othis.each(function (index, item) {
            var $item = $(item), bodyHeight = height, $tableBody = $item.find(".layui-table-body");
            $tableBody.each(function () {
                var $tableBodyThis = $(this), lay_height = $tableBodyThis.attr("lay-height");
                if (lay_height) {
                    $tableBodyThis.height(lay_height);
                } else {
                    bodyHeight = bodyHeight - $item.find(".layui-search-box").outerHeight(true);
                    bodyHeight = bodyHeight - $item.find("hr").outerHeight(true);
                    bodyHeight = bodyHeight - $item.find(".layui-btn-box").outerHeight(true);
                    bodyHeight = bodyHeight - $item.find(".layui-laypage-box").outerHeight(true);
                    bodyHeight = bodyHeight - $item.find(".layui-table-head").outerHeight(true);
                    $tableBodyThis.height(bodyHeight - 10);
                }
            })
        });
        $(window).on("resize", function () {
            _self.resize(lay_id);
        });
    }
    /**
     * 表格相关
     */
    grid.prototype.table = function () {
        /**
         * 渲染表格
         * @param othis 表格父级对象
         */
        function render(othis) {
            var $table = othis.find(".layui-table")
                , isAddEmpty = true;
            layform.render('checkbox');
            $table.each(function () {
                var $tableThis = $(this)
                    , newTLength = 0
                    , aStyles = []
                    , boxWidth = othis.find(".layui-conten-box").outerWidth() - 20
                    , oldThs = $tableThis.find("thead>tr:last-child").find("th")
                    , autoWidthLength = 0;
                oldThs.each(function () {
                    var $th = $(this), style = [],
                        width = _self.sort().initWidth($th, $th.outerWidth());
                    if (typeof ($th.attr("width")) !== "undefined") {
                        width = $th.attr("width");
                        if (width === "*") {
                            width = 0;
                            autoWidthLength++;
                        }
                        $th.removeAttr("width");
                    }
                    style[0] = parseInt(width);
                    newTLength += parseInt(width);
                    aStyles[aStyles.length] = style;
                });
                var $thead = $tableThis.find("thead")
                    , $tbody = $tableThis.find("tbody")
                    , lay_heigth = $tbody.attr("lay-height");
                $thead.wrap(
                    "<div class='layui-table-head'><table class='layui-table' style='width:"
                    + boxWidth + "px;table-layout:fixed;'></table></div>");
                $tbody.wrap(
                    "<div class='layui-table-body' " + (typeof (lay_heigth) === "undefined" ? ""
                        : "lay-height='" + lay_heigth + "'") + "><table class='layui-table' style='width:"
                    + boxWidth + "px;table-layout:fixed;'></table></div>");
                var lastTh = $(">tr:last-child>th", $thead);
                var firstTd = $(">tr:first-child>td", $tbody);
                var allTd = $(">tr>td", $tbody);
                allTd.each(function () {
                    if (this.children.length > 0) {
                        return;
                    }
                    var $td = $(this);
                    $td.html("<div>" + $td.html() + "</div>");
                });
                lastTh.each(function (i) {
                    if (aStyles[i][0] === 0) {
                        isAddEmpty = false;
                        this.style.width = (boxWidth - newTLength - 1) / autoWidthLength + "px";
                    } else {
                        this.style.width = aStyles[i][0] + "px";
                    }
                });
                firstTd.each(function (i) {
                    if (aStyles[i][0] === 0) {
                        isAddEmpty = false;
                        this.style.width = (boxWidth - newTLength - 1) / autoWidthLength + "px";
                    } else {
                        this.style.width = aStyles[i][0] + "px";
                    }
                });
                // 处理长度不够问题
                var $theadTr;
                if (newTLength < boxWidth && isAddEmpty) {
                    $theadTr = $(">tr", $thead);
                    $theadTr.each(function () {
                        $(this).append("<th style='width:" + (boxWidth - newTLength - 1) + "px'></th>");
                    });
                    var $tbodyTr = $(">tr", $tbody);
                    $tbodyTr.each(function () {
                        $(this).append("<td style='width:" + (boxWidth - newTLength - 1) + "px'></td>");
                    });
                } else {
                    $theadTr = $(">tr>th:last-child", $thead);
                    if ($theadTr.html() === "") {
                        $theadTr.remove();
                        $(">tr>td:last-child", $tbody).remove();
                    }
                }
                $tableThis.parent().html($tableThis.html());
            });
        }

        return {
            render: render
        }
    };
    /**
     *  事件
     */
    grid.prototype.onEvent = function () {
        /**
         * 表格滚动条事件
         * @param othis 表格父级对象
         */
        function scroller(othis) {
            var scroller = othis.find(".layui-table-body");
            scroller.each(function () {
                var $scrollerThis = $(this);
                $scrollerThis.scroll(function () {
                    var header = $scrollerThis.parents(".layui-conten-box").find(".layui-table-head");
                    if ($scrollerThis.scrollLeft() > 0) {
                        header.css("position", "relative");
                        var scroll = $scrollerThis.scrollLeft();
                        header.css("left", 0 - scroll);
                    }
                    if ($scrollerThis.scrollLeft() === 0) {
                        header.css("position", "relative");
                        header.css("left", "0px");
                    }
                    return false;
                });
            });
        }

        /**
         * 表格多选框事件
         * @param othis 表格父级对象
         */
        function checkbox(othis) {
            var $table = othis.find(".layui-table-body .layui-table");
            $table.find("tr").on("click", function (event) {
                if (event.target.localName !== "td") {
                    return;
                }
                var checkbox = $(this).find('input[type="checkbox"]');
                if (checkbox.length > 0) {
                    checkbox[0].checked = !checkbox[0].checked;
                    $table.find("tr[class='activate']").removeClass("activate")
                    if(checkbox[0].checked){
                        $(this).addClass("activate")
                    }
                    layui.event("form", 'checkbox', {
                        elem: checkbox[0]
                    });
                }
            });
            $table.find("div").on("click", function (event) {
                if (event.target.localName !== "div") {
                    return;
                }
                var checkbox = $(this).parents("tr").find('input[type="checkbox"]');
                if (checkbox.length > 0) {
                    checkbox[0].checked = !checkbox[0].checked;
                    $table.find("tr[class='activate']").removeClass("activate")
                    if(checkbox[0].checked){
                        $(this).parents("tr").addClass("activate")
                    }
                    layui.event("form", 'checkbox', {
                        elem: checkbox[0]
                    });
                }
            });
            $table.find("span").on("click", function (event) {
                if (event.target.localName !== "span") {
                    return;
                }
                var checkbox = $(this).parents("tr").find('input[type="checkbox"]');
                if (checkbox.length > 0) {
                    checkbox[0].checked = !checkbox[0].checked;
                    $table.find("tr[class='activate']").removeClass("activate")
                    if(checkbox[0].checked){
                        $(this).parents("tr").addClass("activate")
                    }
                    layui.event("form", 'checkbox', {
                        elem: checkbox[0]
                    });
                }
            });
        }

        /**
         * 初始化表格相关事件
         * @param config 网格配置
         */
        function init(config) {
            if (config.cache.eventInit) {
                return;
            }
            config.cache.eventInit = true;
            layform.on('checkbox(allChoose)', function (data) {
                var child = $(data.elem).parents('.layui-conten-box')
                    .find('input[type="checkbox"]');
                child.each(function (index, item) {
                    item.checked = data.elem.checked;
                });
                layform.render('checkbox');
            });
            layform.on('checkbox', function (data) {
                var othis = $(data.elem), parents = othis.parents(".layui-tab-item"),
                    allChoose = parents.find('.layui-table-head input[type="checkbox"][lay-filter="allChoose"]');
                if (othis.attr("lay-filter") === "allChoose") {
                    return;
                }
                var checked = data.elem.checked;
                if (allChoose.length <= 0) {
                    var child = othis.parents('.layui-conten-box').find('input[type="checkbox"]');
                    child.each(function (index, item) {
                        item.checked = false;
                    });
                }
                data.elem.checked = checked;
                layform.render('checkbox');
            });
        }

        return {
            init: init,
            scroller: scroller,
            checkbox: checkbox
        }
    };
    /**
     * 排序相关
     */
    grid.prototype.sort = function () {
        /**
         * 初始化排序
         * @param othis 表格父级对象
         */
        function init(othis) {
            var $sortTh = othis.find(".layui-table th[orderField]"),
                searchbox = othis.find(".layui-search-box");
            if ($sortTh.length <= 0) {
                return;
            }
            $sortTh.each(function(){
                var direction = $(this).attr("orderDirection"),
                    field = $(this).attr("orderField");
                if(direction!=="" && typeof (direction) !== "undefined" && field!=="" && typeof (field) !== "undefined"){
                    searchbox.append('<input type="hidden" id="orderField" name="orderField" value="' + field + '"/>');
                    searchbox.append('<input type="hidden" id="orderDirection" name="orderDirection" value="' + direction + '"/>');
                }
            });
            $sortTh.on("click", function () {
                orderBy($(this), othis)
            });
        }

        /**
         * 初始化排序列宽度
         * @param $that 当前列jQuery对象
         * @param width 当前列宽度
         */
        function initWidth($that, width) {
            if (typeof $that.attr("orderField") === "undefined") {
                return width;
            }
            $that.css("cursor", "pointer");
            var $span = $('<span>').hide().text($that.text());
            $that.append($span);
            var widthSpan = $span.width();
            $span.remove();
            if (widthSpan + 20 > width) {
                width = width + 20;
            }
            if ($that.attr("orderDirection") === "asc") {
                $that.append('<i class="layui-edge asc"></i>');
            }
            if ($that.attr("orderDirection") === "desc") {
                $that.append('<i class="layui-edge desc"></i>');
            }
            return width;
        }

        /**
         * 排序
         * @param $that 当前点击对象
         * @param othis 表格父级对象
         */
        function orderBy($that, othis) {
            var searchbox = othis.find(".layui-search-box"),
                field = $that.attr("orderField") || '',
                direction = $that.attr("orderDirection") || '',
                $field = searchbox.find("#orderField"),
                $direction = searchbox.find("#orderDirection");
            if (direction.toLocaleLowerCase() === "asc") {
                direction = "desc";
            } else if (direction.toLocaleLowerCase() === "desc") {
                direction = "asc";
            } else {
                direction = "desc";
            }
            if ($field.length <= 0) {
                searchbox.append('<input type="hidden" id="orderField" name="orderField" value="' + field + '"/>');
            } else {
                $field.val(field);
            }
            if ($direction.length <= 0) {
                searchbox.append(
                    '<input type="hidden" id="orderDirection" name="orderDirection" value="' + direction + '"/>');
            } else {
                $direction.val(direction);
            }
            _self.search().refresh(othis);
        }

        return {
            init: init,
            initWidth: initWidth
        }
    };
    /**
     * 查询相关
     */
    grid.prototype.search = function () {
        /**
         * 初始化查询
         * @param othis 表格父级对象
         */
        function init(othis) {
            var searchBox = othis.find(".layui-search-box"),
                searchBtn = searchBox.find("button");
            searchBtn.on("click", function () {
                refresh(othis);
            });
            searchBox.on("click", function () {
                document.onkeydown = function (event) {
                    var e = event || window.event;
                    if (e && e.keyCode === 13) {
                        return searchBtn.click();
                    }
                };
            });
            layform.render();
        }

        /**
         * 查询数据刷新
         * @param othis 表格父级对象
         * @param curr 当前页数
         * @param cb 刷新后回掉函数
         */
        function refresh(othis, curr, cb) {
            var content = othis.find(".layui-form")
                , href = content.attr("lay-href")
                , param = params(othis, curr)
                , layerIndex = layer.load(2);
            $.post(href, param, function (result) {
                othis.html(result);
                if (typeof (cb) === "function") {
                    cb.call(this);
                }
                _self.render(othis);
                layer.close(layerIndex);
            });
        }

        /**
         * 查询参数
         * @param othis 表格父级对象
         * @param curr 当前页数
         * @returns {{}} 参数json对象
         */
        function params(othis, curr) {
            var searchbox = othis.find(".layui-search-box")
                , param = {};
            // pages
            if (typeof (curr) !== "undefined") {
                param.pageNum = curr;
            }
            // input
            var searchInput = searchbox.find("input");
            $.each(searchInput, function (key, value) {
                var $value = $(value)
                    , name = $value.attr("name")
                    ,type = $value.attr("type");
                if (typeof (name) === "undefined") {
                    return;
                }
                if(type === "checkbox"){
                    param[name] = $value.prop("checked")?'on':'off';
                } else {
                    param[name] = $value.val();
                }
            });
            // select
            var searchSelect = searchbox.find("select");
            $.each(searchSelect, function (key, value) {
                var $value = $(value);
                var name = $value.attr("name");
                if (typeof (name) === "undefined") {
                    return;
                }
                param[name] = $value.val();
            });

            return param;
        }

        return {
            init: init,
            params: params,
            refresh: refresh
        }
    };
    /**
     * 按钮相关
     */
    grid.prototype.button = function () {
        /**
         * 初始化按钮
         * @param othis 表格父级对象
         */
        function init(othis) {
            var buttonBtn = othis.find(".layui-btn-box button");
            buttonBtn.on("click", function () {
                buttonEvent($(this), othis);
            });
        }

        /**
         * 按钮事件
         * @param $that 当前按钮jQuery对象
         * @param othis 表格父级对象
         */
        function buttonEvent($that, othis) {
            var lay_target = $that.attr("lay-target"),
                lay_tips = $that.attr("lay-tips"),
                lay_href = $that.attr("lay-href"),
                lay_checked = $that.attr("lay-checked"),
                lay_filter = $that.attr("lay-filter"),
                lay_width = $that.attr("lay-width") || 800,
                lay_height = $that.attr("lay-height") || 600,
                lay_title = $that.attr("lay_title") || $that.text(),
                lay_id = othis.find(".layui-tab-item").attr("lay-id"),
                allChoose = othis.find('.layui-table-head input[type="checkbox"][lay-filter="allChoose"]'),
                paramChecked = "",
                paramCheckedSpe = "";
            if (typeof (lay_href) === "undefined") {
                layer.msg("链接错误！", {icon: 2});
                return;
            }
            if (typeof (lay_checked) !== "undefined") {
                var checkbox = othis.find('.layui-table-body').find('input[type="checkbox"]:checked');
                if (allChoose.length > 0 && lay_filter === "allChoose") {
                    paramCheckedSpe = ",";
                }
                if (checkbox.length > 1 && typeof (lay_filter) === "undefined") {
                    layer.msg("只能选择一条数据", {icon: 2});
                    return;
                }
                checkbox.each(function (index, item) {
                    if (item.checked) {
                        paramChecked += item.name + paramCheckedSpe;
                    }
                });
                paramChecked = paramChecked.substring(0, paramChecked.length - paramCheckedSpe.length);
                if (paramChecked === "") {
                    layer.msg("请选择数据！", {icon: 2});
                    return;
                }
                paramChecked = lay_checked + "=" + paramChecked;
                if (paramChecked !== "") {
                    var paramSpe = lay_href.indexOf("?") >= 0 ? '&' : '?';
                    lay_href = lay_href + paramSpe + paramChecked;
                }
            }
            if (lay_target === "ajax") {
                if (typeof (lay_tips) !== "undefined") {
                    layer.confirm(lay_tips, {
                        icon: 3,
                        title: '提示'
                    }, function (index) {
                        layer.close(index);
                        buttonAjax(lay_href, {}, othis);
                    });
                } else {
                    buttonAjax(lay_href, {}, othis);
                }
                return;
            }
            if (lay_target === "dialog") {
                buttonDialog(lay_href, {}, lay_title, lay_width, lay_height, lay_id, othis);
                return;
            }
            if (lay_target === "info") {
                buttonInfo(lay_href, {}, lay_title, lay_width, lay_height);
                return;
            }
            if (lay_target === "export") {
                var params = _self.search().params(othis);
                var paramStr = "";
                layui.each(params, function (key, value) {
                    paramStr += "&" + key + "=" + value;
                });
                if (paramStr !== "") {
                    paramSpe = lay_href.indexOf("?") >= 0 ? '&' : '?';
                    lay_href = lay_href + paramSpe + paramStr.substring(1, paramStr.length);
                }
                if (typeof (lay_tips) !== "undefined") {
                    layer.confirm(lay_tips, {
                        icon: 3,
                        title: '提示'
                    }, function (index) {
                        layer.close(index);
                        buttonExport(lay_href)
                    });
                } else {
                    buttonExport(lay_href)
                }
            }
        }

        /**
         * 按钮Ajax类型
         * @param lay_href 链接
         * @param params 参数
         * @param othis 表格父级对象用于刷新网格
         * @param callback 执行成功后回掉函数，参数为服务器放回值
         */
        function buttonAjax(lay_href, params, othis, callback) {
            var layerIndex = layer.load(2);
            ajax.post(lay_href, params, function (result) {
                layer.close(layerIndex);
                if (result.code === 200) {
                    layer.alert(result.msg, {
                        title: "成功",
                        icon: 1
                    }, function (index) {
                        layer.close(index);
                        if (typeof callback === "function") {
                            callback.call(this, result);
                        }
                        _self.search().refresh(othis)
                    });
                } else {
                    layer.alert(result.msg, {
                        title: "错误",
                        icon: 2
                    });
                }
            });
        }

        /**
         * 按钮Dialog类型
         * @param lay_href 链接
         * @param params 参数
         * @param lay_title 标题
         * @param lay_width 宽度
         * @param lay_height 高度
         * @param lay_id 唯一ID
         * @param othis 表格父级对象用于刷新网格
         * @param callback 成功后回掉函数
         */
        function buttonDialog(lay_href, params, lay_title, lay_width, lay_height, lay_id, othis, callback) {
            var layerIndex = layer.load(2);
            ajax.post(lay_href, params, function (result) {
                var layerOptions = {
                    title: lay_title,
                    type: 1,
                    skin: 'layui-layer-rim', // 加上边框
                    area: [lay_width + 'px', lay_height + 'px'], // 宽高
                    content: result,
                    btn: ["保存", "取消"],
                    yes: function (index, layero) {
                        layero.find(".layui-btn[lay-filter='save']").click();
                    },
                    success: function (layero) {
                        _self.config.cache.othis = othis;
                        layero.find(".layui-form").append(
                            '<div class="layui-form-item layui-hide"><button class="layui-btn" lay-id="'
                            + lay_id
                            + '" lay-submit="" lay-filter="save">保存</button></div>');
                    }
                };
                layer.close(layerIndex);
                layerIndex = layer.open(layerOptions);
                _self.config.cache.layerIndex = layerIndex;
                if (typeof callback === "function") {
                    callback.call(this, result);
                }
                layform.render();
            });
        }

        /**
         * 按钮Info类型
         * @param lay_href 链接
         * @param params 参数
         * @param lay_title 标题
         * @param lay_width 宽度
         * @param lay_height 高度
         * @param callback 成功后回掉函数
         */
        function buttonInfo(lay_href, params, lay_title, lay_width, lay_height, callback) {
            var layerIndex = layer.load(2);
            ajax.post(lay_href, params, function (result) {
                var layerOptions = {
                    title: lay_title,
                    type: 1,
                    skin: 'layui-layer-rim', // 加上边框
                    area: [lay_width + 'px', lay_height + 'px'], // 宽高
                    content: result,
                    success: function (layero) {
                        if (layero.find(".layui-table").length > 0) {
                            _self.render(layero.find(".layui-layer-content"));
                        }
                    }
                };
                layer.close(layerIndex);
                layer.open(layerOptions);
                if (typeof callback === "function") {
                    callback.call(this, result);
                }
                layform.render();
            });
        }

        /**
         * 按钮Export类型
         * @param lay_href 链接
         */
        function buttonExport(lay_href) {
            $('<form method="post" action="' + lay_href + '"></form>').appendTo('body').submit().remove();
        }

        return {
            init: init
        }
    };
    /**
     * 分页相关
     */
    grid.prototype.page = function () {
        /**
         * 分页初始化
         * @param othis 表格父级对象
         */
        function init(othis) {
            var pageEle = othis.find(".layui-laypage-box");
            if (pageEle.length <= 0) {
                return;
            }
            var pages = pageEle.attr("lay-pages"),
                curr = pageEle.attr("lay-curr"),
                layerOptions = {
                    cont: pageEle,
                    pages: pages,
                    curr: curr,
                    groups: 5,
                    jump: function (obj, first) {
                        if (first) {
                            return;
                        }
                        var curr = obj.curr;
                        var cont = obj.cont.parents(".layui-tab-item");
                        if(cont.length === 0){
                            cont = obj.cont.parents(".layui-layer-content");
                        }
                        _self.search().refresh(cont, curr);
                    }
                };
            laypage(layerOptions);
        }

        return {
            init: init
        }
    };
    /**
     * 返回layui对象
     */
    exports(MOD_NAME, _self);
})
;