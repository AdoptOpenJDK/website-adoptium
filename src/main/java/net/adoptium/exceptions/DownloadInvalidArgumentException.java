package net.adoptium.exceptions;

import javax.ws.rs.WebApplicationException;

public class DownloadInvalidArgumentException extends WebApplicationException {

    final String msg;
    final String suggestion;

    public DownloadInvalidArgumentException(String msg, String suggestion) {
        this.msg = msg;
        this.suggestion = suggestion;
    }

    public String getMessage() {
        return this.msg;
    }

    public String getSuggestion() {
        return this.suggestion;
    }

}