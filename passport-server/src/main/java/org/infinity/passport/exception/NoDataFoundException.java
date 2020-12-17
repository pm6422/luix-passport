package org.infinity.passport.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NoDataFoundException extends RuntimeException {

    private static final long serialVersionUID = 3389857462571862367L;

    private final String id;

    public NoDataFoundException(String id) {
        super("No data found!");
        this.id = id;
    }
}