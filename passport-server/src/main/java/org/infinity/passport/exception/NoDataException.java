package org.infinity.passport.exception;

import org.infinity.passport.dto.ParameterizedErrorDTO;

public class NoDataException extends RuntimeException {

    private static final long serialVersionUID = 3389857462571862367L;

    private String            id;

    private String            message;

    public NoDataException(String id) {
        super();
        this.id = id;
    }

    public NoDataException(String id, String message) {
        super();
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ParameterizedErrorDTO getErrorDTO() {
        return new ParameterizedErrorDTO(message, id);
    }
}
