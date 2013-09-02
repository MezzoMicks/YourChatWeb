package de.deyovi.chat.web.json.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import de.deyovi.chat.web.json.JSONData;

public class DefaultJSONData implements JSONData {

	/**
	 * since 0.5.0
	 */
	private static final long serialVersionUID = 2987326616508679744L;
	private Serializable value;
	
	public DefaultJSONData(Object value) {
		if (value instanceof Serializable) {
			this.value = (Serializable) value;
		} else {
			this.value = null;
		}
	}
	
	@Override
	public void appendTo(Appendable appendable) throws IOException {
		String appendee = toString();
		appendable.append(appendee);
	}

	@Override
	public int getSize() {
		return toString().length();
	}
	
	@Override
	public String toString() {
		String appendee = null;
		if (value != null) {
			if (value instanceof String) {
				appendee = "\"" + (String) value + "\"";
			} else if (value instanceof Date) {
				appendee = Long.toString(((Date) value).getTime());
			} else if (value instanceof Number) {
				if (value instanceof Long) {
					appendee = Long.toString((Long) value);
				} else if (value instanceof Integer) {
					appendee = Integer.toString((Integer) value);
				} else if (value instanceof Short) {
					appendee = Short.toString((Short) value);
				} else if (value instanceof Double) {
					appendee = Double.toString((Double) value);
				} else if (value instanceof Float) {
					appendee = Float.toString((Float) value);
				}
			} else if (value instanceof Boolean) {
				appendee = Boolean.toString((Boolean) value);
			}
		}
		if (appendee == null) {
			appendee = "null";
		}
		return appendee;
	}

}
