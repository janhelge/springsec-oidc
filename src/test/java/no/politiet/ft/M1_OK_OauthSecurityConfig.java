//
//package no.politiet.ft;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.env.Environment;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
//import org.springframework.security.oauth2.core.AuthorizationGrantType;
//import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
//
//import java.util.Collections;
//import java.util.List;
//
////@EnableWebSecurity
////@PropertySource("classpath:oauth2-clients.properties")
//public class M1_OK_OauthSecurityConfig extends WebSecurityConfigurerAdapter {
//    private Environment environment;
//
//    public M1_OK_OauthSecurityConfig(Environment environment) {
//        this.environment = environment;
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .oauth2Login()
//                .clients(clientRegistrationRepository())
//                .userInfoEndpoint()
//                .userInfoTypeConverter(
//                        new UserInfoConverter(),
//                        new URI("https://www.googleapis.com/oauth2/v3/userinfo"));
//    }
//
//   // @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        List<ClientRegistration> clientRegistrations = Collections.singletonList(
//                clientRegistration("security.oauth2.client.google."));
//
//        return new InMemoryClientRegistrationRepository(clientRegistrations);
//    }
//
//    private ClientRegistration clientRegistration(String clientPropertyKey) {
//
//
//        // authorization_code
//        //() -> AuthorizationGrantType.AUTHORIZATION_CODE
//
//        String clientId = this.environment.getProperty(clientPropertyKey + "client-id");
//        String clientSecret = this.environment.getProperty(clientPropertyKey + "client-secret");
//        ClientAuthenticationMethod clientAuthenticationMethod = null; /*ClientAuthenticationMethod.valueOf(
//                this.environment.getProperty(clientPropertyKey + "client-authentication-method").toUpperCase());*/
//
//        AuthorizationGrantType authorizationGrantType = null; /*AuthorizationGrantType.valueOf(
//                this.environment.getProperty(clientPropertyKey + "authorized-grant-type").toUpperCase()); */
//        String redirectUri = this.environment.getProperty(clientPropertyKey + "redirect-uri");
//        String[] scopes = this.environment.getProperty(clientPropertyKey + "scopes").split(",");
//        String authorizationUri = this.environment.getProperty(clientPropertyKey + "authorization-uri");
//        String tokenUri = this.environment.getProperty(clientPropertyKey + "token-uri");
//        String userInfoUri = this.environment.getProperty(clientPropertyKey + "user-info-uri");
//        String clientName = this.environment.getProperty(clientPropertyKey + "client-name");
//        String clientAlias = this.environment.getProperty(clientPropertyKey + "client-alias");
//
//        return new ClientRegistration.Builder(clientId)
//                .clientSecret(clientSecret)
//                .clientAuthenticationMethod(clientAuthenticationMethod)
//                .authorizedGrantType(authorizationGrantType)
//                .redirectUri(redirectUri)
//                .scopes(scopes)
//                .authorizationUri(authorizationUri)
//                .tokenUri(tokenUri)
//                .userInfoUri(userInfoUri)
//                .clientName(clientName)
//                .clientAlias(clientAlias)
//                .build();
//    }
//}