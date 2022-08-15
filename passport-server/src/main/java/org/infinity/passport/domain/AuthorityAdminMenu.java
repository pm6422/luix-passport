package org.infinity.passport.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the AuthorityAdminMenu entity.
 */
@Document
@Data
@NoArgsConstructor
public class AuthorityAdminMenu implements Serializable {
    private static final long   serialVersionUID = 1L;
    @Id
    private              String id;
    @NotNull
    @Indexed
    private              String authorityName;
    @NotNull
    @Indexed
    private              String adminMenuId;

    public AuthorityAdminMenu(String authorityName, String adminMenuId) {
        super();
        this.authorityName = authorityName;
        this.adminMenuId = adminMenuId;
    }

    public static AuthorityAdminMenu of(String authorityName, String adminMenuId) {
        return new AuthorityAdminMenu(authorityName, adminMenuId);
    }
}