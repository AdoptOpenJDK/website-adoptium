package net.adoptium.model;

import io.quarkus.qute.Engine;

public class DownloadErrorTemplate {
    private final String errorMessage;
    private final String suggestion;

    public DownloadErrorTemplate(Engine engine, String errorMessageID, String suggestionID) {
        this.errorMessage = getI18N(engine, errorMessageID);
        this.suggestion = getI18N(engine, suggestionID);
    }

    private String getI18N(Engine engine, String id) {
        return engine.parse("{msg:" + id + "}").render();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSuggestion() {
        return suggestion;
    }
}
