package net.adoptium.model;

import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class DocumentationTemplate {
    String docName;

    public DocumentationTemplate(String docName) {
        this.docName = docName;
    }

    public String getDocName() {
        return docName;
    }

    public String getDocPath() {
        return "DocumentationResource/documentation/testdoc1/index";
    }

    public Template getDoc() {
        Engine engine = Engine.builder().addDefaultValueResolvers().addDefaultSectionHelpers().build();
        System.out.println(engine.parse(getDocPath()));
        engine.putTemplate("my", engine.parse(getDocPath()));
        return engine.getTemplate("my");
    }

}
