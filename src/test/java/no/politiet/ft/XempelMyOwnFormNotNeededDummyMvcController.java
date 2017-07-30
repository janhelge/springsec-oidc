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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Configuration
@EnableWebMvc
@EnableWebSecurity
@Controller
@RequestMapping("/")
public class XempelMyOwnFormNotNeededDummyMvcController {

    private static final Logger log = LoggerFactory.getLogger(XempelMyOwnFormNotNeededDummyMvcController.class);

	@RequestMapping(method = RequestMethod.GET,produces = "text/plain")
    @ResponseBody
	public String sayHello() {

		return "sayHello() says welcome 1";
	}

	@RequestMapping(value="/helloagain", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
	public String sayHelloAgain() {
		return "sayHelloAgain() says welcome 2";
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
//    @RequestMapping(value="/helloagain", method = RequestMethod.GET, produces = "text/plain")
//    public String sayHelloAgain(ModelMap model) {
//        model.addAttribute("greeting", "Hello World Again, from Spring 4 MVC");
//        return "welcome";
//    }

    @RequestMapping(value = "/login-failed", produces = "text/html")
    @ResponseBody
    public String loginfailed() {
        return login("failure");
    }


    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?reason=logout"; //You can redirect wherever you want, but generally it's a good practice to show login screen again.
    }



    @ResponseBody
    @RequestMapping(value = "/login", produces = "text/html") // , method = RequestMethod.GET
    public String login(@RequestParam(value="reason", defaultValue="default") String reason) {
        return generateUserFriendlyMessageFromReason(reason).toString();
    }


    private StringBuilder appendStandardLoginForm(StringBuilder sb){
        return sb
                .append("<form action='/login' method='post' >")
                .append("<table><tr><td><label for='username'>Brukernavn</label></td>")
                .append("<td><input type='text' id='username' name='username'/></td></tr>")
                .append("<tr><td><label for='password'>Passord</label></td>")
                .append("<td><input type='password' id='password' name='password'/></td></tr>")
                .append("<tr><td colspan='2'>")
                .append("<input type='submit' name='action' value='Logg inn'/>")
                .append("</td></tr></table></form>")
                ;
    }


    private StringBuilder generateUserFriendlyMessageFromReason(final String reason) {
        StringBuilder sb = new StringBuilder();

        log.debug("Reason: >" + (reason == null ? "<null>" : reason) + "<");

        if (reason != null && "logout".equals(reason)) {
            return sb // <== We do not want to have login capabilities, we want user to click to another page
                    .append("<br/>Du er n&aring; logget ut.")
                    .append("<br/>Du kan logge inn igjen ved &aring; bruke <a href=\"login\">denne</a> lenken.")
                    ;
        }

        if (reason == null) {
            sb.append("<br/>Uventet parameter &lt;null&gt;<br/>"); // <== Denne skal ikke skje, men er grei aa mappe til tekst for sikkerthets skyld
        } else if ("default".equals(reason)) {
            sb.append("<br/>Innloggingsside(reason=default)<br/>");
        } else if ("login".equals(reason)) {
            sb.append("<br/>Vennligst logg inn (reason=login):<br/>");
        } else if ("failure".equals(reason)) {
            sb.append("<br/>Feil, Vennligst pr&oslash;v igjen(reason=failure):<br/>");
        } else {
            sb.append("<br/>Uventet parameter ").append(reason);
        }
        return appendStandardLoginForm(sb);
    }
}