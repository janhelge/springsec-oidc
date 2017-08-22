package no.politiet.ft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableWebMvc
@EnableWebSecurity
@Controller
@RequestMapping("/")
public class MvcController extends WebMvcConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(MvcController.class);

    private String links = "<table><tr>" +
            "<td><a href=\"/login\">Login</a>.&nbsp;</td>" +
            "<td><a href=\"/\">Hjem (&aring;pen)</a>.&nbsp;</td>" +
            "<td><a href=\"/secured/info\">Sikret info</a>.&nbsp;</td>" +
            "<td><a href=\"/secured/whoami\">Sikret info whoami</a>.&nbsp;</td>" +
            "<td><a href=\"/logout\">Logout</a>.&nbsp;</td>" +
            "</tr></table>";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/secured/**")
                .addResourceLocations("/");
    }

    @RequestMapping(value = "/")
    public String securedIndexRedirect() {
        return "redirect:/secured/index.html" ;
    }

    @RequestMapping(value = "/whoami")
    public String whoamiRedirect() {
        return "redirect:/secured/whoami" ;
    }

    @ResponseBody
    @RequestMapping(value = "/secured/whoami", method = RequestMethod.GET, produces = "application/json")
    public Object json() {
        Map<String,Object> ret = new HashMap();
        StringBuilder sb = new StringBuilder();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (principal instanceof DefaultOidcUser) {
            DefaultOidcUser d = (DefaultOidcUser) principal;
            ret.put("principal.getName()",d.getName() != null ? d.getName() : "<null>");
            Map<String, Object> claims = d.getClaims();
            for (String key : claims.keySet()) {
                //ret.put(key,stringifyClaim(claims.get(key)));
                ret.put(key,claims.get(key));
            }
        } else {
            log.warn("Ukjent principal, class: " + principal.getClass().getCanonicalName());
        }
        return ret;
    }

    // HTML-FOR-DEMO

    @ResponseBody
    @RequestMapping(value= "/welcome", method = RequestMethod.GET, produces = "text/html")
    public String welcome() {
        return links + "<br/>Welcome - this is the \"/welcome\" handler (open, not secured)";
    }



    @ResponseBody
    @RequestMapping(value = "/bye", method = RequestMethod.GET, produces = "text/html")
    public String bye() {
        return links + "<br/>Du er logget ut.<br/>Du kan logge inn igjen med <a href=\"/login\">denne</a> lenken.";
    }

    @ResponseBody
    @RequestMapping(value = "/secured/info", method = RequestMethod.GET, produces = "text/html")
    public String securedInfo() {
        /*System.out.println("Stop her");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Object credentials = auth.getCredentials();
        Object details = auth.getDetails();
        Object principal = auth.getPrincipal();
        String name = auth.getName();
        boolean authenticated = auth.isAuthenticated();
        // authorities[0]  ->userinfo.claims.[0-7] samt -->authority=ROLE_USER samt -->attributes[0-13]
        // credential er ""
        // details.remoteAddress="127.0.0.1"
        // principal.{userinfo.claims,authorities(=="ROLE_USER"),attributes(som har 14 claims bl.a. "sub","iss") og nameAttributeKey="sub"}
        // name="Simon Baumgartner"
        */
        StringBuilder sb = new StringBuilder();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (principal instanceof DefaultOidcUser) {
            DefaultOidcUser d = (DefaultOidcUser) principal;

            sb.append("Name: ").append(d.getName() != null ? d.getName() : "<null>").append("<br/>");
            Map<String, Object> claims = d.getClaims();
            for (String key : claims.keySet()) {
                Object o = claims.get(key);
                String val = stringifyClaim(claims.get(key));
                sb.append("<span style=\"color:#B40404\">" + key + "</span>: " + val + "<br/>");
            }
        } else {
            log.debug("Ukjent principal, class: " + principal.getClass().getCanonicalName());
        }
        log.debug(sb.toString());
        return links + "<br/><span style=\"color:#FF0000\">securedInfo()</span> was called<br/>" +
                sb.toString();
    }

    private String stringifyClaim(Object o) {
        String clsType = "";
        if (o instanceof String) clsType = "";
        else if (o instanceof Boolean) clsType = "";
        else clsType = o.getClass().getCanonicalName();

        if (o == null) return clsType + "<null>";
        else return o + (clsType.equals("")?"":"("+clsType+")");
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:bye"; // You can redirect wherever you want, but generally it'links a good practice to show login screen again.
    }

    @RequestMapping(value = "/log")
    public String shortLogout() {
        return "redirect:/logout" ;
    }


}