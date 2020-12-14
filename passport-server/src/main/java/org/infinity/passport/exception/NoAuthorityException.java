package org.infinity.passport.exception;

import org.infinity.passport.dto.ParameterizedErrorDTO;

public class NoAuthorityException extends RuntimeException {

    private static final long   serialVersionUID = 3389857462571862367L;
    private final        String userName;
    private              String message;

    public NoAuthorityException(String userName) {
        super();
        this.userName = userName;
    }

    public NoAuthorityException(String userName, String message) {
        super();
        this.userName = userName;
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ParameterizedErrorDTO getErrorDTO() {
        return new ParameterizedErrorDTO(message, userName);
    }
}
