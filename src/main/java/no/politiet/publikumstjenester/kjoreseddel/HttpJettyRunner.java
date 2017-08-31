package no.politiet.publikumstjenester.kjoreseddel;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ClassInheritanceHandler;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.webapp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import java.net.URL;

public class HttpJettyRunner {

    // NB: Variablene brukes i mitre-oauth2-client.properies file
    // OIDC_REDIRECT_PREFIX=http://localhost:8082/kjoreseddel/
    // OIDC_PROVIDER_PREFIX=http://localhost:8080/openid-connect-server-webapp/
    final static String REQUIRED_ENV_VARIABLE_OIDC_PROVIDER_PREFIX = "OIDC_PROVIDER_PREFIX";
    final static String REQUIRED_ENV_VARIABLE_OIDC_REDIRECT_PREFIX = "OIDC_REDIRECT_PREFIX";

    // NB: Defaultverdien for denne er $REQUIRED_ENV_VARIABLE_OIDC_REDIRECT_PREFIX
    private final static String VARIABLE_APP_CONTEXT_PREFIX = "APP_CONTEXT_PREFIX";
    public static String CONTEXTPATH;

    private static final Logger log = LoggerFactory.getLogger(HttpJettyRunner.class);
    /*
    security.oauth2.client.mitre.redirect-uri=http://localhost:8082/kjoreeddel/oauth2/authorize/code/mitre
                                              !-------------------------------!
    REDIRECT_URI_PREFIX=http://localhost:8082/kjoreseddel/
     */


    public static void main(String[] args) throws Exception {

        if (System.getenv("LOGBACK_CONFIGURATION_FILE") != null) {
            System.setProperty("logback.configurationFile", System.getenv("LOGBACK_CONFIGURATION_FILE"));
        } else {
            System.setProperty("logback.configurationFile", "src/test/resources/logback-test.xml");
        }

        throwIfEnvOrPropertyMissing(REQUIRED_ENV_VARIABLE_OIDC_PROVIDER_PREFIX);
        throwIfEnvOrPropertyMissing(REQUIRED_ENV_VARIABLE_OIDC_REDIRECT_PREFIX);
        log.info("appContextPrefix(): "+ appContextPrefix());
        URL url = new URL(appContextPrefix());

        int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
        CONTEXTPATH=url.getPath();
        log.info("ContextPath is " + CONTEXTPATH);

	    new HttpJettyRunner().startWebApp(port, CONTEXTPATH, ApplicationInitializer.class);
		log.info("Jetty is airborne :-), please verify on\n" +appContextPrefix());
	}



    private void startWebApp(int port, String contextPath,
			Class<? extends AbstractAnnotationConfigDispatcherServletInitializer> annotationConfigurationCls) throws Exception {

		String webroot ="src/main/webapp";
		WebAppContext context = new WebAppContext(webroot,contextPath);

		context.setConfigurations(new Configuration[] { // <== Note: Pay particular attention to initialization order of classes:
				new WebInfConfiguration(), 	// (1) <== Extracts war, orders jar and defines classpath
				new WebXmlConfiguration(), 	// (2) <== Processes a WEB-INF/web.xml file
				new MetaInfConfiguration(), // (3) <== Looks in container and webapp jars for META-INF/resources and META-INF/web-fragment.xml
				new FragmentConfiguration(),// (4) <== Processes all discovered META-INF/web-fragment.xml files
				new EnvConfiguration(), 	// (5) <== JNDI: java:comp/env for the webapps
				new PlusConfiguration(), 	// (6) <== JNDI: Processes JNDI related aspects of WEB-INF/web.xml and hooks up naming entries
				/*new AnnotationConfiguration(),*/ getAnnotationConfiguration(annotationConfigurationCls),// (7)
				new JettyWebXmlConfiguration() // (8) <== Processes a WEB-INF/jetty-web.xml file
		});

		Server server = new Server(port);
		server.setHandler(context);
		server.start();
		return;
	}

	private AnnotationConfiguration getAnnotationConfiguration(final Class<? extends AbstractAnnotationConfigDispatcherServletInitializer> cls) {
		return new AnnotationConfiguration() {
			@Override
			public void preConfigure(WebAppContext context) throws Exception {
				ClassInheritanceMap map = new ClassInheritanceMap();
				ConcurrentHashSet<String> hashSet = new ConcurrentHashSet<>();
				hashSet.add(cls.getName());
				map.put(WebApplicationInitializer.class.getName(), hashSet);
				context.setAttribute(CLASS_INHERITANCE_MAP, map);
				_classInheritanceHandler = new ClassInheritanceHandler(map);
			}
		};
	}
    private static String appContextPrefix() {
        String appContextPrefix;
        if ((appContextPrefix=System.getProperty(VARIABLE_APP_CONTEXT_PREFIX, System.getenv(VARIABLE_APP_CONTEXT_PREFIX)))==null)
            appContextPrefix=System.getProperty(REQUIRED_ENV_VARIABLE_OIDC_REDIRECT_PREFIX, System.getenv(REQUIRED_ENV_VARIABLE_OIDC_REDIRECT_PREFIX));
        return appContextPrefix;
    }

    private static void throwIfEnvOrPropertyMissing(String evnVariableName) {
        if (System.getProperty(evnVariableName, System.getenv(evnVariableName)) == null) {
            log.error("FATAL ERROR: Missing required system property or possibely OS-level-ENV-variable named: " + evnVariableName);
            throw new RuntimeException("FATAL ERROR: Missing required system property or possibely OS-level-ENV-variable named:" + evnVariableName);
        }
    }
}