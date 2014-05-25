package de.deyovi.chat.web.controller;

public interface Mapping {

	public String getPathAsString();
	
	public String[] getAliases();
	
	public MatchedMapping match(String path, String method);
	
	public static interface MatchedMapping {
		
		public Mapping getMapping();
		
		public Method getMethod();
		
		public String getPayload();
		
	}
	
}
