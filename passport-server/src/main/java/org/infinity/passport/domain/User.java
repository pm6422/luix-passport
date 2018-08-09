package org.infinity.passport.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * Spring Data MongoDB collection for the User entity.
 */
@Document
public class User extends AbstractAuditableDomain implements Serializable {

    private static final long  serialVersionUID = 1L;

    public static final String FIELD_USER_NAME  = "userName";

    public static final String FIELD_EMAIL      = "email";

    public static final String FIELD_MOBILE_NO  = "mobileNo";

    @Id
    private String             id;

    @NotNull
    @Size(min = 1, max = 50)
    @Indexed
    private String             userName;

    @Size(max = 50)
    private String             firstName;

    @Size(max = 50)
    private String             lastName;

    @Email
    @Size(min = 5, max = 100)
    @Indexed
    private String             email;

    @Indexed
    private String             mobileNo;

    @NotNull
    @Size(min = 60, max = 60)
    @JsonIgnore
    private String             passwordHash;

    private Boolean            activated;

    @Size(max = 20)
    @JsonIgnore
    private String             activationKey;

    @Size(max = 20)
    @JsonIgnore
    private String             resetKey;

    private Instant            resetTime;

    @Size(max = 256)
    private String             avatarImageUrl;

    private Boolean            enabled;

    public User() {
        super();
    }

    /**
     * Constructor for creating operation
     * @param userName
     * @param firstName
     * @param lastName
     * @param email
     * @param mobileNo
     * @param passwordHash
     * @param activated
     * @param activationKey
     * @param resetKey
     * @param resetTime
     * @param avatarImageUrl
     * @param enabled
     */
    public User(String userName, String firstName, String lastName, String email, String mobileNo, String passwordHash,
            Boolean activated, String activationKey, String resetKey, Instant resetTime, String avatarImageUrl,
            Boolean enabled) {
        super();
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNo = mobileNo;
        this.passwordHash = passwordHash;
        this.activated = activated;
        this.activationKey = activationKey;
        this.resetKey = resetKey;
        this.resetTime = resetTime;
        this.avatarImageUrl = avatarImageUrl;
        this.enabled = enabled;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public Instant getResetTime() {
        return resetTime;
    }

    public void setResetTime(Instant resetTime) {
        this.resetTime = resetTime;
    }

    public String getAvatarImageUrl() {
        return avatarImageUrl;
    }

    public void setAvatarImageUrl(String avatarImageUrl) {
        this.avatarImageUrl = avatarImageUrl;
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
        User other = (User) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserName() == null ? other.getUserName() == null
                        : this.getUserName().equals(other.getUserName()))
                && (this.getFirstName() == null ? other.getFirstName() == null
                        : this.getFirstName().equals(other.getFirstName()))
                && (this.getLastName() == null ? other.getLastName() == null
                        : this.getLastName().equals(other.getLastName()))
                && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
                && (this.getMobileNo() == null ? other.getMobileNo() == null
                        : this.getMobileNo().equals(other.getMobileNo()))
                && (this.getPasswordHash() == null ? other.getPasswordHash() == null
                        : this.getPasswordHash().equals(other.getPasswordHash()))
                && (this.getActivated() == null ? other.getActivated() == null
                        : this.getActivated().equals(other.getActivated()))
                && (this.getActivationKey() == null ? other.getActivationKey() == null
                        : this.getActivationKey().equals(other.getActivationKey()))
                && (this.getResetKey() == null ? other.getResetKey() == null
                        : this.getResetKey().equals(other.getResetKey()))
                && (this.getResetTime() == null ? other.getResetTime() == null
                        : this.getResetTime().equals(other.getResetTime()))
                && (this.getAvatarImageUrl() == null ? other.getAvatarImageUrl() == null
                        : this.getAvatarImageUrl().equals(other.getAvatarImageUrl()))
                && (this.enabled == null ? other.enabled == null : this.enabled.equals(other.enabled))
                && (this.getCreatedTime() == null ? other.getCreatedTime() == null
                        : this.getCreatedTime().equals(other.getCreatedTime()))
                && (this.getCreatedBy() == null ? other.getCreatedBy() == null
                        : this.getCreatedBy().equals(other.getCreatedBy()))
                && (this.getModifiedTime() == null ? other.getModifiedTime() == null
                        : this.getModifiedTime().equals(other.getModifiedTime()))
                && (this.getModifiedBy() == null ? other.getModifiedBy() == null
                        : this.getModifiedBy().equals(other.getModifiedBy()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getFirstName() == null) ? 0 : getFirstName().hashCode());
        result = prime * result + ((getLastName() == null) ? 0 : getLastName().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getMobileNo() == null) ? 0 : getMobileNo().hashCode());
        result = prime * result + ((getPasswordHash() == null) ? 0 : getPasswordHash().hashCode());
        result = prime * result + ((getActivated() == null) ? 0 : getActivated().hashCode());
        result = prime * result + ((getActivationKey() == null) ? 0 : getActivationKey().hashCode());
        result = prime * result + ((getResetKey() == null) ? 0 : getResetKey().hashCode());
        result = prime * result + ((getResetTime() == null) ? 0 : getResetTime().hashCode());
        result = prime * result + ((getAvatarImageUrl() == null) ? 0 : getAvatarImageUrl().hashCode());
        result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        result = prime * result + ((getCreatedBy() == null) ? 0 : getCreatedBy().hashCode());
        result = prime * result + ((getModifiedTime() == null) ? 0 : getModifiedTime().hashCode());
        result = prime * result + ((getModifiedBy() == null) ? 0 : getModifiedBy().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", userName=" + userName + ", firstName=" + firstName + ", lastName=" + lastName
                + ", email=" + email + ", mobileNo=" + mobileNo + ", passwordHash=" + passwordHash + ", activated="
                + activated + ", activationKey=" + activationKey + ", resetKey=" + resetKey + ", resetTime=" + resetTime
                + ", avatarImageUrl=" + avatarImageUrl + ", enabled=" + enabled + ", createdTime="
                + super.getCreatedTime() + ", createdBy=" + super.getCreatedBy() + ", modifiedTime="
                + super.getModifiedTime() + ", modifiedBy=" + super.getModifiedBy() + "]";
    }
}
