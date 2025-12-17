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
public class Role {
    public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_DEVELOPER = "ROLE_DEVELOPER";
    public static final String ROLE_TEAM_OWNER = "ROLE_TEAM_OWNER";
    public static final String ROLE_TEAM_MEMBER = "ROLE_TEAM_MEMBER";
    public static final String ROLE_MEMBER_PRO = "ROLE_MEMBER_PRO";
    public static final String ROLE_MEMBER_PREMIUM = "ROLE_MEMBER_PREMIUM";

    @Id
    @GeneratedValue
    @TsidGenerator
    @Column(length = 18)
    private String id;

    private String remark;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(updatable = false)
    private String createdBy;

    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        createdBy = AuthUtils.getCurrentUsername();
        updatedBy = createdBy;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        updatedBy = AuthUtils.getCurrentUsername();
    }
}
