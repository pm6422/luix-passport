package org.infinity.passport.controller.advice;

import java.io.Serializable;
import java.util.List;

import org.springframework.validation.FieldError;

/**
 * DTO for transferring error message with a list of field errors.
 */
public class ErrorDTO implements Serializable {
    private static final long serialVersionUID = 8796503825563140269L;

    private final String      code;

    private final String      message;

    private List<FieldError>  fieldErrors;

    public ErrorDTO(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public ErrorDTO(String code, String message, List<FieldError> fieldErrors) {
        super();
        this.code = code;
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
