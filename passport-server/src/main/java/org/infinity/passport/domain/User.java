package org.infinity.passport.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Spring Data MongoDB collection for the User entity.
 */
@Document(collection = "User")
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

    private Boolean hasProfilePhoto;

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
     * @param enabled
     */
    public User(String userName, String firstName, String lastName, String email, String mobileNo, String passwordHash,
                Boolean activated, String activationKey, String resetKey, Instant resetTime, Boolean enabled) {
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

    public Boolean getHasProfilePhoto() {
        return hasProfilePhoto;
    }

    public void setHasProfilePhoto(Boolean hasProfilePhoto) {
        this.hasProfilePhoto = hasProfilePhoto;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(userName, user.userName) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(email, user.email) &&
                Objects.equals(mobileNo, user.mobileNo) &&
                Objects.equals(passwordHash, user.passwordHash) &&
                Objects.equals(activated, user.activated) &&
                Objects.equals(activationKey, user.activationKey) &&
                Objects.equals(resetKey, user.resetKey) &&
                Objects.equals(resetTime, user.resetTime) &&
                Objects.equals(hasProfilePhoto, user.hasProfilePhoto) &&
                Objects.equals(enabled, user.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, firstName, lastName, email, mobileNo, passwordHash, activated, activationKey, resetKey, resetTime, hasProfilePhoto, enabled);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", activated=" + activated +
                ", activationKey='" + activationKey + '\'' +
                ", resetKey='" + resetKey + '\'' +
                ", resetTime=" + resetTime +
                ", hasProfilePhoto=" + hasProfilePhoto +
                ", enabled=" + enabled +
                ", createdBy='" + getCreatedBy() + '\'' +
                ", createdTime=" + getCreatedTime() +
                ", modifiedBy='" + getModifiedBy() + '\'' +
                ", modifiedTime=" + getModifiedTime() +
                '}';
    }
}
