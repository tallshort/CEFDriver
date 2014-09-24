package com.tallshort.cefdriver;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.tallshort.cefdriver.messages.Param;


public class ParamHelperTest {

	@Before
	public void setUp() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		JSONObject obj = new JSONObject();
		obj.put("expression","a=" + Param.strParam("I'm a \"boy\""));
		System.out.println(obj.toJSONString());
	}

}
