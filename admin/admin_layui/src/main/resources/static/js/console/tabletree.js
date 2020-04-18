/**
 * Created by LY on 2017/4/6.
 */
layui.define('jquery', function (exports) {
    "use strict";

    var $ = layui.jquery;
    var hint = layui.hint();

    var enterSkin = 'layui-tree-enter', Tree = function (options) {
        this.options = options;
    };

    //图标
    var icon = {
        arrow: ['&#xe623;', '&#xe625;'] //箭头
        , checkbox: ['&#xe605;', '&#xe605;'] //复选框
        , radio: ['&#xe62b;', '&#xe62a;'] //单选框
        , branch: ['&#xe622;', '&#xe624;'] //父节点
        , leaf: '&#xe621;' //叶节点
    };

    //初始化
    Tree.prototype.init = function (elem) {
        var that = this;
        elem.addClass('layui-box layui-tree'); //添加tree样式
        if (that.options.skin) {
            elem.addClass('layui-tree-skin-' + that.options.skin);
        }
        that.tree(elem);
        that.on(elem);
    };

    //树节点解析
    Tree.prototype.tree = function (elem, children) {
        var that = this, options = that.options
        var nodes = children || options.nodes;

        layui.each(nodes, function (index, item) {
            var hasChild = item.children && item.children.length > 0;
            var ul = $('<ul class="' + (item.spread ? "layui-show" : "") + '"></ul>');
            var li = $(['<li ' + (item.spread ? 'data-spread="' + item.spread + '"' : '') + '>'
                           //展开箭
                           , function () {
                    return hasChild ? '<i class="layui-icon layui-tree-spread">' + (
                                        item.spread ? icon.arrow[1] : icon.arrow[0]
                                    ) + '</i>' : '';
                }()

                           //复选框/单选框
                           , function () {
                    return options.check ? (
                        '<i class="layui-icon layui-tree-check" lay-filter="allChoose" lay-checked="checked">' + (
                            options.check === 'checkbox' ? icon.checkbox[0] : (
                                options.check === 'radio' ? icon.radio[0] : ''
                            )
                        ) + '</i>'
                    ) : '';
                }()
                           //节点1
                           , function () {
                    return '<a href="' + (item.href || 'javascript:;') + '" ' + (
                            options.target && item.href ? 'target=\"' + options.target + '\"' : ''
                        ) + '>'
                        + ('<cite>' + (item.name || '未命名') + '</cite></a>');
                }()
                           , '<span style="width: 200px;position: absolute;top: 0;right: 0;">'
                           //节点2
                           , function () {
                    return '<a href="' + (item.href || 'javascript:;') + '" ' + (
                            options.target && item.href ? 'target=\"' + options.target + '\"' : ''
                        ) + '>'
                        + ('<cite>' + (item.name || '未命名') + '</cite></a>');
                }()
                           , function () {
                    return options.check ? (
                        '<i class="layui-icon layui-tree-check' + (item.checked ? ' layui-tree-checked' : '') + '"'
                        + (item.checked ? 'data-checked="true"' : '') + ' lay-checked="checked">' + (
                            options.check === 'checkbox' ? (item.checked ? icon.checkbox[1] : icon.checkbox[0]) : (
                                options.check === 'radio' ? (item.checked ? icon.radio[1] : icon.radio[0]) : ''
                            )
                        ) + '</i>'
                        + '<input type="checkbox" class="layui-hide" lay-ignore name="' + (item.rel || '') + '" value="'
                        + (item.id || '') + '"' + (item.checked ? ' checked="checked"' : '') + '/>'
                    ) : '';
                }()
                           , '</span>'
                           , '</li>'].join(''));

            //如果有子节点，则递归继续生成树
            if (hasChild) {
                li.append(ul);
                that.tree(ul, item.children);
            }

            elem.append(li);

            //触发点击节点回调
            typeof options.click === 'function' && that.click(li, item);

            //伸展节点
            that.spread(li, item);

            //拖拽节点
            options.drag && that.drag(li, item);

            // 复选框事件
            that.checkbox(li, item);
        });
    };
    // 复选框事件
    Tree.prototype.checkbox = function (elem, item) {
        var that = this, options = that.options, $checks = elem.find('i[lay-checked="checked"]');
        $checks.unbind('click').on('click', function () {
            var $this = $(this);
            if ($this.data("checked")) {
                $this.html(icon.checkbox[0]).removeClass("layui-tree-checked").data('checked', null);
                if ($this.parent('li').length == 0) {
                    $this.parent("span").children('input[type="checkbox"]').removeProp('checked');
                    $this.parent("span").children('i[class="layui-icon layui-tree-check layui-tree-checked"]')
                        .html(icon.checkbox[0])
                        .removeClass("layui-tree-checked").data('checked', null);
                } else {
                    $this.parent('li').find("span").children('input[type="checkbox"]').removeProp('checked');
                    $this.parent('li').find("span")
                        .children('i[class="layui-icon layui-tree-check layui-tree-checked"]').html(icon.checkbox[0])
                        .removeClass("layui-tree-checked").data('checked', null);
                }
                if ($this.attr("lay-filter") === "allChoose") {
                    var $child = $this.parent("li")
                        .find('ul i[class="layui-icon layui-tree-check layui-tree-checked"]');
                    $child.each(function (index, elem) {
                        var $elem = $(elem);
                        $elem.html(icon.checkbox[0]).removeClass("layui-tree-checked").data('checked', null);
                        $elem.siblings("span").children('input[type="checkbox"]').removeProp('checked');
                        $elem.siblings("span").children('layui-icon layui-tree-check layui-tree-checked')
                            .html(icon.checkbox[0])
                            .removeClass("layui-tree-checked").data('checked', null);
                    });
                }
            } else {
                $this.html(icon.checkbox[1]).addClass("layui-tree-checked").data('checked', true);
                if ($this.parent('li').length == 0) {
                    $this.parent("span").children('input[type="checkbox"]').prop('checked', 'true');
                    $this.parent("span").children('i[class="layui-icon layui-tree-check"]')
                        .html(icon.checkbox[1]).addClass("layui-tree-checked").data('checked', true);
                } else {
                    $this.parent('li').find("span").children('input[type="checkbox"]').prop('checked', 'true');
                    $this.parent('li').find("span").children('i[class="layui-icon layui-tree-check"]')
                        .html(icon.checkbox[1]).addClass("layui-tree-checked").data('checked', true);
                }
                if ($this.attr("lay-filter") === "allChoose") {
                    var $child = $this.parent("li").find('ul i[class="layui-icon layui-tree-check"]');
                    $child.each(function (index, elem) {
                        var $elem = $(elem);
                        $elem.html(icon.checkbox[1]).addClass("layui-tree-checked").data('checked', true);
                        $elem.siblings("span").children('input[type="checkbox"]').prop('checked', 'true');
                        $elem.siblings("span").children('layui-icon layui-tree-check').html(icon.checkbox[1])
                            .addClass("layui-tree-checked").data('checked', true);
                    });
                }
            }
        })
    }
    //点击节点回调
    Tree.prototype.click = function (elem, item) {
        var that = this, options = that.options;
        elem.children('a').on('click', function (e) {
            layui.stope(e);
            options.click(item)
        });
    };

    //伸展节点
    Tree.prototype.spread = function (elem, item) {
        var that = this, options = that.options;
        var arrow = elem.children('.layui-tree-spread')
        var ul = elem.children('ul'), a = elem.children('a');

        //执行伸展
        var open = function () {
            if (elem.data('spread')) {
                elem.data('spread', null)
                ul.removeClass('layui-show');
                arrow.html(icon.arrow[0]);
                a.find('.layui-icon').html(icon.branch[0]);
            } else {
                elem.data('spread', true);
                ul.addClass('layui-show');
                arrow.html(icon.arrow[1]);
                a.find('.layui-icon').html(icon.branch[1]);
            }
        };

        //如果没有子节点，则不执行
        if (!ul[0]) {
            return;
        }

        arrow.on('click', open);
        a.on('dblclick', open);
    }

    //通用事件
    Tree.prototype.on = function (elem) {
        var that = this, options = that.options;
        var dragStr = 'layui-tree-drag';

        //屏蔽选中文字
        elem.find('i').on('selectstart', function (e) {
            return false
        });

        //拖拽
        if (options.drag) {
            $(document).on('mousemove', function (e) {
                var move = that.move;
                if (move.from) {
                    var to = move.to, treeMove = $('<div class="layui-box ' + dragStr + '"></div>');
                    e.preventDefault();
                    $('.' + dragStr)[0] || $('body').append(treeMove);
                    var dragElem = $('.' + dragStr)[0] ? $('.' + dragStr) : treeMove;
                    (dragElem).addClass('layui-show').html(move.from.elem.children('a').html());
                    dragElem.css({
                                     left: e.pageX + 10
                                     , top: e.pageY + 10
                                 })
                }
            }).on('mouseup', function () {
                var move = that.move;
                if (move.from) {
                    move.from.elem.children('a').removeClass(enterSkin);
                    move.to && move.to.elem.children('a').removeClass(enterSkin);
                    that.move = {};
                    $('.' + dragStr).remove();
                }
            });
        }
    };

    //拖拽节点
    Tree.prototype.move = {};
    Tree.prototype.drag = function (elem, item) {
        var that = this, options = that.options;
        var a = elem.children('a'), mouseenter = function () {
            var othis = $(this), move = that.move;
            if (move.from) {
                move.to = {
                    item: item
                    , elem: elem
                };
                othis.addClass(enterSkin);
            }
        };
        a.on('mousedown', function () {
            var move = that.move
            move.from = {
                item: item
                , elem: elem
            };
        });
        a.on('mouseenter', mouseenter).on('mousemove', mouseenter)
            .on('mouseleave', function () {
                var othis = $(this), move = that.move;
                if (move.from) {
                    delete move.to;
                    othis.removeClass(enterSkin);
                }
            });
    };

    //暴露接口
    exports('tabletree', function (options) {
        var tree = new Tree(options = options || {});
        var elem = $(options.elem);
        if (!elem[0]) {
            return hint.error('layui.tree 没有找到' + options.elem + '元素');
        }
        tree.init(elem);
    });
});
