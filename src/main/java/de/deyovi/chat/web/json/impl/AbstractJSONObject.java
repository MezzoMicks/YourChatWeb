package de.deyovi.chat.web.json.impl;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.web.json.JSONElement;
import de.deyovi.chat.web.json.JSONObject;

/**
 * Abstract foundation for {@link JSONObject} defining the core functionality to present the stored data as string
 * @author Michi
 *
 */
public abstract class AbstractJSONObject implements JSONObject {

	
	private static final Logger logger = LogManager.getLogger(AbstractJSONObject.class);
	
	@Override
	public void appendTo(Appendable appender) throws IOException {
		// open the object
		appender.append("{");
		// get all the objects elements
		JSONElement[] elements = getElements();
		for (int i = 0; i < elements.length; i++) {
			if (i > 0) {
				appender.append(',');
			}
			// and append them
			elements[i].appendTo(appender);
		}
		// close the object
		appender.append("}");
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			appendTo(sb);
		} catch (IOException e) {
			logger.error("Error building String-representation for JSONObject", e);
		}
		return sb.toString();
	}
	
}
