package net.adoptium;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@HeaderParam("Accept-Language") String lang) {
        System.out.println("------------");
        System.out.println(lang);
        ResourceBundle mybundle = null;

        for (Locale.LanguageRange language : Locale.LanguageRange.parse(lang)) {
            Locale locale = Locale.forLanguageTag(language.getRange());
            try {
                mybundle = ResourceBundle.getBundle("MessageBundle", locale);
                System.out.println("Found language: " + locale);
                break;
            } catch (MissingResourceException ignore) {
                System.out.println("Language not found: " + locale);
            }
        }
        return mybundle.getString("greeting");
    }
}

