package no.politiet.ft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@Import({MvcController.class, MitreOAuth2ClientRegistrationRepo.class})

@PropertySource(ignoreResourceNotFound = true, value = {
        "classpath:mitre-oauth2-client.properties",
        "classpath:application.properties",
        "classpath:application-env.properties"})

@EnableWebSecurity
public class SpringSecConf extends WebSecurityConfigurerAdapter{

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

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


    @Override
    public void configure(WebSecurity web) throws Exception {
        // Boolean isDebug = environment.getProperty("spring.security.debug", Boolean.class, Boolean.FALSE);
        // web.debug(Boolean.TRUE);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                //.antMatchers("/secured/**")  // NB: Ref springSecFilter faar requestene /secured/**
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .clients(clientRegistrationRepository);
    }

    //@Override
    protected void notUsedFormbasedconfigure(HttpSecurity http) throws Exception {

        http
            .authorizeRequests()
                .antMatchers("/secured/**")
                .hasAnyRole("USER") // <== "USER" funker men "ROLE_USER" fungerer ikke
                .anyRequest()
                .authenticated()

            .and()
                .formLogin()
                .permitAll()

            .and()
                .logout()
                .permitAll()
        ;
    }
}