package no.politiet.ft;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class ASecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer{
    public ASecurityWebApplicationInitializer(){
        super(ASpringSecConf.class);
    }
}
