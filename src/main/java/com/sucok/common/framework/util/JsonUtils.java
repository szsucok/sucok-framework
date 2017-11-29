package com.sucok.common.framework.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.extern.apachecommons.CommonsLog;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by chendaoxing on 2017/7/1. JSON工具类
 */
@CommonsLog
public class JsonUtils {

	public static String toString(Object object) {
		StringWriter sw = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		try {
			mapper.writeValue(sw, object);
		} catch (IOException e) {
			log.error("Object to Json error", e);
		}
		return sw.toString();
	}

	public static <T> Object toObject(String json, Class<T> clazz) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			log.error("Json to Object error", e);
			return null;
		}
	}

	public static Map<String, Object> toMap(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(json, HashMap.class);
		} catch (Exception e) {
			log.error("Json to Map error", e);
			return null;
		}
	}

	public static TreeMap<String, String> toTreeMap(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(json, TreeMap.class);
		} catch (Exception e) {
			log.error("Json to Map error", e);
			return null;
		}
	}

	public static <T> List<T> toList(String json, Class<T> clazz) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, clazz);
			return mapper.readValue(json, javaType);
		} catch (Exception e) {
			log.error("Json to List error", e);
			return null;
		}
	}
}
