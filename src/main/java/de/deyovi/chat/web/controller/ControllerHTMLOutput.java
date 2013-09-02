package de.deyovi.chat.web.controller;

public class ControllerHTMLOutput implements ControllerOutput {

	private final String html;
	
	public ControllerHTMLOutput(String html) {
		this.html = html;
	}
	
	@Override
	public String getContentType() {
		return "text/html; charset=UTF-8";
	}
	
	@Override
	public int getStatus() {
		return 200;
	}
	
	@Override
	public int getContentLength() {
		return html.length();
	}
	
	@Override
	public String[] getHeaders() {
		return null;
	}
	
	public String getHtml() {
		return html;
	}
	
}
