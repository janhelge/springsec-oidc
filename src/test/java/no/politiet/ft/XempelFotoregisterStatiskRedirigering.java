package no.politiet.ft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * Denne klassen redirigerer linkene "/bestemmelser", "/tips" og "/kontaktinfo".
 * Hensikten er aa parameterstyre hvor linkene peker, slik at vi fleksibelt kan 
 * bytte ut hvor de skal peke senere.
 */

@RequestMapping
public class XempelFotoregisterStatiskRedirigering {

	private static final Logger LOGGER = LoggerFactory.getLogger(XempelFotoregisterStatiskRedirigering.class);

	@Autowired
	private Environment env;

	@RequestMapping(value = "/secured/bestemmelser")
	public String linkTilVeiledningbestemmelser() throws IOException {
		String key = "link2veiledningbestemmelser";
		String toValue = env.getProperty(key, "UKJENT_ENV_" + key);
		LOGGER.debug("redirigerer /bestemmelser til " + toValue + " pga env[\"" + key + "\"]");
		return "redirect:" + toValue;

	}

	
	@RequestMapping(value = "/secured/log")
	public String linkTilLogout() throws IOException {
		String key = "link2logout";
		String toValue = env.getProperty(key, "UKJENT_ENV_" + key);
		LOGGER.debug("redirigerer /kontaktinfo til " + toValue + " pga env[\"" + key + "\"]");
		return "redirect:" + toValue;
	}
}