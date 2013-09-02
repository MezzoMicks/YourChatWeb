package de.deyovi.chat.web.controller;

public class ControllerStatusOutput implements ControllerOutput {

	private final int status;
	
	public ControllerStatusOutput(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getContentType() {
		return "text/plain; charset=UTF-8";
	}

	public int getContentLength() {
		return 0;
	}
	
	public String[] getHeaders() {
		return null;
	}
	
}
