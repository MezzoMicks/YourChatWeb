package de.deyovi.chat.web.controller;

import org.json.JSONObject;


public class ControllerJSONOutput implements ControllerOutput {

	private final JSONObject json;
	
	public ControllerJSONOutput(JSONObject json) {
		this.json = json;
	}
	
	@Override
	public int getStatus() {
		return 200;
	}

	@Override
	public String getContentType() {
		return "application/json";
	}
	
	@Override
	public int getContentLength() {
		return json.length();
	}
	
	@Override
	public String[] getHeaders() {
		return null;
	}
	
	public JSONObject getJSON() {
		return json;
	}

	
	
}
