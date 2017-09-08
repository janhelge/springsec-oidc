package no.politiet.publikumstjenester.kjoreseddel;

import no.politiet.fellestjenester.felleskomponenter.webjarsclasspathutil.WebjarsClasspathVersionStringLookupUtility;
import no.politiet.publikumstjenester.kjoreseddel.domainobject.KjoreseddelSoknadDomainObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.oidc.core.user.DefaultOidcUser;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;


@Configuration
@EnableWebMvc
@EnableWebSecurity
@Controller
@Import(SpringWebConf.class)
@RequestMapping("/")
public class SpringWebMvcConf extends WebMvcConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(SpringWebMvcConf.class);
    private static final String frontendMavenArtifactId = "kjoreseddel-client";

    @Autowired
    private KjoreseddelSoknadHandler kjoreseddelSoknadHandler;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String version = WebjarsClasspathVersionStringLookupUtility.findByArtifactId(frontendMavenArtifactId);

        if (version == null) {  // <==  ERROR: Mangler frontentkoden
            log.error("FATAL ERROR: Mangler frontend kode, kan ikke betjene klienter.");
            throw new MissingResourceException("Mangler frontend kode, kan ikke betjene klienter", frontendMavenArtifactId, "versjon");
        }

        String frontendcodeResourceLocation = "classpath:"
                + WebjarsClasspathVersionStringLookupUtility.STANDARD_CLASSPATH_LOCATION_FOR_WEBJARS
                + frontendMavenArtifactId + "/" + version + "/";

        log.info("Frontend benytter denne ressursen...: " + frontendcodeResourceLocation);  // ==> classpath:/META-INF/resources/webjars/kjoreseddel-client/0.1-SNAPSHOT/

        registry
                .addResourceHandler("/**") // <== /kjoreseddel/ + **
                .addResourceLocations(frontendcodeResourceLocation);

        registry
                .addResourceHandler("open/**") // <== /kjoreseddel/ +  open/**
                .addResourceLocations("classpath:/META-INF/open-web-root/");

    }


    @RequestMapping(value = "/")
    public String securedIndexRedirect() {
        log.info("Redirect for / ");
        return "redirect:index.html";
    }

    @RequestMapping(value = "log")
    public String shortLogout() {
        return "redirect:logout";
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/bye"; // You can redirect wherever you want, but generally it'links a good practice to show login screen again.
    }

    // TODO: Swagger anoteringer @ApiOperation("") @ApiResponses=
    @ResponseBody
    @RequestMapping(value = "api/whoami", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> whoAmI() {
        return UtilForClaimFixup.emulateIDPortenFromMitreIDTestClaims(myDefaultOidcUser());
    }

    private DefaultOidcUser myDefaultOidcUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            log.error("Auth is <null>, unable to fulfill whoami");
            return null;
        }

        log.info("About to get autentication.");
        Object principal = auth.getPrincipal();
        if (principal instanceof DefaultOidcUser) {
            return (DefaultOidcUser) principal;
        } else {
            log.warn("Ukjent principal, class: " + principal.getClass().getCanonicalName());
            throw new IllegalStateException("Ukjent principal, class: " + principal.getClass().getCanonicalName());
        }
    }

    @PostMapping("api/soknad")
    public String soknad(
            @RequestParam("helseattest") MultipartFile[] helseattest,
            @RequestParam("kjentmannsprove") MultipartFile[] kjentmannsprove,
            @RequestParam String adresse,
            @RequestParam String annen_adresse,
            @RequestParam String soknaden_gjelder) throws IOException {
        KjoreseddelSoknadDomainObject e = createKjoreseddelSoknadDomainObject(whoAmI(), helseattest,
                kjentmannsprove, adresse, annen_adresse, soknaden_gjelder);

        kjoreseddelSoknadHandler.handle(e);

        return "redirect:/open/soknad-levert"; // <== FIXME: Endres til redirect:/ eller /takk-for-post
    }

    // OPEN HTML-FOR-DEMO-OG-DEBUGGING-I-STARTEN

    @RequestMapping(value = {"open/", "open"})
    public String openIndexRedirect() {
        return "redirect:/open/index.html";
    }

    @ResponseBody
    @RequestMapping(value = "open/soknad-levert", method = RequestMethod.GET, produces = "text/html")
    public String welcome() {
        return StaticHtmlDemoUtil.links + "<br/>Dette er \"open/soknad-levert\" handler (open, not secured).";
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


    private KjoreseddelSoknadDomainObject createKjoreseddelSoknadDomainObject(
            Map<String, Object> claim, MultipartFile[] helseattest, MultipartFile[] kjentmannsprove,
            String adresse, String annen_adresse, String soknaden_gjelder) throws IOException {
        log.info("api/Soknad Feltinput: adresse: " + adresse + " annen_adresse: " + annen_adresse + " soknaden_gjelder: " + soknaden_gjelder);

        KjoreseddelSoknadDomainObject e = new KjoreseddelSoknadDomainObject();

        e.put("adresse", adresse);
        e.put("annen_adresse", annen_adresse);
        e.put("soknaden_gjelder", soknaden_gjelder);

        if (claim.keySet().contains("pid")) {
            e.put("pid", claim.get("pid"));
            e.put("aud", claim.get("aud"));
            e.put("acr", claim.get("acr"));
            e.put("iss", claim.get("iss"));
        } else if (claim.keySet().contains("ERROR")) {
            e.put("ERROR", claim.get("ERROR"));
        }

        e.put("created", LocalDateTime.now());

        persisterFile(e, "kjentmannsprove", kjentmannsprove);
        persisterFile(e, "helseattest", helseattest);
        return e;
    }

    private void persisterFile(KjoreseddelSoknadDomainObject m, String filetype, MultipartFile[] files) throws IOException {
        for (int i = 0; i < files.length; i++) {
            log.info("api/Soknad files " + filetype + " " + i + " originalFilename(): " + files[i].getOriginalFilename() + " contentType: " + files[i].getContentType() + " size:" + files[i].getSize());
            String key = filetype + (i == 0 ? "" : Integer.toString(i));
            m.put(filetype + (i == 0 ? "" : Integer.toString(i)), files[i].getBytes());
        }
    }
}