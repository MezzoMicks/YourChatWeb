package de.deyovi.chat.web.json;

import java.io.IOException;
import java.io.Serializable;

/**
 * Common type of a JSON
 * @author michi
 *
 */
public interface JSONBase extends Serializable {

	/**
	 * Appends this JSON Elements-Content to the Appendable
	 * @param appendable
	 */
	public void appendTo(Appendable appendable) throws IOException;
	
}
