package de.deyovi.chat.web.json.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import de.deyovi.chat.web.json.JSONElement;
import de.deyovi.chat.web.json.JSONObject;

/**
 * Basic {@link JSONObject}
 * @author Michi
 *
 */
public class DefaultJSONObject extends AbstractJSONObject {

	/**
	 * since 0.5.0
	 */
	private static final long serialVersionUID = 6526457616824242218L;
	
	// We're using a TreeMap to keep the natural order of elements
	private final TreeMap<String, JSONElement> elements = new TreeMap<String, JSONElement>();
	
	/**
	 * For creating an empty {@link JSONObject}
	 */
	public DefaultJSONObject() {
		// plain constructor
	}
	
	/**
	 * Creates a {@link JSONObject} with this initial key:value-pair
	 * @param key
	 * @param value
	 */
	public DefaultJSONObject(String key, Object value) {
		put(key, value);
	}
	
	/**
	 * Creates a {@link JSONObject} from the given Map-Data
	 * @param dataMap
	 */
	public DefaultJSONObject(Map<String, Object> dataMap) {
		Set<Entry<String,Object>> entries = dataMap.entrySet();
		for (Entry<String, Object> entry : entries) {
			elements.put(entry.getKey(), new DefaultJSONElement(entry.getKey(), entry.getValue()));
		}
	}
	
	@Override
	public JSONObject put(String key, Object value) {
		elements.put(key, new DefaultJSONElement(key, value));
		return this;
	}
	
	@Override
	public JSONObject put(String key) {
		JSONObject newObject = new DefaultJSONObject();
		elements.put(key, new DefaultJSONElement(key, newObject));
		return newObject;
	}
	
	@Override
	public JSONObject push(String key, Object value) {
		JSONElement element = elements.get(key);
		if (element == null) {
			elements.put(key, new DefaultJSONElement(key, value));
		} else {
			element.push(value);
		}
		return this;
	}
	
	@Override
	public JSONElement[] getElements() {
		Collection<JSONElement> elementList = elements.values();
		return elementList.toArray(new JSONElement[elementList.size()]);
	}
}
