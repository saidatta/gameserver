package org.softwarewolf.gameserver.base.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;

/**
 * We need this concrete implementation even if it's blank.
 */
public class ApplicationInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
	}

}
