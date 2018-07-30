package org.infinity.passport.domain;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.infinity.passport.dto.AuthorityDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Spring Data MongoDB collection for the Authority entity.
 */
@Document
public class Authority implements Serializable {

    private static final long  serialVersionUID       = 1L;

    public static final String ANONYMOUS              = "ROLE_ANONYMOUS";

    public static final String ADMIN                  = "ROLE_ADMIN";

    public static final String USER                   = "ROLE_USER";

    public static final String DEVELOPER              = "ROLE_DEVELOPER";

    public static final String ROLE_SAMPLE_SOUND_USER = "ROLE_SAMPLE_SOUND_USER";

    public static final String SYSTEM_ACCOUNT         = "system";

    @Id
    @Size(min = 0, max = 50)
    private String             name;

    private Boolean            systemLevel;

    private Boolean            enabled;

    public Authority(String name, Boolean systemLevel, Boolean enabled) {
        super();
        this.name = name;
        this.systemLevel = systemLevel;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSystemLevel() {
        return systemLevel;
    }

    public void setSystemLevel(Boolean systemAuthority) {
        this.systemLevel = systemAuthority;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Authority other = (Authority) that;
        return (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getSystemLevel() == null ? other.getSystemLevel() == null
                        : this.getSystemLevel().equals(other.getSystemLevel()))
                && (this.getEnabled() == null ? other.getEnabled() == null
                        : this.getEnabled().equals(other.getEnabled()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getSystemLevel() == null) ? 0 : getSystemLevel().hashCode());
        result = prime * result + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
        return result;
    }

    public AuthorityDTO asDTO() {
        return new AuthorityDTO(this.getName(), this.getSystemLevel(), this.getEnabled());
    }

    public static Authority of(AuthorityDTO dto) {
        Authority target = new Authority(dto.getName(), dto.getSystemLevel(), dto.getEnabled());
        return target;
    }
}
