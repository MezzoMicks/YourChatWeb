package de.deyovi.chat.web.controller;

public interface ControllerOutput {

	public int getStatus();
	
	public String getContentType();

	public int getContentLength();
	
	public String[] getHeaders();
	
}
