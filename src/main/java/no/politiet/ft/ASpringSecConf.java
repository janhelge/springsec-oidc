package no.politiet.ft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class ASpringSecConf extends WebSecurityConfigurerAdapter{

    private static final Logger log = LoggerFactory.getLogger(ASpringSecConf.class);

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {


        log.debug("ConfigureGlobal was actially called");
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER")
                .and()
                .withUser("12127219735").password("password").roles("USER")
        ;
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
////        http
////                .authorizeRequests()
////                .anyRequest().authenticated()
////                .and()
////                .formLogin()
////                .loginPage("/login")
////                .permitAll();
//
//
//        http
//                .authorizeRequests()
//                .antMatchers("/secured/**").access("hasRole('USER')")
//                //.antMatchers("/dba/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_DBA')")
//                .and().formLogin()
//                .loginPage("/login?reason=login")
//                //.loginProcessingUrl("/perform_login")
//                .failureUrl("/login?reason=failure")
//                .defaultSuccessUrl("/secured/hei")
//                .permitAll();
//
////        http
////                .authorizeRequests()
////                .antMatchers("/secured/**")
////                .hasAnyRole("USER", "DISTRIKT_LISTE", "DISTRIKT")
////                .anyRequest()
////                .authenticated()
////        ;
//    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        //Boolean isDebug = environment.getProperty("spring.security.debug", Boolean.class, Boolean.FALSE);
        web.debug(Boolean.TRUE);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

// 		Vurdere om dette trengs etterhvert ...
//		http
//		.exceptionHandling()
//		.accessDeniedPage("/access-denied.html");

        http
                .csrf()
                .disable() // <== Trenger csrf().disable() for at "/log" (dvs logout) skal fungere
        ;

        http
                .jee()
                //.mappableRoles("LAND", "DISTRIKT_LISTE", "DISTRIKT")
                .mappableRoles("USER")
                //.authenticatedUserDetailsService(authenticationUserDetailsService())
        ;

//        http
//                .formLogin()
//                .loginPage("/login?reason=login")
//                .failureUrl("/login?reason=failure")
//                .usernameParameter("username")
//                .passwordParameter("password")
//                .loginProcessingUrl("/login")
//                .successForwardUrl("/secured/hei")
//                .permitAll()
//        ;
//
        http
                .logout()
                .logoutUrl("/log") // <== NOTE: Forutsetter http.csrf().disable() siden dette blir aktivert med GET!
                .permitAll()
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/login?reason=logout")
        ;

        // Granted Authorities: ROLE_USER' stored to HttpSession:
        http.httpBasic().realmName("MyRealmName");

        http
                .authorizeRequests()
                .antMatchers("/secured/**")
                .hasAnyRole("USER") // <== "USER" funker men "ROLE_USER" fungerer ikke
                .anyRequest()
                .authenticated()
        ;

    }


}
