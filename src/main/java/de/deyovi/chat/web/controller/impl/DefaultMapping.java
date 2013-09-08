package de.deyovi.chat.web.controller.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Method;

public class DefaultMapping implements Mapping, Comparable<Mapping> {

	private final static Logger logger = LogManager.getLogger(DefaultMapping.class);
	
	private final String pathAsString;
	private final Method[] methods;

	public DefaultMapping(String pathAsString, Method... methods) {
		this.methods = methods;
		this.pathAsString = pathAsString == null ? "" : pathAsString.trim();
	}

	public int compareTo(Mapping o) {
		if (o == null) {
			return 1;
		} else {
			return pathAsString.compareTo(o.getPathAsString());
		}
	}

	@Override
	public MatchedMapping match(String requestPath, String requestMethod) {
		MatchedMapping result = null;
		// evaluate the Method
		Method reqMethod = Method.getByName(requestMethod);
		// if there's one
		if (reqMethod != null) {
			// check if the Method matches
			boolean methodMatch = false;
			if (methods.length == 0) {
				methodMatch = true;
			} else {
				for (Method method : methods) {
					if (method == reqMethod) {
						methodMatch = true;
						break;
					}
				}
			}
			if (methodMatch) {
				// Does the path match?
				logger.debug("matching " + pathAsString + " against " + requestPath);
				if (requestPath.startsWith(pathAsString)) {
					result = new DefaultMatchedMapping(requestPath, reqMethod);
				}
			}
		}
		return result;
	}

	@Override
	public String getPathAsString() {
		return pathAsString;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof Mapping) {
			String objPathAsString = ((Mapping) obj).getPathAsString();
			return pathAsString.equals(objPathAsString);
		} else if (obj instanceof String) {
			String objString = obj == null ? "" : ((String) obj).trim();
			return pathAsString.equals(objString);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return pathAsString.hashCode();
	}

	/**
	 * Internal implementation for MatchedMapping
	 * @author michi
	 *
	 */
	private class DefaultMatchedMapping implements MatchedMapping {

		private final String payload;
		private final Method method;

		private DefaultMatchedMapping(String path, Method method) {
			this.payload = path.substring(pathAsString.length());
			this.method = method;
		}

		@Override
		public String getPayload() {
			return payload;
		}
		
		@Override
		public Method getMethod() {
			return method;
		}

		@Override
		public Mapping getMapping() {
			return DefaultMapping.this;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Mapping) {
				return DefaultMapping.this == obj;
			} else {
				return super.equals(obj);
			}
		}

	}
}