package cn.luixtech.passport.server.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class SpringSession {

    @Schema(description = "ID")
    @Id
    protected String primaryId;
    private Long creationTime;
    private Long lastAccessTime;
    private String principalName;
}
