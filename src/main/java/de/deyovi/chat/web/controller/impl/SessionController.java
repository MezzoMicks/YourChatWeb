package de.deyovi.chat.web.controller.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.utils.ChatConfiguration;
import de.deyovi.chat.facades.SessionFacade;
import de.deyovi.chat.facades.impl.DefaultSessionFacade;
import de.deyovi.chat.web.SessionParameters;
import de.deyovi.chat.web.controller.ControllerJSONOutput;
import de.deyovi.chat.web.controller.ControllerOutput;
import de.deyovi.chat.web.controller.ControllerRedirectOutput;
import de.deyovi.chat.web.controller.ControllerViewOutput;
import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;

/**
 * Controller for general Input (talking and uploading) and some additional actions
 * @author Michi
 *
 */
public class SessionController extends AbstractFormController {

	private static final Logger logger = LogManager.getLogger(SessionController.class);
	private static final Mapping PATH_CHAT = new DefaultMapping("/");
	private static final Mapping PATH_LOGIN = new DefaultMapping("login");
	private static final Mapping PATH_LOGOUT = new DefaultMapping("logout");
	private static final Mapping PATH_REGISTER = new DefaultMapping("register");
	private static final Mapping PATH_SUGAR = new DefaultMapping("sugar");
	private static final Mapping[] PATHES = new Mapping[] { PATH_CHAT, PATH_LOGIN, PATH_LOGOUT, PATH_REGISTER, PATH_SUGAR };

	private final SessionFacade facade = DefaultSessionFacade.getInstance();
	
	@Override	
	public Mapping[] getMappings() {
		return PATHES;
	}
	
	@Override
	public ControllerOutput process(MatchedMapping matchedPath, ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		Mapping path = matchedPath.getMapping();
		if (path == PATH_CHAT) {
			return redirectUser(request.getSession(), user);
		} else if (path == PATH_LOGIN) {
			return login(user, request);
		} else if (path == PATH_LOGOUT) {
			return logout(request);
		} else if (path == PATH_REGISTER) {
			return register(request);
		} else if (path == PATH_SUGAR) {
			return sugar(request);
		} else {
			return null;
		}
	}
		
	private ControllerOutput logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session != null) {
			ChatUser user = (ChatUser) session.getAttribute(SessionParameters.USER);
			String locaLogoutKey = (String) session.getAttribute(SessionParameters.LOGOUT_KEY);
			if (user != null) {
				String paramLogoutKey = request.getParameter("key");
				if (paramLogoutKey.equals(locaLogoutKey)) {
					session.setAttribute(SessionParameters.USER, null);
					session.invalidate();
					return new ControllerRedirectOutput("/");
				} else {
					return null;
				}
			} else {
				logger.error("Logout without user!");
				return new ControllerRedirectOutput("/");
			}
		} else {
			return null;
		}
	}
	
	private ControllerOutput register(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session != null) {
			String username = request.getParameter("username");
			String password = request.getParameter("passwordHash");
			String inviteKey = request.getParameter("keyHash");
			String sugar = (String) session.getAttribute(SessionParameters.SUGAR);
			ChatUser newUser = facade.register(username, password, inviteKey, sugar);
			return redirectUser(session, newUser);
		} else {
			logger.error("Register without session!");
			return new ControllerViewOutput("WEB-INF/jsp/login.jsp", null);
		}
	}
	
	private ControllerOutput login(ChatUser user, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session != null) {
			String username = request.getParameter("username");
			String password = request.getParameter("passwordHash");
			String sugar = (String) session.getAttribute(SessionParameters.SUGAR);
			ChatUser newUser = facade.login(username, password, sugar);
			if (newUser != null) {
				session.setAttribute(SessionParameters.USER, newUser);
				session.setAttribute(SessionParameters.LOGOUT_KEY, newUser.getListenId());
				session.setMaxInactiveInterval(-1);
				return new ControllerRedirectOutput("/");
			} else {
				session.invalidate();
				return new ControllerViewOutput("WEB-INF/jsp/login.jsp", null);
			}
		} else {
			logger.error("Login without session!");
			return new ControllerViewOutput("WEB-INF/jsp/login.jsp", null);
		}
	}

	private ControllerOutput redirectUser(HttpSession session, ChatUser newUser) {
		if (newUser != null && newUser.getCurrentRoom() == null) {
            newUser = null;
            session.invalidate();
        }
        if (newUser == null) {
			Map<String, Object> loginParameters = new HashMap<String, Object>(1);
			loginParameters.put("keyRequired", ChatConfiguration.isInvitationRequired());
			return new ControllerViewOutput("WEB-INF/jsp/login.jsp", loginParameters);
		} else {
			return new ControllerViewOutput("WEB-INF/jsp/chat.jsp", null);
		}
	}

	public ControllerOutput sugar(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session != null) {
			String sugar = facade.getSugar();
			session = request.getSession(true);
			String userName = request.getParameter("user");
			session.setAttribute(SessionParameters.SUGAR, sugar + userName);
			try {
				return new ControllerJSONOutput(new JSONObject().put("sugar", sugar));
			} catch (JSONException e) {
				logger.error(e);
				return null;
			}
		} else {
			logger.warn("Sugarrequest without Session from User " + session.getAttribute(SessionParameters.USER));
			return null;
		}
	}
	
}
