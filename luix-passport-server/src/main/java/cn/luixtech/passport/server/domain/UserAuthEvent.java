package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.utils.AuthUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.luixtech.springbootframework.idgenerator.TsidGenerator;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthEvent {

    @Id
    @GeneratedValue
    @TsidGenerator
    @Column(length = 19)
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
