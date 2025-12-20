package cn.luixtech.passport.server.domain;

import com.luixtech.springbootframework.idgenerator.TsidGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SecurityAuditLog {

  @Id
  @GeneratedValue
  @TsidGenerator
  @Column(length = 18)
  private String id;

  @Column(nullable = false)
  private String principal;

  @Column(nullable = false)
  private String action;

  @Column(nullable = false)
  private String severity; // INFO, WARN, CRITICAL

  private String ip;

  private String location;

  private String userAgent;

  @Column(columnDefinition = "TEXT")
  private String details;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
