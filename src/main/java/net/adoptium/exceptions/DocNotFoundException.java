package net.adoptium.exceptions;

import javax.ws.rs.WebApplicationException;

public class DocNotFoundException extends WebApplicationException {
    final String msg;

    public DocNotFoundException(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return this.msg;
    }
}
