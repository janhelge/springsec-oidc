package no.politiet.ft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Configuration
@EnableWebMvc
@EnableWebSecurity
@Controller
@RequestMapping("/")
public class MvcController {

    private static final Logger log = LoggerFactory.getLogger(MvcController.class);


    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,produces = "text/html")
	public String welcome() {
        return links + "<br/>Welcome - this is the \"/\" handler";
	}


    @ResponseBody
	@RequestMapping(value="/bye", method = RequestMethod.GET, produces = "text/html")
	public String bye() {
		return links +"<br/>Utlogget.<br/>Du kan logge inn igjen ved &aring; bruke <a href=\"/login\">denne</a> lenken.";
	}


    @ResponseBody
    @RequestMapping(value="/secured/info", method = RequestMethod.GET, produces = "text/html")
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

        return links +"<br/><div style=\"color:#FF0000\">securedInfo() was called</div>";
    }

//    @RequestMapping(method = RequestMethod.GET,produces = "text/plain")
//    public String sayHello(ModelMap model) {
//        model.addAttribute("greeting", "Hello World from Spring 4 MVC");
//        return "welcome";
//    }


    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/bye"; //You can redirect wherever you want, but generally it'links a good practice to show login screen again.
    }

    private String links = "<a href=\"/login\">Login</a>.&nbsp;" +
                    "<a href=\"/\">Hjem (&aring;pen)</a>.&nbsp;" +
                    "<a href=\"/secured/info\">Sikret info</a>.&nbsp;" +
                    "<a href=\"/logout\">Logout</a>.&nbsp;"
            ;

}