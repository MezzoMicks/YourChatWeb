package de.deyovi.chat.web.controller.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.utils.ChatConfiguration;
import de.deyovi.chat.core.utils.ChatUtils;
import de.deyovi.chat.web.controller.Controller;

public abstract class AbstractFormController implements Controller {

	private static final Logger logger = LogManager.getLogger(AbstractFormController.class);
	
	private final ServletFileUpload upload;
	
	public AbstractFormController() {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(ChatConfiguration.getUploadThreshold());
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
		upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(ChatConfiguration.getUploadMaximum());
		upload.setSizeMax(ChatConfiguration.getUploadRequest());
	}
	
	protected Map<String, Object> getParameters(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (ServletFileUpload.isMultipartContent(request)) {
			try {
				List<FileItem> items = upload.parseRequest(request);
				for (FileItem item : items) {
					if (item.isFormField()) {
						// Process regular form field input
						result.put(item.getFieldName(), sanitize(item.getString("UTF-8")));
					} else {
						// Process form file field (input type="file").
						String fileName = ChatUtils.replaceSpecialChars(item.getName());
						InputStream fileStream = item.getInputStream();
						result.put(item.getFieldName(), new FileUpload(fileName, fileStream));
					}
				}
			} catch (Exception e) {
				logger.error(new ServletException("Cannot parse multipart request.", e));
			}
		} else {
			Enumeration names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				result.put(name, request.getParameter(name));
			}
		}
		return result;
	}
	
	private String sanitize(String input) {
		String output;
		if (input == null) {
			output = null;
		} else {
			output = StringEscapeUtils.escapeHtml4(input.trim());
		}
		return output;
	}
	
	protected class FileUpload {
		
		private String fileName;
		private InputStream stream;
		
		public FileUpload(String fileName, InputStream stream) {
			this.fileName = fileName;
			this.stream = stream;
		}

		public String getFileName() {
			return fileName;
		}
		
		public InputStream getStream() {
			return stream;
		}
		
	}
	
}
