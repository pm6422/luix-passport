package com.luixtech.passport.server.domain;

import com.luixtech.springbootframework.idgenerator.TsidGenerator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TeamInvitation {
  /**
   * Invitation expires in 7 days.
   */
  public static final int INVITATION_EXPIRATION_DAYS = 7;

  public static final String STATUS_UNREGISTERED = "UNREGISTERED";
  public static final String STATUS_REGISTERED = "REGISTERED";
  public static final String STATUS_ACCEPTED = "ACCEPTED";
  public static final String STATUS_REJECTED = "REJECTED";
  public static final String STATUS_EXPIRED = "EXPIRED";

  @Id
  @GeneratedValue
  @TsidGenerator
  @Column(length = 18)
  private String id;

  @NotNull
  @Column(nullable = false, length = 18)
  private String teamId;

  @NotNull
  @Column(nullable = false, length = 50)
  private String inviterId;
  
  @NotNull
  @Column(nullable = false)
  private String inviteeEmail;

  @NotNull
  @Column(nullable = false, length = 1000)
  private String inviteeRoles;

  @NotNull
  @Column(nullable = false, length = 20)
  private String status;

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant expiresAt;

}
