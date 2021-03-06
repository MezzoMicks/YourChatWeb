package de.deyovi.chat.web.controller.impl;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.facades.InputFacade;
import de.deyovi.chat.web.controller.ControllerOutput;
import de.deyovi.chat.web.controller.ControllerStatusOutput;
import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;
import de.deyovi.chat.web.controller.annotations.Controller;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Map;

/**
 * Controller for general Input (talking and uploading) and some additional actions
 * @author Michi
 *
 */
@Singleton
@Controller
public class InputController extends AbstractFormController {

	private static final Logger logger = LogManager.getLogger(InputController.class);
	private static final Mapping PATH_TALK = new DefaultMapping("talk");
	private static final Mapping PATH_AWAY = new DefaultMapping("away");
	private static final Mapping PATH_JOIN = new DefaultMapping("join");
	private static final Mapping[] PATHES = new Mapping[] { PATH_TALK, PATH_AWAY, PATH_JOIN };

	private static final String PARAM_ROOM = "room";
	private static final String PARAM_MESSAGE = "message";
	private static final String PARAM_TALK_FILE = "talkfile";

    @Inject
    private InputFacade inputFacade;
	
	@Override	
	public Mapping[] getMappings() {
		return PATHES;
	}
	
	@Override
	public ControllerOutput process(MatchedMapping matchedPath, ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		Mapping path = matchedPath.getMapping();
		if (path == PATH_TALK) {
			talk(user, request);
			return new ControllerStatusOutput(200);
		} else if (path == PATH_AWAY) {
            inputFacade.away(user, !user.isAway());
			return new ControllerStatusOutput(200);
		} else if (path == PATH_JOIN) {
			Map<String, Object> parameters = getParameters(request);
            inputFacade.join(user, (String) parameters.get(PARAM_ROOM));
			return new ControllerStatusOutput(200);
		} else {
			return null;
		}
	}
	
	private void talk(ChatUser user, HttpServletRequest request) {
		Map<String, Object> parameters = getParameters(request);
		String message = (String) parameters.get(PARAM_MESSAGE);
		message = StringEscapeUtils.unescapeHtml4(message);
		Object fileObject = parameters.get(PARAM_TALK_FILE);
		FileUpload upload;
		if (fileObject != null && fileObject instanceof FileUpload) {
			upload = (FileUpload) fileObject;
		} else {
			upload = null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug(user + " says " + message);
		}
		String uploadName;
		InputStream uploadStream;
		if (upload != null) {
			uploadStream = upload.getStream();
			uploadName = upload.getFileName();
			if (message == null || message.trim().isEmpty()) {
				message = "Upload:";
			}
		} else {
			uploadStream = null;
			uploadName = null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug(user + " starting process() for " + message);
		}
		inputFacade.talk(user, message, uploadStream, uploadName);
	}

}
