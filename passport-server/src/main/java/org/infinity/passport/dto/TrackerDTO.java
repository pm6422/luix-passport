package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.Instant;

/**
 * DTO for storing a user's activity.
 */
@ApiModel("用户追踪")
@Data
public class TrackerDTO {

    private String sessionId;

    private String userLogin;

    private String ipAddress;

    private String page;

    private Instant time;

}
