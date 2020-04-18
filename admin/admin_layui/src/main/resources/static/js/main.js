layui.config({
                 base: '/js/console/'
             });
layui.use(
    ['element', 'layer', 'form', 'laydate', 'laypage', 'grid', 'ajax', 'lookup', 'laytime', 'upload', 'tabletree'],
    function () {
        var element = layui.element()
            , layform = layui.form()
            , layer = layui.layer
            , grid = layui.grid
            , $ = layui.jquery
            , ajax = layui.ajax();
        // 禁止菜单A标签跳转并执行绑定事件
        $(".layui-nav-tree a").click(function () {
            layui.event("element", 'nav(left_menu)', this);
            return false;
        });
        // 绑定保存事件
        layform.on("submit(save)", function (data) {
            var $that = $(data.elem)
                , lay_id = $that.attr("lay-id")
                , href = $that.parents(".layui-form").attr("lay-href");
            ajax.post(href, data.field, function (result) {
                if (result.code === 200) {
                    layer.alert(result.msg, {
                        title: "成功",
                        icon: 1
                    }, function (index) {
                        if(grid.config.cache.othis){
                            var searchbox = grid.config.cache.othis.find(".layui-search-box")
                                , searchBtn = searchbox.find("button");
                            searchBtn.click();
                            layer.close(index);
                            layer.close(grid.config.cache.layerIndex);
                        }else{
                            layer.closeAll();
                        }
                    });
                } else {
                    layer.alert(result.msg, {
                        title: "失败",
                        icon: 2
                    });
                }
            });
        });
        //导航点击事件
        element.on('nav(left_menu)', function (elem) {
            var $that = $(elem),
                card = "right_tab",
                lay_id = $that.attr("lay-id"),
                href = $that.attr("href");
            if (typeof(href) === "undefined" || href === "javascript:;" || href === "javascript:void(0);") {
                return;
            }
            var layerIndex = layer.load(2);
            if (typeof(lay_id) === "undefined") {
                lay_id = "bc_" + new Date().getTime();
                $that.attr("lay-id", lay_id);
                //新增一个Tab项
                ajax.get(href, function (result) {
                    // 增加Tab
                    element.tabAdd(card, {
                        title: $that.text()
                        , content: result
                        , id: lay_id
                    });
                    // 切换Tab
                    element.tabChange(card, lay_id);
                    // 绑定删除事件
                    $(".layui-tab-close").on("click", function () {
                        var lay_id = $(this).parents().eq(0).attr("lay-id");
                        $(".layui-nav[lay-filter=left_menu]").find("a[lay-id=" + lay_id + "]")
                            .removeAttr("lay-id");
                    });
                    grid.init(lay_id);
                    layer.close(layerIndex);
                });
            } else {
                element.tabChange(card, lay_id);
                ajax.get(href, function (result) {
                    $('.layui-tab-item[lay-id="' + lay_id + '"]').html(result);
                    grid.init(lay_id);
                    layer.close(layerIndex);
                });
            }
        });
    });