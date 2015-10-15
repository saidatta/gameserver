package org.softwarewolf.gameserver.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages={"org.softwarewolf.gameserver.base"})
public class ClientWebConfig extends WebMvcConfigurerAdapter {
		// Maps resources path to webapp/resources
		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	        registry.addResourceHandler("*/*.js").addResourceLocations("/resources/js/");
		}
		
		// Only needed if we are using @Value and ${...} when referencing properties
		@Bean
		public static PropertySourcesPlaceholderConfigurer properties() {
			PropertySourcesPlaceholderConfigurer propertySources = new PropertySourcesPlaceholderConfigurer();
			// change the static string later - Tim 
			Resource[] resources = new ClassPathResource[] { 
					new ClassPathResource("spring.properties") };
			propertySources.setLocations(resources);
			propertySources.setIgnoreUnresolvablePlaceholders(true);
			return propertySources;
		}
		
		// Provides internationalization of messages
		@Bean
		public ReloadableResourceBundleMessageSource messageSource() {
			//ResourceBundleMessageSource source = new ResourceBundleMessageSource();
			ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
			source.setBasenames("/WEB-INF/view/i18n/messages");//, "/WEB-INF", "/WEB-INF/view", "view", "/view");
			return source;
		}	   
}
