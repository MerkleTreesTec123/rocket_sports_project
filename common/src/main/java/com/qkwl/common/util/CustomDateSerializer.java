package com.qkwl.common.util;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 自定义时间格式，序列化【不能删除】
 * @author ZKF
 */
public class CustomDateSerializer extends JsonSerializer<Date>{

	@Override
	public void serialize(Date date, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeString(DateUtils.format(date, DateUtils.YYYY_MM_DD_HH_MM_SS)); 
	}

	@Override
	public Class<Date> handledType() {
		return Date.class;
	}

}
