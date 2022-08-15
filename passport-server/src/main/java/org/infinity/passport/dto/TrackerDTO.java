package org.infinity.passport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

/**
 * DTO for storing a user's activity.
 */
@Schema(description = "user trace DTO")
@Data
public class TrackerDTO {

    private String sessionId;

    private String userLogin;

    private String ipAddress;

    private String page;

    private Instant time;

}
