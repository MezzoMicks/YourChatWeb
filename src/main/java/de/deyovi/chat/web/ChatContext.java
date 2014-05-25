package de.deyovi.chat.web;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import de.deyovi.chat.core.utils.ChatConfiguration;

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
