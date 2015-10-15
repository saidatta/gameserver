package org.softwarewolf.gameserver.base.config;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

public class MvcWebApplicationInitializer extends
		AbstractDispatcherServletInitializer {

	private AnnotationConfigWebApplicationContext context;

	private WebApplicationContext getContext() {
		if (context == null) {
			context = new AnnotationConfigWebApplicationContext();
			context.setConfigLocation("org.softwarewolf.gameserver.base.config");
			context.getEnvironment().setActiveProfiles(
					"spring.profiles.active",
					SpringActiveProfiles.WEB_APPLICATION);
		}
		System.setProperty("java.net.preferIPv4Stack" , "true");
		return context;
	}

	@Override
	protected WebApplicationContext createRootApplicationContext() {
		return getContext();
	}

	@Override
	protected WebApplicationContext createServletApplicationContext() {
		return getContext();
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

}
