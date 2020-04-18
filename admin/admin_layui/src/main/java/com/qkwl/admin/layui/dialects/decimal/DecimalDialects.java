package com.qkwl.admin.layui.dialects.decimal;


import com.qkwl.admin.layui.dialects.decimal.util.DecimalUtils;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionEnhancingDialect;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义执行变量-格式化BigDecimal
 */
public class DecimalDialects extends AbstractDialect implements IExpressionEnhancingDialect {
    public DecimalDialects() {
        super();
    }

    @Override
    public Map<String, Object> getAdditionalExpressionObjects(IProcessingContext iProcessingContext) {
        Map<String, Object> expressions = new HashMap<>();
        expressions.put("decimal", new DecimalUtils());
        return expressions;
    }

    @Override
    public String getPrefix() {
        return "decimal";
    }
}
