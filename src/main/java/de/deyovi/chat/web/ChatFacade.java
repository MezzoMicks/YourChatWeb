package de.deyovi.chat.web;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.services.ChatUserService;
import de.deyovi.chat.core.services.impl.DefaultChatUserService;
import de.deyovi.chat.core.utils.ChatUtils;
import de.deyovi.chat.web.controller.Controller;
import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;
import de.deyovi.chat.web.json.JSONObject;

/**
 * Servlet implementation class ChatFacade
 */
public class ChatFacade extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8570145465987583123L;
	private static final String PREFIX_REDIRECT = "redirect:";
	private static final String UTF_8 = "UTF-8";
	private static final Logger logger = LogManager.getLogger(ChatFacade.class);

	private final ChatUserService userService = DefaultChatUserService.getInstance();

	private final TreeMap<Mapping, Controller> controllers = new TreeMap<Mapping, Controller>();

	/**
	 * Default constructor.
	 */
	public ChatFacade() {
		List<Class> controllers = new LinkedList<Class>();
		List<String> classes = ChatUtils.getClassNamesFromPackage("de.deyovi.chat.web", true);
		for (String className : classes) {
			try {
				Class<?> clazz = Class.forName(className);
				if ((clazz.getModifiers() & Modifier.ABSTRACT) == 0 && (clazz.getModifiers() & Modifier.INTERFACE) == 0 ) {
					if (ChatUtils.checkInheritance(clazz, Controller.class)) {
						controllers.add((Class<Controller>) clazz);
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		for (Class clazz : controllers) {
			try {
				logger.info("Instantiating Controller:" + clazz.getName());
				addController((Controller) clazz.getConstructors()[0].newInstance());
			} catch (Exception e) {
				logger.error("Error while instantiating Controller:" + clazz.getName(), e);
			}
		}
	}

	public void addController(Controller controller) {
		if (controller != null) {
			for (Mapping mapping : controller.getMappings()) {
				logger.info("adding controller "
						+ controller.getClass().getSimpleName() + " to path "
						+ mapping.getPathAsString());
				controllers.put(mapping, controller);
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGeneral(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGeneral(request, response);
	}

	private void doGeneral(HttpServletRequest request, HttpServletResponse response) {
		String requestPath = request.getPathInfo();
		if (!requestPath.isEmpty()) {
			requestPath = requestPath.substring(1);
		}
		logger.debug("called doGeneral with requestPath=" + requestPath);
		Controller controller = null;
		MatchedMapping matchedPath = null;
		for (Mapping pathPrefix : controllers.descendingKeySet()) {
			MatchedMapping tmpPath = pathPrefix.match(requestPath, request.getMethod());
			if (tmpPath != null) {
				matchedPath = tmpPath;
				controller = controllers.get(pathPrefix);
				break;
			}
		}
		if (controller == null) {
			response.setStatus(403);
		} else {
			try {
				HttpSession session = request.getSession();
				String userSessionID = (String) session.getAttribute(SessionParameters.USER);
				ChatUser user;
				if (userSessionID == null) {
					user = null;
				} else {
					user = userService.getBySessionId(userSessionID);
				}
				Object result = controller.process(matchedPath, user, request,
						response);
				if (result instanceof String) {
					String resultString = (String) result;
					if (resultString.startsWith(PREFIX_REDIRECT)) {
						response.sendRedirect(resultString
								.substring(PREFIX_REDIRECT.length() + 1));
					} else {
						response.setContentType("text/html");
						response.setCharacterEncoding(UTF_8);
						response.getWriter().write(resultString);
					}
				} else if (result instanceof JSONObject) {
					JSONObject resultJSON = (JSONObject) result;
					response.setContentType("application/json");
					response.setCharacterEncoding(UTF_8);
					resultJSON.appendTo(response.getWriter());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}