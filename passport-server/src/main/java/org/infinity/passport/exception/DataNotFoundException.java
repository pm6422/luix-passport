package org.infinity.passport.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DataNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 3389857462571862367L;

    private final String id;

    public DataNotFoundException(String id) {
        super("Data not found!");
        this.id = id;
    }
}