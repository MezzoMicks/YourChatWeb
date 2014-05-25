package de.deyovi.chat.web.controller.impl;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.services.EntityService;
import de.deyovi.chat.core.services.impl.DefaultEntityService;

public class Configurator implements ServletContextListener {

	private final static Logger logger = LogManager.getLogger(Configurator.class);

    @Inject
	private EntityService entityService;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		entityService.closeEntityManagerFactory();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		/*
		instance = this;
		// Fix might Help with some websites
		System.setProperty("http.agent", "");
		ServletContext context = event.getServletContext();
		webInfDir = context.getRealPath("WEB-INF");
		baseDir = context.getRealPath("/");
	*/
	}

	public void debug() {
		debug(null);
	}	
	
	public void debug(String dir) {
		if (dir == null) {
			dir = "./";
		} else if (!dir.endsWith("/")) {
			dir += "/";
		}
	
	}

}
