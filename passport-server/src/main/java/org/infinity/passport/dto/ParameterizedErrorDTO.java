package org.infinity.passport.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for sending a parameterized error message.
 */
@Data
@NoArgsConstructor
public class ParameterizedErrorDTO implements Serializable {

    private static final long serialVersionUID = -2060518823145626799L;

    private String message;

    private Object[] params;

    public ParameterizedErrorDTO(String message, Object... params) {
        this.message = message;
        this.params = params;
    }
}
