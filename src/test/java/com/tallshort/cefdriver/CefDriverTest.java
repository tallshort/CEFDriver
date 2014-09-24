package com.tallshort.cefdriver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tallshort.cefdriver.By;
import com.tallshort.cefdriver.CefDriver;
import com.tallshort.cefdriver.CefDriverException;
import com.tallshort.cefdriver.ICefDriver;
import com.tallshort.cefdriver.messages.ElementActionMessage;

public class CefDriverTest {

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testDomElementActionMessage() {
		ElementActionMessage m = new ElementActionMessage(By.id("searchBtn"), "setValue()", "google'");
		Assert.assertTrue(m.toJSONString().contains("document.getElementById('searchBtn').setValue(\\\"google'\\\");"));
		m = new ElementActionMessage(By.id("searchBtn"), "setValue()", "arg1", 2, false);
		Assert.assertTrue(m.toJSONString().contains("document.getElementById('searchBtn').setValue(\\\"arg1\\\", 2, false);"));
		m = new ElementActionMessage(By.id("searchBtn"), "click()");
		Assert.assertTrue(m.toJSONString().contains("document.getElementById('searchBtn').click();"));
		m = new ElementActionMessage(By.id("searchBtn"), "value=", "google");
		Assert.assertTrue(m.toJSONString().contains("document.getElementById('searchBtn').value=\\\"google\\\";"));
		m = new ElementActionMessage(By.id("searchBtn"), "value=", true);
		Assert.assertTrue(m.toJSONString().contains("document.getElementById('searchBtn').value=true;"));
		m = new ElementActionMessage(By.id("searchBtn"), "value");
		Assert.assertTrue(m.toJSONString().contains("document.getElementById('searchBtn').value;"));
		m = new ElementActionMessage(By.xpath("//searchBtn"), "value");
		Assert.assertTrue(m.toJSONString().contains("getElementByXpath('\\/\\/searchBtn').value;"));
		m = new ElementActionMessage(By.id("searchBtn"));
		Assert.assertTrue(m.toJSONString().contains("document.getElementById('searchBtn');"));
	}

	@Test
	public void test() throws Exception {
		ICefDriver driver = new CefDriver();
		try {
			driver.connect();
			driver
				.delay(3)
				.switchToWindowById("52828221c06109632cc0ee894ea6eedd")
				.get("http://www.baidu.com")
				.text(By.id("kw"), "adobe");
			Assert.assertEquals("adobe", (String) driver.attr(By.by("kw", "ID"), "value"));
			driver.click(By.xpath("//*[@id=\"su\"]"));
			Assert.assertEquals("www.baidu.com", driver.evalAndGet("document.location.host"));
		} finally {
			driver.close();
		}
	}
	
	@Test(expected=CefDriverException.class)
	public void testCanNotFindElement_1() {
		ICefDriver driver = new CefDriver();
		try {
			driver.connect();
			driver.element(By.id("xxxxxx"));
		} finally {
			driver.close();
		}
	}
	
	@Test(expected=CefDriverException.class)
	public void testCanNotFindElement_2() {
		ICefDriver driver = new CefDriver();
		try {
			driver.connect();
			driver.element(By.xpath("//xxxxxx"));
		} finally {
			driver.close();
		}
	}

}
