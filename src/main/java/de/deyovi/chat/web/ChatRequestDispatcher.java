package de.deyovi.chat.web;

import java.beans.Beans;
import java.beans.beancontext.BeanContextSupport;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Singleton;
import javax.ejb.embeddable.EJBContainer;
import javax.ejb.spi.EJBContainerProvider;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.deyovi.chat.web.controller.impl.ProfileController;
import org.apache.catalina.core.ApplicationContext;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.utils.ChatUtils;
import de.deyovi.chat.facades.SetupFacade;
import de.deyovi.chat.facades.impl.DefaultSetupFacade;
import de.deyovi.chat.web.controller.Controller;
import de.deyovi.chat.web.controller.ControllerHTMLOutput;
import de.deyovi.chat.web.controller.ControllerJSONOutput;
import de.deyovi.chat.web.controller.ControllerOutput;
import de.deyovi.chat.web.controller.ControllerRedirectOutput;
import de.deyovi.chat.web.controller.ControllerStreamOutput;
import de.deyovi.chat.web.controller.ControllerViewOutput;
import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;
import org.apache.naming.factory.EjbFactory;

/**
 * Servlet implementation class ChatFacade
 */
public class ChatRequestDispatcher extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8570145465987583123L;
	private static final Logger logger = LogManager.getLogger(ChatRequestDispatcher.class);

	private final TreeMap<Mapping, Controller> controllers = new TreeMap<Mapping, Controller>();
    @Inject
    private SetupFacade setupFacade;

    @PostConstruct
	private void setup() {
        for (Controller controller : ChatUtils.getBeansForType(Controller.class)) {
            if (controller != null) {
                for (Mapping mapping : controller.getMappings()) {
                    logger.info("adding controller "
                            + controller.getClass().getSimpleName() + " to path "
                            + mapping.getPathAsString());
                    controllers.put(mapping, controller);
                }
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
		ControllerOutput result;
//		String requestPath = request.getPathInfo();
		String uri = request.getRequestURI();
		logger.debug("uri:" + uri);
		String context = request.getServletContext().getContextPath();
		String shortURI = context.length() > 1 ? uri.substring(context.length()) : uri;
		logger.debug("context:" + context);
		String requestPath = shortURI;
		logger.debug(shortURI);
		// Too make sure, the Browser knows it's webroot
		if (requestPath == null || requestPath.isEmpty()) {
			// .. we redirect if there was no subsequent slash
			result = new ControllerRedirectOutput("/");
		} else { 
			if (!requestPath.equals("/")){
				requestPath = requestPath.substring(1);
			}
			logger.debug("called doGeneral with requestPath=" + requestPath);
			if (true || setupFacade.isInitialized() || requestPath.equals("init")) {
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
					result = null;
				} else {
					HttpSession session = request.getSession();
					ChatUser user = (ChatUser) session.getAttribute(SessionParameters.USER);
                    request.setAttribute("currentUser", user);
					result = controller.process(matchedPath, user, request, response);
				}
			} else {
				result = new ControllerRedirectOutput("/init");
			}
		}
		
		try {
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
						for (Entry<String, ? extends Object> parameter : viewOutput.getParameters().entrySet()) {
							request.setAttribute(parameter.getKey(), parameter.getValue());
						}
					}
					logger.debug("forwarding to:" + viewOutput.getTargetJSP());
			        request.getRequestDispatcher(viewOutput.getTargetJSP()).forward(request, response);
				} else if (result instanceof ControllerRedirectOutput) {
					String target = ((ControllerRedirectOutput) result).getTarget();
					if (target.startsWith("/")) {
						target = request.getServletContext().getContextPath() + target;
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