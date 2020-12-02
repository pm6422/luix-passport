package org.infinity.passport.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.infinity.passport.dto.AppAuthorityDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the AppAuthority entity.
 */
@Document(collection = "AppAuthority")
@Data
@EqualsAndHashCode
@NoArgsConstructor
public class AppAuthority implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Size(max = 50)
    private String appName;

    @Size(max = 50)
    private String authorityName;

    public AppAuthority(String appName, String authorityName) {
        this.appName = appName;
        this.authorityName = authorityName;
    }

    public AppAuthorityDTO toDTO() {
        AppAuthorityDTO dest = new AppAuthorityDTO();
        BeanUtils.copyProperties(this, dest);
        return dest;
    }

    public static AppAuthority of(AppAuthorityDTO dto) {
        AppAuthority dest = new AppAuthority();
        BeanUtils.copyProperties(dto, dest);
        return dest;
    }
}
