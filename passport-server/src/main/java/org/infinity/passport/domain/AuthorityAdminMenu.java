package org.infinity.passport.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Spring Data MongoDB collection for the AuthorityAdminMenu entity.
 */
@Document
public class AuthorityAdminMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String            id;

    @NotNull
    @Size(min = 1, max = 20)
    private String            authorityName;

    @NotNull
    private String            adminMenuId;

    public AuthorityAdminMenu() {
        super();
    }

    /**
     * Constructor for creating operation
     * @param authorityName
     * @param adminMenuId
     * @param modifiedTime
     */
    public AuthorityAdminMenu(String authorityName, String adminMenuId) {
        super();
        this.authorityName = authorityName;
        this.adminMenuId = adminMenuId;
    }

    public static AuthorityAdminMenu of(String authorityName, String adminMenuId) {
        return new AuthorityAdminMenu(authorityName, adminMenuId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public String getAdminMenuId() {
        return adminMenuId;
    }

    public void setAdminMenuId(String adminMenuId) {
        this.adminMenuId = adminMenuId;
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
        AuthorityAdminMenu other = (AuthorityAdminMenu) that;
        return (this.getAuthorityName() == null ? other.getAuthorityName() == null
                : this.getAuthorityName().equals(other.getAuthorityName()))
                && (this.getAdminMenuId() == null ? other.getAdminMenuId() == null
                        : this.getAdminMenuId().equals(other.getAdminMenuId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAuthorityName() == null) ? 0 : getAuthorityName().hashCode());
        result = prime * result + ((getAdminMenuId() == null) ? 0 : getAdminMenuId().hashCode());
        return result;
    }
}