package no.politiet.ft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
		.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),
                true, "/secured/*","/logout","/login")
		;
	}


	@Override
	protected Class<?>[] getRootConfigClasses() {
	    Class cls = MvcConfig.class; // HelloWorldController.class ;
		LOGGER.debug("RootConfigClasses is " + cls.getName());
		return new Class<?>[] {	cls };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
}
