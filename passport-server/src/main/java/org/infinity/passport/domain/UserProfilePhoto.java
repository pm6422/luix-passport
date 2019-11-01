package org.infinity.passport.domain;

import org.bson.types.Binary;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * Spring Data MongoDB collection for the UserProfilePhoto entity.
 */
@Document(collection = "UserProfilePhoto")
public class UserProfilePhoto extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = -8375847941374800940L;

    @Id
    private String id;

    @NotNull
    @Size(min = 1, max = 50)
    @Indexed
    private String userName;

    private Binary profilePhoto;

    public UserProfilePhoto(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Binary getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(Binary profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfilePhoto that = (UserProfilePhoto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(profilePhoto, that.profilePhoto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, profilePhoto);
    }

    @Override
    public String toString() {
        return "UserProfilePhoto{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", profilePhoto=" + profilePhoto +
                ", createdBy='" + getCreatedBy() + '\'' +
                ", createdTime=" + getCreatedTime() +
                ", modifiedBy='" + getModifiedBy() + '\'' +
                ", modifiedTime=" + getModifiedTime() +
                '}';
    }
}
