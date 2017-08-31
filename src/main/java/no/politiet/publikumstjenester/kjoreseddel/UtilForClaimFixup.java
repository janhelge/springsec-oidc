package no.politiet.publikumstjenester.kjoreseddel;

import org.springframework.security.oauth2.oidc.core.user.DefaultOidcUser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UtilForClaimFixup {

    /*
    ID-Porten:
{
"sub" : "-v-lcae5rGG-jlvzuv9Y9H7R8NmAeM2-kh0qWb-vPIE=",
"aud" : "test_rp_yt2",
"acr" : "Level4",
"auth_time" : 1497605218,
"amr" : "BankID",
"iss" : "https://oidc-yt2.difi.eon.no/idporten-oidc-provider/",
"pid" : "23079410918",
"exp" : 1497605382,
"locale" : "nb",
"iat" : 1497605262,
"nonce" : "min_fine_nonce_verdi",
"jti" : "Hgb3zwO9g0bjmSbCCtQCxMowsZEu00lCJ2Exg4Zhv3g="
}

Mitre-ID:
{"sub":"fodselsnummer.12127219735",
"email_verified":false,"kid":"rsa1",
"iss":"http://localhost:8080/openid-connect-server-webapp/","preferred_username":"12127219735",
"given_name":"Simon","principal.getName()":"Simon Baumgartner","aud":["myspringsec"],
"nickname":"Simon","name":"Simon Baumgartner","exp":1503934081000,"family_name":"Baumgartner",
"iat":1503933481000,"email":"fodselsnummer.12127219735@fake.epost.com","jti":"bc60db9e-6b23-4921-a7f6-dd1ccd5162be"}
       */
        public static Map<String,Object> produceWhoAmIFromClaim(DefaultOidcUser defaultOidcUser) {

            Map<String,Object> ret = new HashMap<>();
            if (defaultOidcUser==null) {
                ret.put("ERROR", "DeaultOidcUser is <null>");
                return ret;
            }
            //ret.put("principal.getName()",defaultOidcUser.getName() != null ? defaultOidcUser.getName() : "<null>");
            Map<String, Object> claims = defaultOidcUser.getClaims();
            Set<String> supress = new HashSet<>();

            supress.add("preferred_username");
            supress.add("family_name");
            supress.add("given_name");
            supress.add("nickname");
            supress.add("name");
            String sub = (String) claims.get("sub");
            if (sub.startsWith("fodselsnummer.")) {
                ret.put("pid",sub.substring(14));
                ret.put("acr","TestLevelAcr");  // "Level4"
                ret.put("amr","KrimstadID");    // "BankID"
            }
            for (String key : claims.keySet()) {
                if (supress.contains(key)) continue;
                ret.put(key,claims.get(key));
            }
            return ret;
        }
}
