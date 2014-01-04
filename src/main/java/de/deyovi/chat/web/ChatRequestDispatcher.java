package de.deyovi.chat.web;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.utils.ChatConfiguration;
import de.deyovi.chat.core.utils.ChatUtils;
import de.deyovi.chat.web.controller.Controller;
import de.deyovi.chat.web.controller.ControllerHTMLOutput;
import de.deyovi.chat.web.controller.ControllerJSONOutput;
import de.deyovi.chat.web.controller.ControllerOutput;
import de.deyovi.chat.web.controller.ControllerRedirectOutput;
import de.deyovi.chat.web.controller.ControllerStreamOutput;
import de.deyovi.chat.web.controller.ControllerViewOutput;
import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;

/**
 * Servlet implementation class ChatFacade
 */
public class ChatFacade extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8570145465987583123L;
	private static final Logger logger = LogManager.getLogger(ChatFacade.class);

	private final TreeMap<Mapping, Controller> controllers = new TreeMap<Mapping, Controller>();
	private final String urlPrefix;

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
					if (Controller.class.isAssignableFrom(clazz)) {
						de.deyovi.chat.web.controller.annotations.Controller annotation = clazz.getAnnotation(de.deyovi.chat.web.controller.annotations.Controller.class);
						boolean ignore = false;
						if (annotation != null && annotation.disabled()) {
							ignore = true;
						}
						if (!ignore) {
							controllers.add(clazz);
						}
					}
				}
			} catch (ClassNotFoundException e) {
				logger.error("Error while scanning Controllers", e);
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
		urlPrefix = ChatConfiguration.getUrlPrefix();
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
		if (requestPath == null || requestPath.isEmpty()) {
			requestPath = "/";
		} else if (!requestPath.equals("/")){
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
				ChatUser user = (ChatUser) session.getAttribute(SessionParameters.USER);
				ControllerOutput result = controller.process(matchedPath, user, request, response);
				if (result == null) {
					response.sendError(404);
				} else {
					response.setStatus(result.getStatus());
					response.setContentType(result.getContentType());
					if (result instanceof ControllerJSONOutput) {
						((ControllerJSONOutput) result).getJSON().write(response.getWriter());
					} else if (result instanceof ControllerHTMLOutput) {
						response.getWriter().write(((ControllerHTMLOutput) result).getHtml());
					} else if (result instanceof ControllerViewOutput) {
						ControllerViewOutput viewOutput = (ControllerViewOutput) result;
						if (viewOutput.getParameters() != null) {
							for (Entry<String, Object> parameter : viewOutput.getParameters().entrySet()) {
								request.setAttribute(parameter.getKey(), parameter.getValue());
							}
						}
						request.setAttribute("urlPrefix", urlPrefix);
						logger.debug("forwarding to:" + viewOutput.getTargetJSP());
				        request.getRequestDispatcher(viewOutput.getTargetJSP()).forward(request, response);
					} else if (result instanceof ControllerRedirectOutput) {
						String target = ((ControllerRedirectOutput) result).getTarget();
						if (target.startsWith("/")) {
							target = request.getContextPath() + target;
							System.out.println(target);
						}
						response.sendRedirect(target);
					} else if (result instanceof ControllerStreamOutput) {
						IOUtils.copy(((ControllerStreamOutput) result).getStream(), response.getOutputStream());
					}
				}
			} catch (Exception e) {
				logger.error("Error while processing request:", e);
			}
		}

	}

}