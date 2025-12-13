package cn.luixtech.passport.server.domain;

import com.luixtech.springbootframework.idgenerator.TsidGenerator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import jakarta.persistence.Transient;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TeamInvitation {
  @Id
  @GeneratedValue
  @TsidGenerator
  @Column(length = 18)
  private String id;

  @NotNull
  @Column(nullable = false, length = 18)
  private String teamId;

  @NotNull
  @Column(nullable = false)
  private String email;

  @NotNull
  @Column(nullable = false, length = 20)
  private String role;

  @NotNull
  @Column(nullable = false, length = 50)
  private String inviterId;

  @NotNull
  @Column(nullable = false, length = 20)
  private String status;

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant expiresAt;

  @Transient
  private Boolean registered;

  @Column
  private Instant registeredAt;
}
