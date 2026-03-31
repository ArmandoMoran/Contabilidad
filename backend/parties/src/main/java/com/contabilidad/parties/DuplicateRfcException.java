package com.contabilidad.parties;

public class DuplicateRfcException extends RuntimeException {

    public DuplicateRfcException(String rfc) {
        super("A record with RFC '%s' already exists".formatted(rfc));
    }
}
