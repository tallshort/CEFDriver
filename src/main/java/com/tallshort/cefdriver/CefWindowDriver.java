package com.tallshort.cefdriver;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.tallshort.cefdriver.messages.RequestMessage;
import com.tallshort.cefdriver.messages.ResultMessage;


public class CefWindowDriver extends WebSocketClient {

	private static final int STEP_DELAY = 2000; // 2s

	private static final int RESULT_TIMEOUT = 5;
	
	private final static Logger logger = Logger.getLogger(CefWindowDriver.class);
	
	protected BlockingQueue<ResultMessage> resultMessageQueue = new LinkedBlockingQueue<ResultMessage>();
	
	private final String title;
	private final String id;
	
	public CefWindowDriver(String windowId, String title, URI serverUri, Draft draft) {
		super(serverUri, draft);
		this.id = windowId;
		this.title = title;
	}

	public CefWindowDriver(String windowId, String title, URI serverURI) {
		this(windowId, title, serverURI, new Draft_17());
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getId() {
		return id;
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		logger.info("opened connection");
	}

	@Override
	public void onMessage(String message) {
		logger.info(Thread.currentThread() + ": received: " + message);
		try {
			JSONObject messageObj = (JSONObject)JSONValue.parseWithException(message);
			long id = (Long) messageObj.get("id");
			JSONObject jsonResult = (JSONObject) messageObj.get("result");
			resultMessageQueue.put(new ResultMessage(id, jsonResult));
		} catch (Exception e) {
			throw new CefDriverException(e);
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		logger.info("Connection closed by " + (remote ? "remote peer" : "us"));
	}

	@Override
	public void onError(Exception ex) {
		logger.error(ex.getMessage());
		ex.printStackTrace();
	}
	
	public ResultMessage executeRequest(RequestMessage requestMessage) {
		long requestId = sendRequest(requestMessage);
		ResultMessage resultMessage = waitForResultMessage(requestId);
		if (resultMessage == null) {
			throw new CefDriverException("No result message for request #" + requestId + " in allowed time");
		}
		logger.info(resultMessage);
		if (resultMessage.isErrorThrown()) {
			throw new CefDriverException(resultMessage.getErrorString());
		}
		return resultMessage;
	}
	
	protected long sendRequest(RequestMessage requestMessage) {
		logger.info("RequestMessage: " + requestMessage.toJSONString());
		send(requestMessage.toJSONString());
		return requestMessage.getId();
	}

	protected ResultMessage waitForResultMessage(long requestId) {
		try {
			Thread.sleep(STEP_DELAY);
		} catch (InterruptedException e1) {
			// ignored
		}
		ResultMessage resultMessage = null;
		try {
			while ((resultMessage = resultMessageQueue.poll(RESULT_TIMEOUT, TimeUnit.SECONDS)) != null) {
				if (resultMessage.getId() == requestId) {
					break;
				}
			}
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		return resultMessage;
	}
	
	public String toString() {
		return "[" + getId() + ", " + getTitle() + "]";
	}

}