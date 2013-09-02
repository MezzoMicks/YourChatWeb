package de.deyovi.chat.web.controller;

import java.util.Map;

public class ControllerViewOutput implements ControllerOutput {

	private final String targetJSP;
	private final Map<String, Object> parameters;
	
	public ControllerViewOutput(String targetJSP, Map<String, Object> parameters) {
		this.targetJSP = targetJSP;
		this.parameters = parameters;
	}

	@Override
	public int getStatus() {
		return 200;
	}
	
	@Override
	public String getContentType() {
		return "text/html; charset=UTF-8";
	}

	@Override
	public int getContentLength() {
		return -1;
	}

	@Override
	public String[] getHeaders() {
		return null;
	}
	
	public String getTargetJSP() {
		return targetJSP;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}
	
}
