package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.infinity.passport.domain.base.AbstractAuditableDomain;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class App extends AbstractAuditableDomain implements Serializable {
    private static final long serialVersionUID = -9092868503747872611L;

    @Schema(required = true)
    @NotNull
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "{EP5903}")
    private String name;

    @Schema
    private Boolean enabled;

    @Schema
    @Transient
    private Set<String> authorities;

    public App(String name, Boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }
}
