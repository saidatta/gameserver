package org.softwarewolf.gameserver.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebMvcSecurity
// @EnableGlobalMethodSecurity(prePostEnabled=true)
// @Profile(SpringActiveProfiles.WEB_APPLICATION)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	// @Autowired
	// AuthenticationEntryPoint apiAuthenticationEntryPoint;

	// had to restort to lazy lodading due to circular dependency issue in unit
	// test runs
	@Autowired
	UserDetailsService customUserDetailsService;

	// @Autowired
	// CustomUserAuthFilter customUserAuthFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf()
				.disable()
				// // .addFilterAfter(new WebAsyncManagerIntegrationFilter(),
				// SecurityContextPersistenceFilter.class)
				// //
				// .exceptionHandling().authenticationEntryPoint(apiAuthenticationEntryPoint).and()
				// .headers().and()
				// // .addFilterBefore(customUserAuthFilter,
				// UsernamePasswordAuthenticationFilter.class)
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.ALWAYS).and()
				// .sessionFixation().migrateSession().and()
				// // .invalidSessionUrl("/web/login/sessionTimeout")
				// // .sessionAuthenticationStrategy(customAuthStrategy()).and()
				// .securityContext().and()
				// .requestCache().disable()
				// .servletApi().and()
				// .anonymous().and()
				// .logout() //need to add logout request mathching url
				// .deleteCookies("JSESSIONID")
				// .invalidateHttpSession(true).and()				
			.authorizeRequests()
				.antMatchers("/ckeditor/**").permitAll()	
				.antMatchers("/bootstrap/**").permitAll()	
				.antMatchers("/public/**").permitAll()
				.antMatchers("/resources/**").permitAll()
				.antMatchers("/login/**")
				.access("isAnonymous() or isAuthenticated()")
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/gamemaster/**").hasRole("GAMEMASTER")
				.antMatchers("/user/**").authenticated()
				.anyRequest().authenticated().and()
			.formLogin()
				.defaultSuccessUrl("/user/menu")
				.loginPage("/login").permitAll()
				.failureUrl("/login?error").and()
			.logout()
				.logoutSuccessUrl("/login").permitAll();
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/resources/**");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {

		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(customUserDetailsService);
		
		authProvider.setPasswordEncoder(new BCryptPasswordEncoder());

		auth.authenticationProvider(authProvider);
		auth.userDetailsService(customUserDetailsService);
	}

	// @Bean(name = "authenticationManager")
	// @Override
	// public AuthenticationManager authenticationManagerBean() throws Exception
	// {
	// return super.authenticationManagerBean();
	// }
}
