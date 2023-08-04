package com.luixtech.passport.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for transferring error message with a list of field errors.
 */
@Data
@Builder
public class ErrorDTO implements Serializable {
    private static final long serialVersionUID = 8796503825563140269L;

    private String           code;
    private String           message;
    private List<ErrorField> errorFields;

    @Data
    @Builder
    public static class ErrorField {
        private String field;
        private Object rejectedValue;
        private String message;
    }
}
