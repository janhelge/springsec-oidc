package no.politiet.ft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
@Configuration
@EnableWebSecurity
public class ASpringSecConf extends WebSecurityConfigurerAdapter{

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER")
                .and()
                .withUser("12127219735").password("password").roles("USER")
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .permitAll();


        http
                .authorizeRequests()
                .antMatchers("/secured/**").access("hasRole('ROLE_USER')")
                //.antMatchers("/dba/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_DBA')")
                .and().formLogin()
                .loginPage("/login?reason=login")
                .failureUrl("/login?reason=failure")
                .defaultSuccessUrl("/secured/hei")
                .permitAll();

        http
                .authorizeRequests()
                .antMatchers("/secured/**")
                .hasAnyRole("USER", "DISTRIKT_LISTE", "DISTRIKT")
                .anyRequest()
                .authenticated()
        ;
    }

}
