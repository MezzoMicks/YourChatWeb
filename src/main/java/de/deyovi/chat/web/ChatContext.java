package de.deyovi.chat.web;

import de.deyovi.chat.core.utils.ChatConfiguration;

import javax.servlet.*;
import java.util.Set;

public class ChatContext implements ServletContainerInitializer, ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
//		System.out.println("Log4JInitServlet is initializing log4j");
//		String log4jLocation = arg0.getServletContext().getRealPath("/META-INF/log4j.properties");
//		File configFile = new File(log4jLocation);
//		if (configFile.exists()) {
//			System.out.println("Initializing log4j with: " + log4jLocation);
//			PropertyConfigurator.configure(log4jLocation);
//		} else {
//			System.err.println("*** " + log4jLocation + " file not found, so initializing log4j with BasicConfigurator");
//			BasicConfigurator.configure();
//		}
//		System.out.println("initializing ChatConfiguration");
		ChatConfiguration.initialize();
		
	}

	@Override
	public void onStartup(Set<Class<?>> arg0, ServletContext arg1)
			throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
