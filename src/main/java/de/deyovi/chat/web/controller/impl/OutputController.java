package de.deyovi.chat.web.controller.impl;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.services.OutputService.OutputMeta;
import de.deyovi.chat.core.services.impl.HTMLMessageConsumer;
import de.deyovi.chat.facades.OutputFacade;
import de.deyovi.chat.web.controller.*;
import de.deyovi.chat.web.controller.Mapping.MatchedMapping;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@de.deyovi.chat.web.controller.annotations.Controller
public class OutputController implements Controller {

	private static final Logger logger = LogManager.getLogger(OutputController.class);

	private static final Mapping PATH_LISTEN = new DefaultMapping("listen");
	private static final Mapping PATH_REFRESH = new DefaultMapping("refresh");
	private static final Mapping[] PATHES = new Mapping[] { PATH_LISTEN, PATH_REFRESH };
	private final static int ALIVE_CYCLES = 100;
	private static Map<String, Integer> senslessCycleMap = new ConcurrentHashMap<String, Integer>();

    @Inject
	private OutputFacade facade;

	private enum OutputType {

		SYNC("sync"), ASYNC("async"), REGISTER("register");

		private final String id;

		private OutputType(String id) {
			this.id = id;
		}

		private static OutputType getById(String id) {
			if (id != null) {
				id = id.trim().toLowerCase();
				for (OutputType value : values()) {
					if (value.id.equals(id)) {
						return value;
					}
				}
			}
			return SYNC;
		}
	}

	@Override
	public Mapping[] getMappings() {
		return PATHES;
	}

	@Override
	public ControllerOutput process(MatchedMapping path, ChatUser user, HttpServletRequest request, HttpServletResponse response) {
		String lang = request.getParameter("lang");
		if (lang == null) {
			lang = "de";
		}
		Locale locale = new Locale(lang);

		if (path.equals(PATH_LISTEN)) {
			return output(user, locale, request);
		} else if (path.equals(PATH_REFRESH)) {
			return new ControllerJSONOutput(facade.refresh(user, locale));
		} else {
			return null;
		}
	}

	private ControllerOutput output(ChatUser user, Locale locale, HttpServletRequest request) {
		ControllerOutput result = null;
		OutputType type = OutputType.getById(request.getParameter("output"));
		logger.debug(request.getRemoteAddr() + " does " + type);
		try {
			final String listenid = request.getParameter("listenid");
			logger.debug(user + 	" starts to listen with id " + listenid);
			String htmlString = request.getParameter("html");
			boolean html = htmlString == null || Boolean.parseBoolean(htmlString);
			if (type == OutputType.ASYNC) {
				StringBuilder writer = new StringBuilder();
				OutputMeta meta = facade.listen(new HTMLMessageConsumer(writer), locale, user, listenid);
				if (html) {
					if (writer.length() == 0) {
						Integer senselessCycles = senslessCycleMap.get(listenid);
						if (senselessCycles == null) {
							senselessCycles = 0;
						}
						if (++senselessCycles % ALIVE_CYCLES == 0) {
							senselessCycles = 0;
							writeDummy(writer, 16);
						}
						senslessCycleMap.put(listenid, senselessCycles);
					}
					result = new ControllerHTMLOutput(writer.toString());
				} else if (meta.isInterrupted()) {
					result = new ControllerStatusOutput(307);
				}
			} else if (type == OutputType.REGISTER) {
                String listenId = facade.register(user);
				result = new ControllerJSONOutput(new JSONObject().put("listenId", listenId));
			}
		} catch (Exception ex) {
			logger.error(ex);
		}
		return result;
	}

	private static void writeDummy(Appendable appender, int length) throws IOException {
		appender.append("<!-- ");
		Random rnd = new Random();
		while (length-- > 0) {
			appender.append(new Integer((rnd.nextInt(10)+48)).toString());
		}
		appender.append("-->");
	}

}
