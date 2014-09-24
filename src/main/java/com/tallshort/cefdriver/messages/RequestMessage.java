package com.tallshort.cefdriver.messages;

import java.util.concurrent.atomic.AtomicLong;

import org.json.simple.JSONObject;

import com.tallshort.cefdriver.BasicConstants;

public abstract class RequestMessage implements IRequestMessage {
	
	private static final AtomicLong REQUESET_SEQ = new AtomicLong();

	private final long id;

	public RequestMessage() {
		id = REQUESET_SEQ.incrementAndGet();
	}
	
	public long getId() {
		return id;
	}
	
	protected abstract JSONObject getRawJsonRequest();

	@SuppressWarnings("unchecked")
	public String toJSONString() {
		JSONObject rawJsonRequest = getRawJsonRequest();
		rawJsonRequest.put(BasicConstants.Property.ID, id);
		return rawJsonRequest.toJSONString();
	}
}
