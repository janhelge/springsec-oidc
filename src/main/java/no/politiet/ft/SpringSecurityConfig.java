//package com.websystique.springmvc.controller;
//
//import no.politiet.fotoregister.rapport.springsec.userdetailserviceimpls.DelegatingAuthenticationUserDetailsService;
//import no.politiet.fotoregister.rapport.springsec.userdetailserviceimpls.LdapUserDetailsUtility;
//import no.politiet.fotoregister.rapport.springsec.userdetailserviceimpls.MineLdapAuthenticationUserDetailsService;
//import no.politiet.fotoregister.rapport.springsec.userdetailserviceimpls.ReflectionAccessUtilityForAuthenticationUserDetailsServiceForTestUsers;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.ldap.core.LdapTemplate;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
//import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
//
//@Configuration
//@EnableWebSecurity
//public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
//
//
//	private static Logger log = LoggerFactory.getLogger(WebSecurityConfigurerAdapter.class);
//
//
//	@Autowired
//    Environment environment;
//
//	@Bean
//	public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken>  authenticationUserDetailsService(){
//		MineLdapAuthenticationUserDetailsService ldapUds = new MineLdapAuthenticationUserDetailsService(new LdapUserDetailsUtility(ldapTemplate));
//		AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> ud = ReflectionAccessUtilityForAuthenticationUserDetailsServiceForTestUsers
//                .newUserDetailServiceInstance();
//
//		if (ud != null) {
//			log.error("NB: Kun for UTV, FUNK: Cascading AuthenticationUserDetailsService med TestbrukerDatabase. Dette er FEIL (ERROR) dersom dette forekommer i prod-miljoer!!! ");
//			return new DelegatingAuthenticationUserDetailsService(ud, ldapUds);
//		} else {
//			log.info("AuthenticationUserDetailsService er " + ldapUds.getClass().getCanonicalName());
//			return ldapUds;
//		}
//	}
//
//	@Override
//    public void configure(WebSecurity web) throws Exception {
//        Boolean isDebug = environment.getProperty("spring.security.debug", Boolean.class, Boolean.FALSE);
//        web.debug(isDebug);
//    }
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http
//		.sessionManagement()
//		.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
//
//// 		Vurdere om dette trengs etterhvert ...
////		http
////		.exceptionHandling()
////		.accessDeniedPage("/access-denied.html");
//
//		http
//			.csrf()
//			.disable() // <== Trenger csrf().disable() for at "/log" (dvs logout) skal fungere
//		;
//
//		http
//			.jee()
//			.mappableRoles("LAND", "DISTRIKT_LISTE", "DISTRIKT")
//			.authenticatedUserDetailsService(authenticationUserDetailsService())
//		;
//
//		http
//			.formLogin()
//			.loginPage("/login?reason=login")
//			.failureUrl("/login?reason=failure")
//			.permitAll()
//		;
//
//		http
//			.logout()
//			.logoutUrl("/log") // <== NOTE: Forutsetter http.csrf().disable() siden dette blir aktivert med GET!
//			.permitAll()
//			.invalidateHttpSession(true)
//			.logoutSuccessUrl("/login?reason=logout")
//		;
//
//		http
//			.authorizeRequests()
//			.antMatchers("/secured/**")
//			.hasAnyRole("LAND", "DISTRIKT_LISTE", "DISTRIKT")
//			.anyRequest()
//			.authenticated()
//		;
//
//	}
//
//}
