package com.tallshort.cefdriver;

import com.tallshort.cefdriver.messages.Param;

public class Element {

	private final ICefDriver cefDriver;
	private final By by;
	
	public Element(ICefDriver cefDriver, By by) {
		this.cefDriver = cefDriver;
		this.by = by;
	}
	
	public Element click() {
		cefDriver.click(by);
		return this;
	}
	
	public Element text(String text) {
		cefDriver.text(by, text);
		return this;
	}
	
	public String text() {
		return cefDriver.text(by);
	}
	
	public Element value(Param value) {
		cefDriver.value(by, value);
		return this;
	}
	
	public Object value() {
		return cefDriver.value(by);
	}
	
	public Element attr(String attr, Param value) {
		cefDriver.attr(by, attr, value);
		return this;
	}
	
	public Object attr(String attr) {
		return cefDriver.attr(by, attr);
	}
	
	public Element call(String function, Object... args) {
		cefDriver.call(by, function, args);
		return this;
	}
	
	public Object callAndGet(String function, Object... args) {
		return cefDriver.callAndGet(by, function, args);
	}
	
	public ICefDriver driver() {
		return cefDriver;
	}

}
