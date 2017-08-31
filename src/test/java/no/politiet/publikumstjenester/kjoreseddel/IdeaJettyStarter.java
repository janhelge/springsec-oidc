package no.politiet.publikumstjenester.kjoreseddel;

public class IdeaJettyStarter {
    public static void main(String[] a) throws Exception {
        System.setProperty(HttpJettyRunner.REQUIRED_ENV_VARIABLE_OIDC_PROVIDER_PREFIX,"http://localhost:8080/openid-connect-server-webapp/");
        System.setProperty(HttpJettyRunner.REQUIRED_ENV_VARIABLE_OIDC_REDIRECT_PREFIX,"http://localhost:8082/"
                + "kjoreseddel/"
        );
        System.setProperty("logback.configurationFile", "src/test/resources/logback-test.xml");
        HttpJettyRunner.main(null);
    }
}
