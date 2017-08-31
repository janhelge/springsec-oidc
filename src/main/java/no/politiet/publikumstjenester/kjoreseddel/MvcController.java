package no.politiet.publikumstjenester.kjoreseddel;

import no.politiet.fellestjenester.felleskomponenter.webjarsclasspathutil.WebjarsClasspathVersionStringLookupUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.oidc.core.user.DefaultOidcUser;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.MissingResourceException;


@Configuration
@EnableWebMvc
@EnableWebSecurity
@Controller
//@RequestMapping("/kjoreseddel/")
public class MvcController extends WebMvcConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(MvcController.class);
    private static final String frontendMavenArtifactId = "kjoreseddel-client";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry
        .addResourceHandler( "open/**") // <== /kjoreseddel/ +  open/**
        .addResourceLocations("classpath:/META-INF/dummy-web-root/");


        String version = WebjarsClasspathVersionStringLookupUtility.findByArtifactId(frontendMavenArtifactId);

        if (version!=null) {
            String frontendcodeResourceLocation = "classpath:"
                    + WebjarsClasspathVersionStringLookupUtility.STANDARD_CLASSPATH_LOCATION_FOR_WEBJARS
                    + frontendMavenArtifactId + "/" + version + "/";

            log.info("Benytter denne ressursen...: " + frontendcodeResourceLocation);  // ==> classpath:/META-INF/resources/webjars/kjoreseddel-client/0.1-SNAPSHOT/

            registry
            .addResourceHandler("/**") // <== /kjoreseddel/ + **
            .addResourceLocations(frontendcodeResourceLocation);

        } else {
            // ERROR: Mangler frontentkoden
            log.error("FATAL ERROR: Mangler frontend kode, kan ikke betjene klienter.");
            throw new MissingResourceException("Mangler frontend kode, kan ikke betjene klienter", frontendMavenArtifactId, "versjon");
        }
    }

    @RequestMapping(value = "/")
    public String securedIndexRedirect() {
        log.info("Redirect for / ");
        return "redirect:index.html";
    }

//    @RequestMapping(value = "/whoami")
//      public String whoamiRedirect() {
//        return "redirect:/api/whoami" ;
//    }

    @RequestMapping(value = "/log")
    public String shortLogout() {

        return "redirect:/logout" ;
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:bye"; // You can redirect wherever you want, but generally it'links a good practice to show login screen again.
    }

    @ResponseBody
    @RequestMapping(value = "api/whoami", method = RequestMethod.GET, produces = "application/json")
    public Map<String,Object> whoAmI() {
        return UtilForClaimFixup.produceWhoAmIFromClaim(myDefaultOidcUser());
    }

    private DefaultOidcUser myDefaultOidcUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth==null) {
            log.error("Auth is <null>, unable to fulfill whoami");
            return null;
        }

        log.info("About to get autentication.");
        Object principal = auth.getPrincipal();
        if (principal instanceof DefaultOidcUser) {
            return (DefaultOidcUser)principal;
        } else {
            log.warn("Ukjent principal, class: " + principal.getClass().getCanonicalName());
            throw new IllegalStateException("Ukjent principal, class: " + principal.getClass().getCanonicalName());
        }
    }

    // OPEN HTML-FOR-DEMO-OG-DEBUGGING-I-STARTEN

    @RequestMapping(value = { "open/","open"})
    public String openIndexRedirect() {

        return "redirect:/open/index.html";
    }


    @ResponseBody
    @RequestMapping(value= "open/welcome", method = RequestMethod.GET, produces = "text/html")
    public String welcome() {
        return StaticHtmlDemoUtil.links + "<br/>Welcome - this is the \"open/welcome\" handler (open, not secured)";
    }

    @ResponseBody
    @RequestMapping(value = "bye", method = RequestMethod.GET, produces = "text/html")
    public String bye() {
        return StaticHtmlDemoUtil.links + "<br/>Du er logget ut.<br/>Du kan logge inn igjen med <a href=\"/login\">denne</a> lenken.";
    }

    @ResponseBody
    @RequestMapping(value = "api/info", method = RequestMethod.GET, produces = "text/html")
    public String securedInfo() {
            return StaticHtmlDemoUtil.securedInfo(myDefaultOidcUser());
    }
}