package no.politiet.publikumstjenester.kjoreseddel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class SpringWebConf {

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(1024*1024*3); // Max 3 meg
        return multipartResolver;
    }

    @Bean(name = "kjoreseddelSoknadHandler")
    public KjoreseddelSoknadHandler kjoreseddelSoknadHandler(){
        return new KjoreseddelSoknadHandlerImpl();
    }
}