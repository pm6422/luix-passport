package org.infinity.passport.exception;

import org.springframework.validation.FieldError;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This exception is throw in case of field validation failure.
 */
public class FieldValidationException extends RuntimeException {
    private static final long serialVersionUID = 2608017580562426023L;

    private final List<FieldError> fieldErrors;

    public FieldValidationException(String objectName, String field, String code) {
        this(objectName, field, null, new String[] { code }, new Object[] { null }, null);
    }

    public FieldValidationException(String objectName, String field, Object rejectedValue, String code) {
        this(objectName, field, rejectedValue, new String[] { code }, new Object[] { null }, null);
    }

    public FieldValidationException(String objectName, String field, Object rejectedValue, String code,
            Object argument) {
        this(objectName, field, rejectedValue, new String[] { code }, new Object[] { argument }, null);
    }

    public FieldValidationException(String objectName, String field, Object rejectedValue, String[] codes,
            Object[] arguments, String defaultMessage) {
        super(MessageFormat.format("Invalid {0}", field));
        fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError(objectName, field, rejectedValue, true, codes, arguments,
                defaultMessage);
        fieldErrors.add(fieldError);
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

}
