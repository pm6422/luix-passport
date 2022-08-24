package com.luixtech.passport.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NoAuthorityException extends RuntimeException {

    private static final long   serialVersionUID = 3389857462571862367L;
    private final        String username;

    public NoAuthorityException(String username) {
        super("No authority!");
        this.username = username;
    }
}
