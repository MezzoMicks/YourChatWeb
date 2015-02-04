package de.deyovi.chat.web.controllers;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.facades.InputFacade;
import de.deyovi.chat.web.sesssion.ChatUserSession;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * Controller for general Input (talking and uploading) and some additional actions
 * @author Michi
 *
 */
@Controller
public class InputController {

	private static final Logger logger = LogManager.getLogger(InputController.class);

	private static final String PARAM_ROOM = "room";
	private static final String PARAM_MESSAGE = "message";
	private static final String PARAM_TALK_FILE = "talkfile";

    @Resource
    private InputFacade inputFacade;

    @Resource
    private ChatUserSession chatUserSession;


    public InputController() {

    }

    @RequestMapping("/away")
    public void away() {
        ChatUser chatUser = getChatUserSession().getCurrentChatUser();
        getInputFacade().away(chatUser, !chatUser.isAway());
    }

    @RequestMapping("/talk")
	public HttpStatus talk(@RequestParam(PARAM_MESSAGE) String message, @RequestParam(PARAM_TALK_FILE) MultipartFile upload) {
		final ChatUser user = getChatUserSession().getCurrentChatUser();
        message = StringEscapeUtils.unescapeHtml4(message);
		if (logger.isDebugEnabled()) {
			logger.debug(user + " says " + message);
		}
		String uploadName;
		InputStream uploadStream;
		if (upload != null) {
            try {
                uploadStream = upload.getInputStream();
                uploadName = upload.getName();
                if (message == null || message.trim().isEmpty()) {
                    message = "Upload:";
                }
            } catch (IOException ioex) {
                return HttpStatus.UNPROCESSABLE_ENTITY;
            }
		} else {
			uploadStream = null;
			uploadName = null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug(user + " starting process() for " + message);
		}
		getInputFacade().talk(user, message, uploadStream, uploadName);
        return HttpStatus.OK;
	}

    @RequestMapping("/join")
    public HttpStatus join(@RequestParam(PARAM_ROOM) String room) {
        final ChatUser chatUser = getChatUserSession().getCurrentChatUser();
        getInputFacade().join(chatUser, room);
        return HttpStatus.OK;
    }

    protected InputFacade getInputFacade() {
        return inputFacade;
    }

    public void setInputFacade(InputFacade inputFacade) {
        this.inputFacade = inputFacade;
    }

    protected ChatUserSession getChatUserSession() {
        return chatUserSession;
    }

    public void setChatUserSession(ChatUserSession chatUserSession) {
        this.chatUserSession = chatUserSession;
    }
}
