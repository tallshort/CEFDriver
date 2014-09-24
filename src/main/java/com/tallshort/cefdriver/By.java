package com.tallshort.cefdriver;

import com.adobe.sst.ccm.pojo.UIObject;

public abstract class By {

	private By() {	
	}
	
	public static By by(UIObject uiObj) {
		return by(uiObj.getValue(), uiObj.getType());
	}
	
	public static By by(String queryValue, String queryType) {
		if (queryType.equalsIgnoreCase("ID")) {
			return id(queryValue);
		} else if (queryType.equalsIgnoreCase("XPATH")) {
			return xpath(queryValue);
		}
		throw new UnsupportedOperationException("queryType=" + queryType + " is unsupported.");
	}
	
	public static By id(String id) {
		return new ElementById(id);
	}
	
	public static By xpath(String xpath) {
		return new ElementByXpath(xpath);
	}
	
	public abstract String queryString();
	
	protected static class ElementById extends By {

		private final String id;
		
		public ElementById(String id) {
			this.id = id;
		}

		@Override
		public String queryString() {
			return "document.getElementById('" + id +"')";
		}
		
		@Override
		public String toString() {
			return "#" + id;
		}
		
	}
	
	protected static class ElementByXpath extends By {

		private final String xpath;
		
		public ElementByXpath(String xpath) {
			this.xpath = xpath;
		}
		
		@Override
		public String queryString() {
			String getElementByXpathFunctionDef = "function getElementByXpath(path) { "
					+ "return document.evaluate(path, document, null, 9, null).singleNodeValue;"
					+ " };";
			return getElementByXpathFunctionDef + "getElementByXpath('" + xpath +"')";
		}
		
		@Override
		public String toString() {
			return xpath;
		}
		
	}

}
