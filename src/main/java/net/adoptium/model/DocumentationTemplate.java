package net.adoptium.model;

import io.quarkus.qute.RawString;

import java.io.*;
import java.util.stream.Collectors;

public class DocumentationTemplate {
    private final String docName;

    public DocumentationTemplate(String docName) {
        this.docName = docName;
    }

    public String getDocName() {
        return docName;
    }

    /**
     * @return returns the correct path to the docs html file in the correct language
     * TODO: language not yet implemented
     */
    public String getDocPath() {
        return "/templates/DocumentationResource/documentation/" + getDocName() + "/index.html";
    }

    /**
     * @return returns a RawString of the docs html file
     */
    public RawString getDocRaw() throws FileNotFoundException {
        try (InputStream inputStream = DocumentationTemplate.class.getResourceAsStream(getDocPath());
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String contents = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            return new RawString(contents);
        }catch (Exception e) {
            throw new FileNotFoundException("Cannot find the \"" + getDocName() + "\" document in " + getDocPath());
        }
    }
}
