package no.politiet.publikumstjenester.kjoreseddel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.Collections;
import java.util.List;

@Configuration

@PropertySource(ignoreResourceNotFound = true, value = {
        "classpath:mitre-oauth2-client.properties",
        "classpath:application.properties",
        "classpath:application-env.properties"})

public class MitreOAuth2ClientRegistrationRepo {

    private static final Logger log = LoggerFactory.getLogger(MitreOAuth2ClientRegistrationRepo.class);


    @Autowired
    private Environment environment;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> clientRegistrations = Collections.singletonList(
                clientRegistration("security.oauth2.client.mitre.")); // Was: "security.oauth2.client.google."
        return new InMemoryClientRegistrationRepository(clientRegistrations);
    }

    private ClientRegistration clientRegistration(String clientPropertyKey) {

        String clientId = this.environment.getProperty(clientPropertyKey + "client-id");
        String clientSecret = this.environment.getProperty(clientPropertyKey + "client-secret");
        ClientAuthenticationMethod clientAuthenticationMethod = new ClientAuthenticationMethod(
                this.environment.getProperty(clientPropertyKey + "client-authentication-method"));
        AuthorizationGrantType authorizationGrantType = AuthorizationGrantType.AUTHORIZATION_CODE;
        // Was: AuthorizationGrantType.valueOf(this.environment.getProperty(clientPropertyKey + "authorized-grant-type").toUpperCase());
        String redirectUri = this.environment.getProperty(clientPropertyKey + "redirect-uri");
        String[] scopes = this.environment.getProperty(clientPropertyKey + "scopes").split(",");
        String authorizationUri = this.environment.getProperty(clientPropertyKey + "authorization-uri");
        String tokenUri = this.environment.getProperty(clientPropertyKey + "token-uri");
        String userInfoUri = this.environment.getProperty(clientPropertyKey + "user-info-uri");
        String jwkSetUri = this.environment.getProperty(clientPropertyKey + "jwk-set-uri");
        String clientName = this.environment.getProperty(clientPropertyKey + "client-name");
        String clientAlias = this.environment.getProperty(clientPropertyKey + "client-alias");

        // =================================================================
        // FEILBESKRIVELSE  SpringSecurity, Feil link i valg av loginmetode.
        // =================================================================
        // org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter
        // i jar: springsecurity-web FeilgenerertLink er http://localhost:8082/oauth2/authorization/code/mitre, skulle vært
        // http://localhost:8082/kjoreseddel/oauth2/authorization/code/mitre (dvs med context-root)
        // DefaultLoginPageGeneratingFilter genererer feil login link fordi contextroot ikke er med
        // security.oauth2.client.mitre.redirect-uri=${OIDC_REDIRECT_PREFIX}oauth2/authorize/code/mitre i property-fila

        log.info(
            "Oidc config med ekspandert environment ...\n"
            + " ${OIDC_PROVIDER_PREFIX}authorize ekspanderes til ==> "
            + "\tauthorizationUri: \t" + authorizationUri+ "\n" // == ${OIDC_PROVIDER_PREFIX}authorize
            + "${OIDC_REDIRECT_PREFIX}oauth2/authorize/code/mitre ==> "
            + "\tredirectUri: \t" + redirectUri                 // == ${OIDC_REDIRECT_PREFIX}oauth2/authorize/code/mitre
        );

        return new ClientRegistration.Builder(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(clientAuthenticationMethod)
                .authorizedGrantType(authorizationGrantType)
                .redirectUri(redirectUri)
                .scopes(scopes)
                .authorizationUri(authorizationUri)
                .tokenUri(tokenUri)
                .userInfoUri(userInfoUri)
                .jwkSetUri(jwkSetUri)
                .clientName(clientName)
                .clientAlias(clientAlias)
                .build();
    }
}
