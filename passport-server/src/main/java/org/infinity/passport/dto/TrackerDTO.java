package org.infinity.passport.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * DTO for storing a user's activity.
 */
@Data
@EqualsAndHashCode
public class TrackerDTO {

    private String sessionId;

    private String userLogin;

    private String ipAddress;

    private String page;

    private Instant time;

}
