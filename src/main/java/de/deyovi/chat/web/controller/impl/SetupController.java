package de.deyovi.chat.web.controller.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.facades.SetupFacade;
import de.deyovi.chat.facades.impl.DefaultSetupFacade;
import de.deyovi.chat.web.controller.ControllerOutput;
import de.deyovi.chat.web.controller.ControllerViewOutput;
import de.deyovi.chat.web.controller.Mapping;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;
import de.deyovi.chat.web.controller.annotations.Controller;

/**
 * Controller for general Input (talking and uploading) and some additional actions
 * @author Michi
 *
 */
@Controller(disabled = true)
public class SetupController extends AbstractFormController {

	private static final Logger logger = LogManager.getLogger(SetupController.class);
	private static final Mapping PATH_SETUP = new DefaultMapping("init");
	private static final Mapping[] PATHES = new Mapping[] { PATH_SETUP };

	private static final String PARAM_TOKEN = "token";
	private static final String PARAM_ACTION = "action";
	private static final String PARAM_USERNAME = "username";
	private static final String PARAM_PASSWORD = "password";
	private static final String ACTION_INITIIALIZE = "initialize";

	private final SetupFacade setupFacade = DefaultSetupFacade.getInstance();
	
	private String currentToken = null;
	
	@Override	
	public Mapping[] getMappings() {
		return PATHES;
	}
	
	@Override
	public ControllerOutput process(MatchedMapping matchedPath, ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		Mapping path = matchedPath.getMapping();
		if (path == PATH_SETUP) {
			Map<String, Object> parameters = getParameters(request);
			String error = null;
			String paramAction = (String) parameters.get(PARAM_ACTION);
			logger.info("payload: " + paramAction);
			if (paramAction != null && paramAction.equals(ACTION_INITIIALIZE)) {
				String token = (String) parameters.get(PARAM_TOKEN);
				if (currentToken == null) {
					logger.error("call to init while no token is available");
				} else if (token == null || !token.equals(currentToken)) {
					logger.error("call to init with invalid token! '" + token + "' current Token is '" + currentToken + "'");
				} else {
					logger.info("initializing system");
					String username = (String) parameters.get(PARAM_USERNAME);
					String password = (String) parameters.get(PARAM_PASSWORD);
					setupFacade.initialize(username, password);
				}
			}
			Map<String, Object> outputParameters = new HashMap<String, Object>();
			if (error == null) {
				outputParameters.put("error", error);
			} 
			currentToken = Long.toHexString(System.currentTimeMillis());
			outputParameters.put(PARAM_TOKEN, currentToken);
			return new ControllerViewOutput("initialize", outputParameters);
		} else {
			return null;
		}
	}
	

}
