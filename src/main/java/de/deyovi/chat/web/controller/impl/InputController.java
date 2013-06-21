package de.deyovi.chat.web.controller.impl;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.services.CommandProcessorService;
import de.deyovi.chat.core.services.InputProcessorService;
import de.deyovi.chat.core.services.impl.DefaultCommandProcessorService;
import de.deyovi.chat.core.services.impl.DefaultInputProcessorService;
import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;

/**
 * Controller for general Input (talking and uploading) and some additional actions
 * @author Michi
 *
 */
public class InputController extends AbstractFormController {

	private static final Logger logger = LogManager.getLogger(InputController.class);
	private static final Mapping PATH_TALK = new DefaultMapping("talk");
	private static final Mapping PATH_AWAY = new DefaultMapping("away");
	private static final Mapping PATH_JOIN = new DefaultMapping("join");
	private static final Mapping[] PATHES = new Mapping[] { PATH_TALK, PATH_AWAY, PATH_JOIN };

	private static final String PARAM_ROOM = "room";
	private static final String PARAM_MESSAGE = "message";
	private static final String PARAM_TALK_FILE = "talkfile";

	private final CommandProcessorService commandService = DefaultCommandProcessorService.getInstance();
	private final InputProcessorService inputService = DefaultInputProcessorService.getInstance();
	
	@Override	
	public Mapping[] getMappings() {
		return PATHES;
	}
	
	@Override
	public Object process(MatchedMapping matchedPath, ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		Mapping path = matchedPath.getMapping();
		if (path == PATH_TALK) {
			talk(user, request);
			return true;
		} else if (path == PATH_AWAY) {
			commandService.away(user, !user.isAway());
			return true;
		} else if (path == PATH_JOIN) {
			Map<String, Object> parameters = getParameters(request);
			commandService.join(user, (String) parameters.get(PARAM_ROOM));
			return true;
		} else {
			return false;
		}
	}
	
	private void talk(ChatUser user, HttpServletRequest request) {
		Map<String, Object> parameters = getParameters(request);
		String message = (String) parameters.get(PARAM_MESSAGE);
		FileUpload upload = (FileUpload) parameters.get(PARAM_TALK_FILE);
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
		Segment[] segments = inputService.process(user, message, uploadStream, uploadName);
		if (segments != null) {
			user.getCurrentRoom().talk(user, segments);
		}
	}

}
