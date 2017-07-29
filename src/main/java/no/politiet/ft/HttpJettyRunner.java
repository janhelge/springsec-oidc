package no.politiet.ft;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ClassInheritanceHandler;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.webapp.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class HttpJettyRunner {
	public static void main(String[] args) throws Exception {
        //System.setProperty("logback.configurationFile", "src/test/resources/logback-test.xml");
		int port=8080;
		String contextPath="/";
	    new HttpJettyRunner().startWebApp(port, contextPath , ApplicationInitializer.class, "fotoreg-rapport-angular-webjar" );
		System.out.println("Jetty is airborne :-), du kan verifisere paa\nhttp://localhost:" + port + contextPath+"/");
	}

	private void startWebApp(int port, String contextPath,
			Class<? extends AbstractAnnotationConfigDispatcherServletInitializer> annotationConfigurationCls, String artifactIdHoldingWebroot) throws Exception {

		String webroot = "src/main"; //"/webapp"; // getWebjarPath(artifactIdHoldingWebroot);
		WebAppContext context = new WebAppContext(webroot,contextPath);

		/* Her er alternativer som kan vaere aktuelle ifbm testing senere:*/
		// System.setProperty("jade.kjoremiljo", "UTV");
		// context.getInitParams().put("logback.configurationFile", "src/test/resources/logback-test.xml");

		EnvConfiguration envConfiguration = new EnvConfiguration();
		// envConfiguration.setJettyEnvXml(getClassPathResourceURL("jetty/jetty-env.xml"));

		context.setConfigurations(new Configuration[] { // <== Note: Pay particular attention to initialization order of classes:
				new WebInfConfiguration(), 	// (1) <== Extracts war, orders jar and defines classpath
				new WebXmlConfiguration(), 	// (2) <== Processes a WEB-INF/web.xml file
				new MetaInfConfiguration(), // (3) <== Looks in container and webapp jars for META-INF/resources and META-INF/web-fragment.xml
				new FragmentConfiguration(),// (4) <== Processes all discovered META-INF/web-fragment.xml files
				envConfiguration/*new EnvConfiguration()*/, 	// (5) <== JNDI: java:comp/env for the webapps
				new PlusConfiguration(), 	// (6) <== JNDI: Processes JNDI related aspects of WEB-INF/web.xml and hooks up naming entries
				/*new AnnotationConfiguration(),*/ getAnnotationConfiguration(annotationConfigurationCls),// (7)
				new JettyWebXmlConfiguration() // (8) <== Processes a WEB-INF/jetty-web.xml file
		});

		/* Dette er en alternativ maate aa legge inn SpringSec og kan med fordel benyttes etterhvert som
		 * liberty ogsaa supporter SpringSec-java-config.
		 */
        // context.addFilter(new FilterHolder(new DelegatingFilterProxy("springSecurityFilterChain"))
        // , "/*", EnumSet.allOf(DispatcherType.class));

		/*
		 * Alternativ innlogging, f.eks. ifbm testing.
		 */
//		SecurityHandler securityHandler = context.getSecurityHandler();
//
//		HashLoginService loginService = new HashLoginService();
//		loginService.setConfig("src/test/resources/jetty/jetty-brukere.properties");
//
//		securityHandler.setLoginService(loginService);
//		securityHandler.setAuthMethod("BASIC");
//		context.setSecurityHandler(securityHandler);

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

	private URL getClassPathResourceURL(String relativePath) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource(relativePath);
		if (!classPathResource.exists()) {
			throw new FileNotFoundException("Fil ikke funnet: " + relativePath);
		}
		return classPathResource.getURL();
	}


	// Kan benyttes dersom man har web.xml-fri contextroot. Liberty supporter imidlertid ikke dette
	// og dette blir til da mest aktuelt der man benytter jetty alene.
//	private String getWebjarPath(String artifactId) {
//		String version = WebjarsClasspathVersionStringLookupUtility.findByArtifactId(artifactId);
//		if (version == null) {
//			throw new RuntimeException("Finner ikke webJar for " + artifactId);
//		}
//		return this.getClass().getResource("/META-INF/resources/webjars/"
//		+ artifactId + "/"
//		+ version).toExternalForm();
//	}
}