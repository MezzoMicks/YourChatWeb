package de.deyovi.chat.web.security;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.services.ChatUserService;
import de.deyovi.chat.facades.SessionFacade;
import de.deyovi.chat.web.sesssion.ChatUserSession;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Michi on 17.01.2015.
 */
public class ChatAuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private ChatUserService chatUserService;
    private ChatUserSession chatUserSession;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        ChatUser chatUser = getChatUserService().getByName(authentication.getName());
        getChatUserSession().setCurrentChatUser(chatUser);
        response.sendRedirect("/chat");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof BadCredentialsException) {
            sendRedirect(request, response, "/login?error=credentials");
        } else {
            sendRedirect(request, response, "/login?error=internal");
        }
    }

    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String target) throws IOException {
        if (target.startsWith("/")) {
            target = request.getContextPath() + target;
        }
        response.sendRedirect(target);
    }

    @Required
    public void setChatUserSession(ChatUserSession chatUserSession) {
        this.chatUserSession = chatUserSession;
    }

    protected ChatUserSession getChatUserSession() {
        return chatUserSession;
    }

    @Required
    public void setChatUserService(ChatUserService chatUserService) {
        this.chatUserService = chatUserService;
    }

    protected ChatUserService getChatUserService() {
        return chatUserService;
    }

}
