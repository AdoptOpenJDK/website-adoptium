package net.adoptium.model;

import io.quarkus.qute.Engine;
import io.quarkus.qute.RawString;
import io.quarkus.qute.Template;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class DocumentationTemplate {
    String docName;

    public DocumentationTemplate(String docName) {
        this.docName = docName;
    }

    public String getDocName() {
        return docName;
    }

    public String getDocPath() {
        return "/templates/DocumentationResource/documentation/" + getDocName() + "/index.html";
    }

    public RawString getDocRaw() {
        try (InputStream inputStream = DocumentationTemplate.class.getResourceAsStream(getDocPath());
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String contents = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            return new RawString(contents);
        }catch (Exception e) {
            throw new RuntimeException("Cannot find Document", e);
        }
    }

}
