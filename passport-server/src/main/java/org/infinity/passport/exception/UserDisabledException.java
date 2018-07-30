package org.infinity.passport.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * This exception is throw in case of a not disabled user trying to authenticate.
 */
public class UserDisabledException extends AuthenticationException {
    private static final long serialVersionUID = 9214829307866439540L;

    public UserDisabledException(String message) {
        super(message);
    }

    public UserDisabledException(String message, Throwable t) {
        super(message, t);
    }
}
