package org.infinity.passport.domain;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.infinity.passport.dto.AppAuthorityDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Spring Data MongoDB collection for the AppAuthority entity.
 */
@Document
public class AppAuthority implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String            id;

    @Size(min = 0, max = 50)
    private String            appName;

    @Size(min = 0, max = 50)
    private String            authorityName;

    public AppAuthority() {
        super();
    }

    public AppAuthority(String appName, String authorityName) {
        this.appName = appName;
        this.authorityName = authorityName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
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
        AppAuthority other = (AppAuthority) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getAppName() == null ? other.getAppName() == null
                        : this.getAppName().equals(other.getAppName()))
                && (this.getAuthorityName() == null ? other.getAuthorityName() == null
                        : this.getAuthorityName().equals(other.getAuthorityName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAppName() == null) ? 0 : getAppName().hashCode());
        result = prime * result + ((getAuthorityName() == null) ? 0 : getAuthorityName().hashCode());
        return result;
    }

    public AppAuthorityDTO asDTO() {
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
