package com.symbio.epb.bigfile.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.util.Map;


/**
 * 
 * @author Yao Pan
 *
 */
public class JSONUtil {
private final static ObjectMapper mapper = new ObjectMapper();
	
	public static Object formatStr2Object(String json, Class<?> clazz) {
		if (StringUtils.isEmpty(json)) {
			return null;
		}
		
        try {
        	return mapper.readValue(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}  
	}
	
	public static JsonNode formatStr2JSONNode(String json) {
		if (StringUtils.isEmpty(json)) {
			return null;
		}
		
        try {
        	return mapper.readTree(json);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}  
	}
	
	public static String formatOject2Str(Object object) {
		if (object == null) {
			return null;
		}
		
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	public static Map<? extends String, ? extends Object> convertJsonNodeToMap(JsonNode source) {
		if(source==null){
			return null;
		}else{
			Map<String, Object> result = mapper.convertValue(source, Map.class);
			return result;
		}
	}
}
