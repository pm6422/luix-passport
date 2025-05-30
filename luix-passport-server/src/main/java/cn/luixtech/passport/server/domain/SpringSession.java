package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.listener.AuditableEntityListener;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@EntityListeners(AuditableEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpringSession implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    @Id
    protected String primaryId;
    private   Long   creationTime;
    private   Long   lastAccessTime;
    private   String principalName;
}
