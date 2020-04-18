package com.qkwl.common.util;

import java.io.IOException;

import com.qkwl.common.Enum.IBaseEnumMessage;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider; 
 
/**
 * 解决枚举类型返回json格式为自定义格式 【不能删除】
 * @author ZKF
 */
public class CustomEnumSerializer extends JsonSerializer<IBaseEnumMessage> {

	@Override
	public void serialize(IBaseEnumMessage value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		//String text=value.getClass().getSimpleName()+"."+value.toString();
		//gen.writeString(value.toString());与EnumUtils中的60行相对应key.setKey(enumMessage.toString());
		gen.writeString(String.valueOf(value + "#" + value.getValue()));//与EnumUtils中的61行相对应key.setKey(enumMessage.getCode());
//		gen.writeObject(value);
	}

	@Override
	public Class<IBaseEnumMessage> handledType() { 
		return IBaseEnumMessage.class;
	} 
}
