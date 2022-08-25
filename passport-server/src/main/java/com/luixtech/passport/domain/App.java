package com.luixtech.passport.domain;

import com.luixtech.passport.domain.base.AbstractAuditableDomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
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

    @Schema(description = "authorities")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "app_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ToString.Exclude
    private Set<AppAuthority> authorities = new HashSet<>();

    public void setAuthorities(Set<AppAuthority> authorities) {
        if (this.authorities == null) {
            this.authorities = authorities;
        } else {
            this.authorities.clear();
            this.authorities.addAll(authorities);
        }
    }

}
