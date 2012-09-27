package li.test.mock;

import java.util.Enumeration;

import javax.servlet.FilterConfig;

public class MockFilterConfig implements FilterConfig {
	private MockServletContext servletContext;

	public MockFilterConfig(MockServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public String getFilterName() {
		return null;
	}

	public String getInitParameter(String arg0) {
		return null;
	}

	public Enumeration<String> getInitParameterNames() {
		return null;
	}

	public MockServletContext getServletContext() {
		return this.servletContext;
	}
}