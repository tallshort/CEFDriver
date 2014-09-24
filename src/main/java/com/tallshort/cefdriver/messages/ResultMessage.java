package com.tallshort.cefdriver.messages;

import org.json.simple.JSONObject;

import com.google.common.base.Joiner;

public class ResultMessage implements Comparable<ResultMessage> {

	private final long id;
	private final JSONObject jsonResult;
	
	public ResultMessage(long id, JSONObject jsonResult) {
		this.id = id;
		this.jsonResult = jsonResult;
	}

	public long getId() {
		return id;
	}

	public JSONObject getJsonResult() {
		return jsonResult;
	}
	
	public Object getReturnValue() {
		if (getJsonResult() == null) {
			return null;
		}
		return ((JSONObject) getJsonResult().get("result")).get("value");
	}
	
	public boolean isErrorThrown() {
		if (getJsonResult() == null) {
			return false;
		} 
		Boolean wasThrown = (Boolean) getJsonResult().get("wasThrown");
		return wasThrown != null && wasThrown == true;
	}
	
	public String getErrorString() {
		if (getJsonResult() == null) {
			return "";
		}
		return  (String) ((JSONObject) getJsonResult().get("result")).get("description");
	}

	public int compareTo(ResultMessage other) {
		return Long.valueOf(id).compareTo(Long.valueOf(other.id));
	}
	
	public String toString() {
		return "ResultMessage: " + Joiner.on(", ").join("#" + getId(), getJsonResult().toJSONString());
	}

}
