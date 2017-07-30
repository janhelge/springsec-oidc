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
public class CsrfFormBasedMvcController {

    private static final Logger log = LoggerFactory.getLogger(CsrfFormBasedMvcController.class);


    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,produces = "text/plain")
	public String welcome() {
		return "Welcome 1 - this is \"/\" handler";
	}


    @ResponseBody
	@RequestMapping(value="/bye", method = RequestMethod.GET, produces = "text/html")
	public String bye() {
		return "<br/>Utlogget."
                + "<br/>Du kan logge inn igjen ved &aring; bruke <a href=\"login\">denne</a> lenken.";
	}


    @ResponseBody
    @RequestMapping(value="/secured/hei", method = RequestMethod.GET, produces = "text/plain")
    public String securedhei() {
        return "securedhei() says hei paa deg";
    }

//    @RequestMapping(method = RequestMethod.GET,produces = "text/plain")
//    public String sayHello(ModelMap model) {
//        model.addAttribute("greeting", "Hello World from Spring 4 MVC");
//        return "welcome";
//    }

//
//    @RequestMapping(value="/oauth2/authorization/code/mitre", method = RequestMethod.GET)
//    public String mitre(HttpServletRequest request, HttpServletResponse response) {
//
//        return "redirect:http://localhost:8080/openid-connect-server-webapp/login";
//    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/bye"; //You can redirect wherever you want, but generally it's a good practice to show login screen again.
    }

}