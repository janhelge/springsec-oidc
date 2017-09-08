package no.politiet.publikumstjenester.kjoreseddel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;
import java.util.EnumSet;

public class ApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer{

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationInitializer.class);


    @Override
	public void onStartup(ServletContext sc) throws ServletException{
		super.onStartup(sc);
		registerSpringSecFilter(sc);
	}

    private void registerSpringSecFilter(ServletContext sc) {
		sc
		.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class.getName())
		.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),true, "/index.html", "/api/*","/logout","/login", "/oauth2/*");
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
	    Class cls = SpringSecConf.class;
		LOGGER.debug("RootConfigClasses is " + cls.getName());
		return new Class<?>[] {	cls };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null;
	}

	// NB: Denne kommer i tillegg til jettys context-path
	@Override
	protected String[] getServletMappings() {
		return new String[] { "/*" };
	}
}
