package de.deyovi.chat.web.controller;

public class ControllerRedirectOutput implements ControllerOutput {

	private final String target;
	
	public ControllerRedirectOutput(String target) {
		this.target = target;
	}
	
	@Override
	public String getContentType() {
		return "text/html; charset=UTF-8";
	}
	
	@Override
	public int getStatus() {
		return 302;
	}
	
	@Override
	public int getContentLength() {
		return 0;
	}

	@Override
	public String[] getHeaders() {
		return null;
	}
	
	public String getTarget() {
		return target;
	}
	
	
}
