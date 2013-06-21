package de.deyovi.chat.web.json.impl;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.web.json.JSONData;
import de.deyovi.chat.web.json.JSONElement;


public abstract class AbstractJSONElement implements JSONElement {

	private static final Logger logger = LogManager.getLogger(AbstractJSONElement.class);
	
	private final String key;
	
	/**
	 * Creates a {@link JSONElement} with the given key
	 * @param key
	 */
	public AbstractJSONElement(String key) {
		this.key = key;
	}
	
	@Override
	public String getKey() {
		return key;
	}
	
	@Override
	public void appendTo(Appendable appender) throws IOException {
		appender.append('"');
		appender.append(getKey());
		appender.append("\":");
		JSONData[] data = getData();
		if (data.length == 1) {
			data[0].appendTo(appender);
		} else {
			appender.append('[');
			for (int i = 0; i < data.length; i++) {
				if (i > 0) {
					appender.append(',');
				}
				data[i].appendTo(appender);
			}
			appender.append(']');
		}
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			appendTo(sb);
		} catch (IOException e) {
			logger.error("Error building String-representation for JSONElement", e);
		}
		return sb.toString();
	}
	
}
