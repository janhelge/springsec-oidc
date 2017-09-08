package no.politiet.publikumstjenester.kjoreseddel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.oidc.core.user.DefaultOidcUser;

import java.util.Map;

public class StaticHtmlDemoUtil {

    private static final Logger log = LoggerFactory.getLogger(StaticHtmlDemoUtil.class);

    public static String links = "<table><tr>" +
            "<td><a href=\"login\">Login</a>.&nbsp;</td>" +
            "<td><a href=\"open/index.html\">&Aring;pen del av applikasjonen, hjelp etc</a>.&nbsp;</td>" +
            "<td><a href=\"open/fileup.html\">DEBUG-Soknad-Skjema, &Aring;pen (DEBUG)</a>.&nbsp;</td>" +
            "<td><a href=\"api/info\">Sikret info</a>.&nbsp;</td>" +
            "<td><a href=\"api/whoami\">Sikret info whoami</a>.&nbsp;</td>" +
            "<td><a href=\"logout\">Logout</a>.&nbsp;</td>" +
            "</tr></table>";


    public static String securedInfo(DefaultOidcUser d) {
        /*
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
        if (d==null) {
            return sb.append("ERROR, DefaultOidcUser is <null>, returning fake").toString();
        }
        sb.append("Name: ").append(d.getName() != null ? d.getName() : "<null>").append("<br/>");
        Map<String, Object> claims = d.getClaims();
        for (String key : claims.keySet()) {
            Object o = claims.get(key);
            String val = stringifyClaim(claims.get(key));
            sb.append("<span style=\"color:#B40404\">" + key + "</span>: " + val + "<br/>");
        }

        return links + "<br/><span style=\"color:#FF0000\">securedInfo()</span> was called<br/>" +
                sb.toString();
    }


    private static String stringifyClaim(Object o) {
        String clsType = "";
        if (o instanceof String) clsType = "";
        else if (o instanceof Boolean) clsType = "";
        else clsType = o.getClass().getCanonicalName();

        if (o == null) return clsType + "<null>";
        else return o + (clsType.equals("")?"":"("+clsType+")");
    }
}
