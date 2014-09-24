package com.tallshort.cefdriver;


public interface ICefDriver {
	
	void connect();

	ICefDriver switchToWindowById(String windowId);
	
	ICefDriver switchTo(String windowTitleKeyword);
	
	ICefDriver get(String url);
	
	ICefDriver delay(int seconds);
	
	ICefDriver click(By by);
	
	/**
	 * A convenient method to set value of String type for the specified element.
	 * 
	 * @param by the specified element
	 * @return CEF driver
	 */
	ICefDriver text(By by, String text);
	
	/**
	 * A convenient method to get value of String type for the specified element.
	 * 
	 * @param by the specified element
	 * @return the value of String type
	 */
	String text(By by);
	
	ICefDriver value(By by, Object value);
	
	Object value(By by);
	
	Element element(By by);
	
	ICefDriver call(By by, String function, Object... args);
	
	Object callAndGet(By by, String function, Object... args);
	
	ICefDriver attr(By by, String attr, Object value);
	
	Object attr(By by, String attr);
	
	ICefDriver eval(String expression);
	
	Object evalAndGet(String expression);
	
	void close();
}
