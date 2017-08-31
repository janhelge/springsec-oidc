package no.politiet.publikumstjenester.kjoreseddel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class StringTester {

    private static final Logger log = LoggerFactory.getLogger(MyTestEnv.class);

    public static void main(String[] a) throws MalformedURLException {
        StringTester x = new StringTester();
        x.go("http://bla/");
    }

    private void go(String hostAndContext) throws MalformedURLException {
        URL url = new URL(hostAndContext);

        log.error(Integer.toString(url.getDefaultPort())); // 80
        log.error(Integer.toString(url.getPort())); // 8080
        log.error(url.getAuthority()); // bla:8080
        log.error(url.getHost()); // bla
        log.error(url.getPath()); // /kjor
        log.error(url.getProtocol()); // http


    }
}
