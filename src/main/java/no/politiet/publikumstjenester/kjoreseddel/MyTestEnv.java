package no.politiet.publikumstjenester.kjoreseddel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MyTestEnv {

    private static final Logger log = LoggerFactory.getLogger(MyTestEnv.class);


    @Autowired
    private Environment env;

    @Bean
    public String se() {

        return "stringen" ;
    }

}
