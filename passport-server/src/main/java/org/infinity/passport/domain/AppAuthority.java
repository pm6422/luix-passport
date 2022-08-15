package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the AppAuthority entity.
 */
@Document
@Data
@NoArgsConstructor
public class AppAuthority implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Schema(required = true)
    @NotNull
    @Size(min = 2, max = 20)
    @Indexed
    private String appName;

    @Schema(required = true)
    @NotNull
    @Size(min = 2, max = 20)
    @Indexed
    private String authorityName;

    public AppAuthority(String appName, String authorityName) {
        this.appName = appName;
        this.authorityName = authorityName;
    }
}
