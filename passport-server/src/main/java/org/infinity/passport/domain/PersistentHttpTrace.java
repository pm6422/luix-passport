package org.infinity.passport.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Persist HttpTrace managed by the Spring Boot actuator @see org.springframework.boot.actuate.trace.http.HttpTrace
 */
@Document(collection = "PersistentHttpTrace")
@EqualsAndHashCode
@ToString
public class PersistentHttpTrace {

    private Instant timestamp;
    private String  uri;
    private Long    timeTaken;
    private int     status;

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
