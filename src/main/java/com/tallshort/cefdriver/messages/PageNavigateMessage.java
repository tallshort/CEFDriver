package com.tallshort.cefdriver.messages;

import static org.chromium.sdk.tests.internal.JsonBuilderUtil.jsonObject;
import static org.chromium.sdk.tests.internal.JsonBuilderUtil.jsonProperty;

import org.json.simple.JSONObject;

import com.tallshort.cefdriver.BasicConstants;

public class PageNavigateMessage extends RequestMessage {

	private final String url;

	public PageNavigateMessage(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}

	protected JSONObject getRawJsonRequest() {
		return jsonObject(
				jsonProperty(BasicConstants.Property.METHOD, "Page.navigate"),
				jsonProperty(BasicConstants.Property.PARAMS, jsonObject(jsonProperty("url", url))));
	}

}
