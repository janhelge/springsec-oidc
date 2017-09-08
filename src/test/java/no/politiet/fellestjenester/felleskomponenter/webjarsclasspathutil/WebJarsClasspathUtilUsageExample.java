package no.politiet.fellestjenester.felleskomponenter.webjarsclasspathutil;

public class WebJarsClasspathUtilUsageExample {
    public static void main(String[] args) {
        String artifactId = "kjoreseddel-client";

        String version = WebjarsClasspathVersionStringLookupUtility.findByArtifactId(artifactId);

        if (version!=null) {
            System.out.println("Fant denne webjar-katalogen:\n"
                    + WebjarsClasspathVersionStringLookupUtility
                    .STANDARD_CLASSPATH_LOCATION_FOR_WEBJARS + artifactId + "/"
                    + version + "/");

        }
    }
}

/*
	// Foelgende kode-snippet er et mer praktisk eksempel, basert på spring-mvc...
	// I ekseplet settes opp en redirect til en webjar-modul sin index.html
	....

	@RequestMapping(value = "/")
	public String index() throws IOException {
		String t = getCachedRedirectPath();
		LOGGER.debug("Redirigerer til /webjars/" + t + "index.html");
		return "redirect:/webjars/" + t + "index.html";
	}

	private String __cachedRedirectPath;

	private String getCachedRedirectPath() {
		if (__cachedRedirectPath == null) {
			String artifactId = "pof-angular-rapporter";
			String version = WebjarsClasspathVersionStringLookupUtility.findByArtifactId(artifactId);
			if (version == null) {
				throw new RuntimeException("Finner ikke webJar for " + artifactId);
			}
			__cachedRedirectPath = artifactId + "/" + version + "/";
		}
		return __cachedRedirectPath;
	}
	...

*/