package com.tallshort.cefdriver.messages;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONValue;

public class Param {

	private final Object value;
	
	private Param(Object value) {
		this.value = value;
	}
	
	public String toString() {
		return value.toString();
	}
	
	public static Param rawParam(Object value) {
		return new Param(value);
	}
	
	public static Param strParam(String value) {
		return new Param("\"" + JSONValue.escape(value) + "\"");
	}
	
	public static List<Param> toParams(Object... values) {
		List<Param> params = new ArrayList<Param>();
		for (Object value : values) {
			if (value instanceof String) {
				params.add(strParam((String) value));
			} else {
				params.add(rawParam(value));
			}
		}
		return params;
	}

}
