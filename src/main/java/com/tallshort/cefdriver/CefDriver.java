package com.tallshort.cefdriver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.tallshort.cefdriver.messages.ElementActionMessage;
import com.tallshort.cefdriver.messages.PageNavigateMessage;
import com.tallshort.cefdriver.messages.RuntimeEvaluateMessage;

public class CefDriver implements ICefDriver {
	
	private CefWindowDriver currentCefWindowDriver;
	private List<CefWindowDriver> cefWindowDrivers = new ArrayList<CefWindowDriver>();
	
	private final static Logger logger = Logger.getLogger(CefDriver.class);
	
	public CefDriver() {
		this("localhost", 28282);
	}
	
	public CefDriver(String host, int remoteDebuggingPort) {
		URIBuilder builder = new URIBuilder();
		URI uri;
		try {
			uri = builder.setScheme("http")
				.setHost(host)
				.setPort(remoteDebuggingPort)
				.setPath("/json")
				.build();
			HttpGet httpGet = new HttpGet(uri);
			DefaultHttpClient httpclient = new DefaultHttpClient();
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpclient.execute(httpGet, responseHandler);
			JSONArray remoteDebuggingWindows = (JSONArray) JSONValue.parseWithException(responseBody);
			for (int i = 0; i < remoteDebuggingWindows.size(); i++) {
				cefWindowDrivers.add(buildCefWindowDriver((JSONObject) remoteDebuggingWindows.get(i)));
			}
		} catch (Exception e) {
			throw new CefDriverException(e);
		}
	}

	protected CefWindowDriver buildCefWindowDriver(JSONObject remoteDebuggingWindow) throws URISyntaxException {
		String title = (String) remoteDebuggingWindow.get("title");
		String webSocketDebuggerUrl = (String) remoteDebuggingWindow.get("webSocketDebuggerUrl");
		// ws://localhost:28282/devtools/page/f3ed2869e3fcafefaeb4c2d7f1b913de
		String windowId = webSocketDebuggerUrl.substring(webSocketDebuggerUrl.lastIndexOf('/') + 1);
		return new CefWindowDriver(windowId, title, new URI(webSocketDebuggerUrl));
	}
	
	public void connect() {
		for (CefWindowDriver cefWindowDriver : cefWindowDrivers) {
			try {
				if (!cefWindowDriver.connectBlocking()) {
					throw new CefDriverException(" Failed to connect to " + cefWindowDriver.getURI());
				}
			} catch (InterruptedException e) {
				throw new CefDriverException(e);
			}
		}
		logger.info("CefDriver is connected");
		// switch to the first window if exists
		if (!cefWindowDrivers.isEmpty()) {
			switchTo(cefWindowDrivers.get(0));
		}
	}

	public ICefDriver switchToWindowById(final String windowId) {
		try {
			CefWindowDriver cefWindowDriver = Iterables.find(cefWindowDrivers, new Predicate<CefWindowDriver>() {
				public boolean apply(CefWindowDriver cefWindowDriver) {
					return cefWindowDriver.getId().equals(windowId);
				}
			});
			switchTo(cefWindowDriver);
		} catch (NoSuchElementException e) {
			throw new CefDriverException(e);
		}
		return this;
	}
	
	public ICefDriver switchTo(final String windowTitleKeyword) {
		try {
			CefWindowDriver cefWindowDriver = Iterables.find(cefWindowDrivers, new Predicate<CefWindowDriver>() {
				public boolean apply(CefWindowDriver cefWindowDriver) {
					return cefWindowDriver.getTitle().toLowerCase().contains(windowTitleKeyword.toLowerCase());
				}
			});
			switchTo(cefWindowDriver);
		} catch (NoSuchElementException e) {
			throw new CefDriverException(e);
		}
		return this;
	}
	
	protected void switchTo(CefWindowDriver cefWindowDriver) {
		currentCefWindowDriver = cefWindowDriver;
		logger.info("switch to window " + currentCefWindowDriver);
	}

	public ICefDriver get(String url) {
		currentCefWindowDriver.executeRequest(new PageNavigateMessage(url));
		return this;
	}

	public ICefDriver delay(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			// ignored
		}
		return this;
	}
	
	public Element element(By by) {
		JSONObject jsonResult =  currentCefWindowDriver.executeRequest(new ElementActionMessage(by)).getJsonResult();
		String subType = (String) ((JSONObject) jsonResult.get("result")).get("subtype");
		if (subType.equalsIgnoreCase("null")) {
			throw new CefDriverException("Cannot find element " + by);
		}
		return new Element(this, by);
	}
	
	public ICefDriver click(By by) {
		return call(by, "click");
	}

	public ICefDriver text(By by, String text) {
		return value(by, text);
	}
	
	public String text(By by) {
		return (String) value(by);
	}
	
	public ICefDriver value(By by, Object value) {
		return attr(by, "value", value);
	}

	public Object value(By by) {
		return attr(by, "value");
	}

	public ICefDriver call(By by, String function, Object... args) {
		callAndGet(by, function, args);
		return this;
	}

	public Object callAndGet(By by, String function, Object... args) {
		return currentCefWindowDriver.executeRequest(new ElementActionMessage(by, function + "()", args)).getReturnValue();
	}

	public ICefDriver attr(By by, String attr, Object value) {
		currentCefWindowDriver.executeRequest(new ElementActionMessage(by, attr + "=", value));
		return this;
	}

	public Object attr(By by, String attr) {
		return currentCefWindowDriver.executeRequest(new ElementActionMessage(by, attr)).getReturnValue();
	}
	
	public ICefDriver eval(String expression) {
		evalAndGet(expression);
		return this;
	}
	
	public Object evalAndGet(String expression) {
		return currentCefWindowDriver.executeRequest(new RuntimeEvaluateMessage(expression)).getReturnValue();
	}

	public void close() {
		for (CefWindowDriver cefWindowDriver : cefWindowDrivers) {
			try {
				cefWindowDriver.closeBlocking();
			} catch (InterruptedException e) {
				// ignored
			}
		}
	}

}
