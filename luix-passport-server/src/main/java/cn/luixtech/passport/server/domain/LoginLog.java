package cn.luixtech.passport.server.domain;

import com.luixtech.springbootframework.idgenerator.TsidGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class LoginLog {

  @Id
  @GeneratedValue
  @TsidGenerator
  @Column(length = 18)
  private String id;

  @Column(nullable = false)
  private String username;

  private String email;

  private String ip;

  private String location;

  private String device;

  private String browser;

  private String os;

  @Column(nullable = false)
  private String status; // success, failed

  @Column(nullable = false, updatable = false)
  private Instant loginAt;

  @PrePersist
  protected void onCreate() {
    if (loginAt == null) {
      loginAt = Instant.now();
    }
  }
}
