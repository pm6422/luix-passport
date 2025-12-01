package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.utils.AuthUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthEvent {

    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;
    public static final String AUTH_SUCCESS = "AuthenticationSuccess";
    public static final String AUTH_FAILURE = "AuthenticationFailure";

    private String username;
    private String event;
    private String remark;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(updatable = false)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        createdBy = AuthUtils.getCurrentUsername();
    }
}
