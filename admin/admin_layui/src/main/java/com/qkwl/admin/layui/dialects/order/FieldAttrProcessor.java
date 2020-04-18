package com.qkwl.admin.layui.dialects.order;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * 排序attr解析
 */
public class FieldAttrProcessor extends AbstractAttributeModifierAttrProcessor {

    public FieldAttrProcessor() {
        super("Field");
    }

    @Override
    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
        String attributeValue = element.getAttributeValue(attributeName);
        Map<String, Object> contextVariables = arguments.getContext().getVariables();
        // 获取参数并解析
        Object param = contextVariables.get("param");
        String orderDirection = "";
        if (param != null) {
            Map<String, String[]> paramMap = (Map<String, String[]>) param;
            if (paramMap.get("orderDirection") != null && paramMap.get("orderField") != null && paramMap.get("orderField")[0].toString().equals(attributeValue)) {
                orderDirection = paramMap.get("orderDirection")[0].toString();
            }
        }
        Map<String, String> values = new HashMap<>();
        values.put("orderField", attributeValue);
        values.put("orderDirection", orderDirection);
        return values;
    }

    @Override
    protected ModificationType getModificationType(Arguments arguments, Element element, String s, String s1) {
        return ModificationType.APPEND_WITH_SPACE;
    }

    @Override
    protected boolean removeAttributeIfEmpty(Arguments arguments, Element element, String s, String s1) {
        return true;
    }

    @Override
    protected boolean recomputeProcessorsAfterExecution(Arguments arguments, Element element, String s) {
        return false;
    }

    @Override
    public int getPrecedence() {
        return 300;
    }
}
