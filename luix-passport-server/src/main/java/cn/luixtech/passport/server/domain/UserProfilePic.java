package cn.luixtech.passport.server.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserProfilePic {

    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "username:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String username;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "bytea", nullable = false)
    private byte[] profilePic;
}
