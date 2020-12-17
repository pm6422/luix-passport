package org.infinity.passport.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NoAuthorityException extends RuntimeException {

    private static final long   serialVersionUID = 3389857462571862367L;
    private final        String userName;

    public NoAuthorityException(String userName) {
        super("No authority!");
        this.userName = userName;
    }
}
