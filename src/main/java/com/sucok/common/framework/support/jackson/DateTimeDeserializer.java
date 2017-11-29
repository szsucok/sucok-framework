/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.support.jackson;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Json Date 时间类型序列化
 * @author chendx
 * @version 1.0 created at 2017年4月27日 下午2:45:56
 *
 */
public class DateTimeDeserializer extends JsonDeserializer<Date> {

	private final static DateFormat FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override

	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		try {
			synchronized (FMT) {
				return FMT.parse(p.getValueAsString());
			}
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
