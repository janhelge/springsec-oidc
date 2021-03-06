package no.politiet.publikumstjenester.kjoreseddel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@Import({SpringWebMvcConf.class, MitreOAuth2ClientRegistrationRepo.class, SpringWebConf.class})

@EnableWebSecurity
public class SpringSecConf extends WebSecurityConfigurerAdapter{

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            //.antMatchers("/api/**")  // NB: Ref springSecFilter. Faar alle requestene som filteret er configurert for
            .anyRequest()
            .authenticated()
            .and()
            .oauth2Login()
            .clients(clientRegistrationRepository);

        http.csrf().disable();
    }
//
//
//    @Autowired // <== Denne trengs bare ved formbased innlogging
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        log.debug("configureGlobal() was called");
//        auth
//                .inMemoryAuthentication()
//                .withUser("user").password("password").roles("USER")
//                .and()
//                .withUser("12127219735").password("password").roles("USER")
//        ;
//    }
//    @Override
//    public void configure(WebSecurity web) { // <== Useful for debugging of SpringSec, Please do not remove even if commented out
//        // Boolean isDebug = environment.getProperty("spring.security.debug", Boolean.class, Boolean.FALSE);
//        // web.debug(Boolean.TRUE);
//    }
//
//
//    //@Override
//    protected void notUsedFormbasedconfigure(HttpSecurity http) throws Exception {
//
//        http
//            .authorizeRequests()
//
//                .antMatchers("/index.html","/api/**")
//                .hasAnyRole("USER") // <== "USER" funker men "ROLE_USER" fungerer ikke
//                .anyRequest()
//                .authenticated()
//
//            .and()
//                .formLogin()
//                .permitAll()
//
//            .and()
//                .logout()
//                .permitAll()
//        ;
//    }
}