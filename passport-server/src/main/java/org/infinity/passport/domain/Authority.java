package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Authority implements Serializable {
    private static final long   serialVersionUID = 406590631051105066L;
    public static final  String ANONYMOUS        = "ROLE_ANONYMOUS";
    public static final  String USER             = "ROLE_USER";
    public static final  String ADMIN            = "ROLE_ADMIN";
    public static final  String DEVELOPER        = "ROLE_DEVELOPER";
    public static final  String SYSTEM_ACCOUNT   = "system";

    @Schema(required = true)
    @NotNull
    @Size(min = 3, max = 16)
    @Pattern(regexp = "^[A-Z_]+$", message = "{EP5902}")
    @Id
    private String  name;
    private Boolean systemLevel;
    private Boolean enabled;

    public Authority(Boolean enabled) {
        this.enabled = enabled;
    }
}
