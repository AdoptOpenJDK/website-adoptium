package net.adoptium.exceptions;

import javax.ws.rs.WebApplicationException;

public class DocumentNotFoundException extends WebApplicationException {

    public DocumentNotFoundException(String message) {
        super(message);
    }
}
