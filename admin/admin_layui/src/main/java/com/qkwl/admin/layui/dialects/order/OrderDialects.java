package com.qkwl.admin.layui.dialects.order;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * 排序方言
 */
public class OrderDialects extends AbstractDialect {

    @Override
    public String getPrefix() {
        return "order";
    }

    @Override
    public Set<IProcessor> getProcessors() {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new FieldAttrProcessor());
        return processors;
    }
}
