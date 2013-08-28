package de.deyovi.chat.web.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.services.ChatUserService;
import de.deyovi.chat.core.services.CommandProcessorService;
import de.deyovi.chat.core.services.impl.DefaultChatUserService;
import de.deyovi.chat.core.services.impl.DefaultCommandProcessorService;
import de.deyovi.chat.core.utils.PasswordUtil;
import de.deyovi.chat.web.SessionParameters;
import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;
import de.deyovi.chat.web.json.impl.DefaultJSONObject;

/**
 * Controller for general Input (talking and uploading) and some additional actions
 * @author Michi
 *
 */
public class SessionController extends AbstractFormController {

	private static final Logger logger = LogManager.getLogger(SessionController.class);
	private static final Mapping PATH_LOGIN = new DefaultMapping("login");
	private static final Mapping PATH_LOGOUT = new DefaultMapping("logout");
	private static final Mapping PATH_REGISTER = new DefaultMapping("register");
	private static final Mapping PATH_SUGAR = new DefaultMapping("sugar");
	private static final Mapping[] PATHES = new Mapping[] { PATH_LOGIN, PATH_LOGOUT, PATH_REGISTER, PATH_SUGAR };

	private static final String PARAM_ROOM = "room";
	private static final String PARAM_MESSAGE = "message";
	private static final String PARAM_TALK_FILE = "talkfile";

	private final CommandProcessorService commandService = DefaultCommandProcessorService.getInstance();
	private final ChatUserService chatUserService = DefaultChatUserService.getInstance();
	
	@Override	
	public Mapping[] getMappings() {
		return PATHES;
	}
	
	@Override
	public Object process(MatchedMapping matchedPath, ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		Mapping path = matchedPath.getMapping();
		if (path == PATH_LOGIN) {
			return login(user, request, response);
		} else if (path == PATH_LOGOUT) {
			return logout(request, response);
		} else if (path == PATH_REGISTER) {
			return register(request, response);
		} else if (path == PATH_SUGAR) {
			return sugar(request);
		} else {
			return false;
		}
	}
		
	private Object logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session != null) {
			ChatUser user = (ChatUser) session.getAttribute(SessionParameters.USER);
			String locaLogoutKey = (String) session.getAttribute(SessionParameters.LOGOUT_KEY);
			if (user != null) {
				String paramLogoutKey = request.getParameter("key");
				if (paramLogoutKey.equals(locaLogoutKey)) {
					chatUserService.logout(user);
					session.setAttribute(SessionParameters.USER, null);
					session.invalidate();
					return "redirect:login.jsp";
				} else {
					return false;
				}
			} else {
				logger.error("Logout without user!");
				return "redirect:login.jsp";
			}
		} else {
			return false;
		}
	}
	
	private String register(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session != null) {
			String username = request.getParameter("username");
			String password = request.getParameter("passwordHash");
			String invitekey = request.getParameter("keyHash");
			String sugar = (String) session.getAttribute(SessionParameters.SUGAR);
			String realSugar = sugar.substring(0, sugar.length() - username.length());
			ChatUser newUser = chatUserService.register(username, password, invitekey, realSugar);
			return redirectUser(session, newUser);
		} else {
			logger.error("Register without session!");
			return "redirect:login.jsp";
		}
	}
	
	private String login(ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session != null) {
			String username = request.getParameter("username");
			String password = request.getParameter("passwordHash");
			String sugar = (String) session.getAttribute(SessionParameters.SUGAR);
			logger.debug("login -> user:" + username + " pass: " + password + " sugar: " + sugar);
			if (sugar.endsWith(username)) {
				String realSugar = sugar.substring(0, sugar.length() - username.length());
				ChatUser newUser = chatUserService.login(username, password, realSugar);
				return redirectUser(session, newUser);
			} else {
				session.invalidate();
				logger.error("Sugar didn't match user: got username '" + username + "' expected sugar '" + sugar + "'. Mixed up sessions?");
				return "redirect:login.jsp";
			}
		} else {
			logger.error("Login without session!");
			return "redirect:login.jsp";
		}
	}

	private String redirectUser(HttpSession session, ChatUser newUser) {
		if (newUser == null) {
			return "redirect:login.jsp";
		} else {
			session.setAttribute(SessionParameters.USER, newUser);
			String logoutKey = Long.toHexString(System.currentTimeMillis());
			session.setAttribute(SessionParameters.LOGOUT_KEY, logoutKey);
			session.setMaxInactiveInterval(-1);
			return "redirect:";
		}
	}

	public Object sugar(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session != null) {
			String sugar = PasswordUtil.getSugar();
			session = request.getSession(true);
			String userName = request.getParameter("user");
			session.setAttribute(SessionParameters.SUGAR, sugar + userName);
			return new DefaultJSONObject("sugar", sugar);
		} else {
			logger.warn("Sugarrequest without Session from User " + session.getAttribute(SessionParameters.USER));
			return false;
		}
	}
	
}
