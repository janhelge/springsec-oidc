package no.politiet.ft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@Import({CsrfFormBasedMvcController.class, MyMitreClientRegistrationRepo.class})

@PropertySource(ignoreResourceNotFound = true, value = {
        "classpath:mitre.client.properties",
        "classpath:application.properties",
        "classpath:application-env.properties"})

@EnableWebSecurity
public class FormbasedSpringSecConf extends WebSecurityConfigurerAdapter{

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;


    private static final Logger log = LoggerFactory.getLogger(FormbasedSpringSecConf.class);

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        log.debug("configureGlobal() was called");
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER")
                .and()
                .withUser("12127219735").password("password").roles("USER")
        ;
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        //Boolean isDebug = environment.getProperty("spring.security.debug", Boolean.class, Boolean.FALSE);
        web.debug(Boolean.TRUE);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                //.antMatchers("/secured/**")
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .clients(clientRegistrationRepository);
    }

    //@Override
    protected void XXconfigure(HttpSecurity http) throws Exception {

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