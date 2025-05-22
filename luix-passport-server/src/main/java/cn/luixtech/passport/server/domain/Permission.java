package cn.luixtech.passport.server.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    @Id
    protected String id;
    private   String description;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "resourceType:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String resourceType;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "action:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String action;

    @Schema(description = "created time")
    @Column(updatable = false)
    private Instant createdAt;
    @Schema(description = "last modified time")
    private Instant modifiedAt;
}
