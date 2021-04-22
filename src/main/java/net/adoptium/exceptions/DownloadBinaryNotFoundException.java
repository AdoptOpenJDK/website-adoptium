package net.adoptium.exceptions;

import javax.ws.rs.WebApplicationException;

public class DownloadBinaryNotFoundException extends WebApplicationException {

    final String msg;
    final String suggestion;

    public DownloadBinaryNotFoundException(String msg, String suggestion) {
        this.msg = msg;
        this.suggestion = suggestion;
    }

    public String getMessage() {
        return this.msg;
    }

    public String getSuggestion() {
        return this.msg;
    }

}