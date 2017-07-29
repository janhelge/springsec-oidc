package no.politiet.ft;

import no.politiet.fellestjenester.felleskomponenter.webjarsclasspathutil.WebjarsClasspathVersionStringLookupUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Date;


@Configuration
@PropertySource(ignoreResourceNotFound = true, value = {
		"classpath:application.properties",
		"classpath:application-env.properties"})
//@Import({SpringSecurityConfig.class, JndiDaoConfig.class, LdapConfig.class})
@EnableWebMvc
@Import({DummyMvcController.class, ASpringSecConf.class})
public class MvcConfig extends WebMvcConfigurerAdapter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MvcConfig.class);
  
//     @Bean
//     public FotoregisterRapportServiceController pofServiceController(FotoregisterRapportDao pofReportDao, Environment env){
//    	 return new FotoregisterRapportServiceController(pofReportDao, env);
//     }

//    @Bean
//    public HelloWorldController helloWorldController(){ return new HelloWorldController();}
//
     @Bean
     public FotoregisterStatiskRedirigering statiskRedirigering()  {
    	 return new FotoregisterStatiskRedirigering();
     }


	@Bean
	public String bygginformasjon(Environment env){
		String timeStampProp = env.getProperty("prosjekt.timestamp");
		String timeStampString = " dato: <ikke tilgjengelig>";
		if (timeStampProp!=null && timeStampProp.length()>0) {
			try {
				timeStampString = " dato: " + new Date(Long.valueOf(timeStampProp)).toString();
			} catch (NumberFormatException ex) {
				timeStampString = "<prosjekt.timestamp=" + env.getProperty("prosjekt.timestamp") +
						" er et ugyldig, tolket som tidsformat>";
			}
		}

		return "Versjon " + env.getProperty("prosjekt.versjon", "?")
				+ " (" + env.getProperty("prosjekt.buildNumber", "?") + ") bygget av: "
				+ env.getProperty("prosjekt.byggetAv", "?") + timeStampString;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		registry
		.addResourceHandler("/secured/webjars/**")
		.addResourceLocations("classpath:/META-INF/resources/webjars/");
		
		String artifactId = "fotoreg-rapport-angular-webjar";
		String version = WebjarsClasspathVersionStringLookupUtility.findByArtifactId(artifactId);
		String angularWebjarClassPathResource = "classpath:/META-INF/resources/webjars/"  + artifactId + "/"  + version + "/";
		LOGGER.debug("Adding a resourceHandler for / pointing to " + angularWebjarClassPathResource);
		
		registry
		.addResourceHandler("/secured/**")
		.addResourceLocations(angularWebjarClassPathResource); /* Kan vurdere aa ta med flere lokasjoner her, a.la , "classpath:/plain/" */
	} 
}