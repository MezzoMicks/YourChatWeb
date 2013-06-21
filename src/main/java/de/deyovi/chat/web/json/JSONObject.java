package de.deyovi.chat.web.json;

/**
 * A JSON-Object in general is a complex data-type started by encapsulated with {}
 * @author Michi
 *
 */
public interface JSONObject extends JSONBase {

	public JSONElement[] getElements();

	public JSONObject put(String key);

	public JSONObject push(String key, Object value);

	public JSONObject put(String key, Object value);
	
}
