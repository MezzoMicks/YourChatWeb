package de.deyovi.chat.web.controller;

import java.io.InputStream;

public class ControllerStreamOutput implements ControllerOutput {

	private final String contentType;
	private final int size;
	private final InputStream input;
	private final String[] headers;
	
	public ControllerStreamOutput(String contentType, long size, InputStream input, String... headers) {
		this.contentType = contentType;
		this.size = (int) size;
		this.input = input;
		this.headers = headers;
	}

	@Override
	public int getContentLength() {
		return size;
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}
	
	@Override
	public String[] getHeaders() {
		return headers;
	}
	
	@Override
	public int getStatus() {
		return 200;
	}
	
	public InputStream getStream() {
		return input;
	}
	
}
