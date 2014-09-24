package com.tallshort.cefdriver.messages;

import java.util.Arrays;

import org.json.simple.JSONObject;

import com.google.common.base.Joiner;
import com.tallshort.cefdriver.By;

public class ElementActionMessage extends RequestMessage {

	private RuntimeEvaluateMessage runtimeEvaluateMessage;
	
	public ElementActionMessage(By by) {
		this(by, "");
	}
	
	public ElementActionMessage(By by, String action, Object... args) {
		String expression = by.queryString();
		boolean returnByValue = true;
		if (action == null || action.isEmpty()) {
			if (args.length != 0) {
				throw new IllegalArgumentException("args should be empty");
			}
			returnByValue = false;
		} else {
			expression += ".";
		}
		if (action.endsWith("()")) { // .function(arg1, arg2, arg3);
			expression += action.substring(0, action.length() - 1) + Joiner.on(", ").join(Param.toParams(args)) + ")";
		} else if (action.endsWith("=")) { // .attr=xxxx;
			if (args.length != 1) {
				throw new IllegalArgumentException(Arrays.toString(args));
			}
			expression += action + Param.toParams(args).get(0);
		} else { // .attr;
			if (args.length != 0) {
				throw new IllegalArgumentException("args should be empty");
			}
			expression += action;
		}
		expression += ";";
		runtimeEvaluateMessage = new RuntimeEvaluateMessage(expression, returnByValue);
	}

	@Override
	protected JSONObject getRawJsonRequest() {
		return runtimeEvaluateMessage.getRawJsonRequest();
	}

}
