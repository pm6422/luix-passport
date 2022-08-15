package org.infinity.passport.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;

/**
 * Persist HttpTrace managed by the Spring Boot actuator @see org.springframework.boot.actuate.trace.http.HttpTrace
 */
@Document
@Data
public class PersistentHttpTrace implements Serializable {
    private static final long    serialVersionUID = 4090431427130666650L;
    private              Instant timestamp;
    private              String  uri;
    private              Long    timeTaken;
    private              int     status;
}
