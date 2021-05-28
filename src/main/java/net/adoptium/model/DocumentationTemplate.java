package net.adoptium.model;

import io.quarkus.qute.RawString;
import net.adoptium.exceptions.DocumentNotFoundException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class DocumentationTemplate {
    private final String docName;
    private final String locale;
    private final String defaultLocale;
    private final RawString contents;

    public DocumentationTemplate(String docName, String locale, String defaultLocale) {
        this.docName = docName;
        this.locale = locale;
        this.defaultLocale = defaultLocale;

        try (InputStream inputStream = DocumentationTemplate.class.getResourceAsStream(getDocPath());
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            contents = new RawString(reader.lines().collect(Collectors.joining(System.lineSeparator())));
        } catch (Exception e) {
            throw new DocumentNotFoundException("Cannot find the \"" + getDocName() + "\" document in " + getDocPath());
        }
    }

    public String getDocName() {
        return docName;
    }

    /**
     * @return returns the correct path to the docs html file in the correct language
     */
    private String getDocPath() {
        if (locale.equals(defaultLocale)) {
            return "/templates/DocumentationResource/documentation/" + docName + "/index.html";
        } else {
            return "/templates/DocumentationResource/documentation/" + docName + "/index_" + locale + ".html";
        }
    }

    public RawString getContents() {
        return contents;
    }
}
