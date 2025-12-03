package cn.luixtech.passport.server.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserProfilePic {

    @Id
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "id:{Validation.NotEmpty}")
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "bytea", nullable = false)
    private byte[] profilePic;
}
