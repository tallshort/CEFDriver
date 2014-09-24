package com.tallshort.cefdriver.messages;

import static org.chromium.sdk.tests.internal.JsonBuilderUtil.jsonObject;
import static org.chromium.sdk.tests.internal.JsonBuilderUtil.jsonProperty;

import org.json.simple.JSONObject;

import com.tallshort.cefdriver.BasicConstants;


public class RuntimeEvaluateMessage extends RequestMessage {
	
	private final String expression;
	private final boolean returnByValue;

	public RuntimeEvaluateMessage(String expression) {
		this(expression, true);
	}
	
	public RuntimeEvaluateMessage(String expression, boolean returnByValue) {
		this.expression = expression;
		this.returnByValue = returnByValue;
	}
	
	public String getExpression() {
		return expression;
	}
	
	@Override
	protected JSONObject getRawJsonRequest() {
		return jsonObject(
				jsonProperty(BasicConstants.Property.METHOD, "Runtime.evaluate"),
				jsonProperty(BasicConstants.Property.PARAMS, jsonObject(
						jsonProperty("expression", expression),
						jsonProperty("returnByValue", returnByValue))));
	}

}
