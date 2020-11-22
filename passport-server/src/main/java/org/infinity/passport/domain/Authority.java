package org.infinity.passport.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.infinity.passport.dto.AuthorityDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the Authority entity.
 */
@Document(collection = "Authority")
@Data
@EqualsAndHashCode
@NoArgsConstructor
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String DEVELOPER = "ROLE_DEVELOPER";

    public static final String ROLE_SAMPLE_SOUND_USER = "ROLE_SAMPLE_SOUND_USER";

    public static final String SYSTEM_ACCOUNT = "system";

    @Id
    @Size(max = 50)
    private String name;

    private Boolean systemLevel;

    private Boolean enabled;

    public Authority(Boolean enabled) {
        this.enabled = enabled;
    }

    public Authority(String name, Boolean systemLevel, Boolean enabled) {
        super();
        this.name = name;
        this.systemLevel = systemLevel;
        this.enabled = enabled;
    }

    public AuthorityDTO asDTO() {
        return new AuthorityDTO(this.getName(), this.getSystemLevel(), this.getEnabled());
    }

    public static Authority of(AuthorityDTO dto) {
        return new Authority(dto.getName(), dto.getSystemLevel(), dto.getEnabled());
    }
}
