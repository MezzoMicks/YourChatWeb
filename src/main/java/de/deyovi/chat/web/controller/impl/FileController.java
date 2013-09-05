package de.deyovi.chat.web.controller.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.services.ChatUserService;
import de.deyovi.chat.core.services.impl.DefaultChatUserService;
import de.deyovi.chat.core.utils.ChatConfiguration;
import de.deyovi.chat.core.utils.PasswordUtil;
import de.deyovi.chat.web.SessionParameters;
import de.deyovi.chat.web.controller.ControllerJSONOutput;
import de.deyovi.chat.web.controller.ControllerOutput;
import de.deyovi.chat.web.controller.ControllerStatusOutput;
import de.deyovi.chat.web.controller.ControllerStreamOutput;
import de.deyovi.chat.web.controller.ControllerViewOutput;
import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;
import de.deyovi.chat.web.json.impl.DefaultJSONObject;

/**
 * Controller for general Input (talking and uploading) and some additional actions
 * @author Michi
 *
 */
public class FileController extends AbstractFormController {

	private static final Logger logger = LogManager.getLogger(FileController.class);
	private static final Mapping PATH_CONTENT= new DefaultMapping("content");
	private static final Mapping[] PATHES = new Mapping[] { PATH_CONTENT };

	private final ChatUserService chatUserService = DefaultChatUserService.getInstance();
	
	@Override	
	public Mapping[] getMappings() {
		return PATHES;
	}
	
	@Override
	public ControllerOutput process(MatchedMapping matchedPath, ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		Mapping path = matchedPath.getMapping();
		if (path == PATH_CONTENT) {
			return new ControllerStatusOutput(404);
		} else  {
			ServletContext context = request.getServletContext();
			return getLocalFile(path.getPathAsString(), matchedPath.getPayload(), context);
		}
	}
		
	private ControllerOutput getLocalFile(String path, String filename, ServletContext context) {
		logger.info("looking for resource " + path + "/" + filename);
		File file = new File(context.getRealPath(path), filename);
		logger.info("real path for resource " + context.getRealPath(path) + "/" + filename);
		ControllerOutput output = null;
		if (file.exists()) {
			try {
				String contentType = context.getMimeType(file.getName());
				long size = file.length();
			    String header = "Content-Disposition:inline; filename=\"" + file.getName() + "\"";
				InputStream input = new FileInputStream(file);
				output = new ControllerStreamOutput(contentType, size, input, header);
			} catch (FileNotFoundException e) {
				logger.warn("Requested File " + path + "/" + filename + " not found");
				output = new ControllerStatusOutput(404);
			}
		} else {
			logger.warn("Requested File " + path + "/" + filename + " not found");
			output = new ControllerStatusOutput(404);
		}
		return output;
	}
	
}