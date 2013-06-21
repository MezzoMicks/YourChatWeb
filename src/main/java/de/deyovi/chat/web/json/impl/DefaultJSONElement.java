package de.deyovi.chat.web.json.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.deyovi.chat.web.json.JSONData;

/**
 * Basic JSONElement
 * @author Michi
 *
 */
public class DefaultJSONElement extends AbstractJSONElement {

	/**
	 * since 0.5.0
	 */
	private static final long serialVersionUID = -2205709739516537277L;
	private final List<JSONData> data = new LinkedList<JSONData>();
	
	/**
	 * Creates an Element with the given key:value-Pair
	 * @param key
	 * @param value
	 */
	public DefaultJSONElement(String key, Object value) {
		super(key);
		push(value);
	}
	
	/**
	 * Adds a value to this 
	 */
	public void push(Object value) {
		Object[] values;
		if (value instanceof Object[]) {
			values = (Object[]) value;
		} else if (value instanceof Collection) {
			Collection<Object> collection = (Collection<Object>) value;
			values = collection.toArray();
		} else {
			values = new Object[] { value };
		}
		for (int i = 0; i < values.length; i++) {
			data.add(new DefaultJSONData(values[i]));
		}
	}
	
	public JSONData[] getData() {
		return data.toArray(new JSONData[data.size()]);
	}
	
}
