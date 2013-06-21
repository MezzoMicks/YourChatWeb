package de.deyovi.chat.web.json;

/**
 * A {@link JSONElement} represents a key:value-Pair
 * @author Michi
 *
 */
public interface JSONElement extends JSONBase {

	/**
	 * Returns the key of this object or null if this is the 'root'
	 * @return key or null
	 */
	public String getKey();
	
	public void push(Object value);
	
	/**
	 * The value of this key:value-Pair
	 * @return {@link JSONData}
	 */
	public JSONData[] getData();
	
}
