package de.deyovi.chat.web.controllers;

import de.deyovi.chat.web.sesssion.ChatUserSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;


/**
 * Created by Michi on 15.01.2015.
 */
@Controller
@RequestMapping("/")
public class MainController {

    @Resource
    private ChatUserSession chatUserSession;

    @ModelAttribute
    public void getUser() {
        getChatUserSession().getCurrentChatUser();
    }

    @RequestMapping(method= RequestMethod.GET)
    public void home(ModelAndView modelAndView) {
        modelAndView.setViewName("chat");
    }

    public void setChatUserSession(ChatUserSession chatUserSession) {
        this.chatUserSession = chatUserSession;
    }

    protected ChatUserSession getChatUserSession() {
        return chatUserSession;
    }
}
