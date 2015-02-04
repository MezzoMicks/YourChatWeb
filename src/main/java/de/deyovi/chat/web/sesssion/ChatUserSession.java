package de.deyovi.chat.web.sesssion;

import de.deyovi.chat.core.objects.ChatUser;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by Michi on 17.01.2015.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ChatUserSession {

    private ChatUser currentChatUser;

    public void setCurrentChatUser(ChatUser chatUser) {
        currentChatUser = chatUser;
    }

    public ChatUser getCurrentChatUser() {
        return currentChatUser;
    }

}
