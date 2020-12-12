package org.infinity.passport.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * Spring Data MongoDB collection for the User entity.
 */
@Document(collection = "User")
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class User extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String FIELD_USER_NAME = "userName";

    public static final String FIELD_EMAIL = "email";

    public static final String FIELD_MOBILE_NO = "mobileNo";

    @NotNull
    @Size(min = 1, max = 50)
    @Indexed
    private String userName;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 100)
    @Indexed
    private String email;

    @Indexed
    private String mobileNo;

    @NotNull
    @Size(min = 60, max = 60)
    @JsonIgnore
    private String passwordHash;

    private Boolean activated;

    @Size(max = 20)
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @JsonIgnore
    private String resetKey;

    private Instant resetTime;

    private Boolean hasProfilePhoto;

    private Boolean enabled;

    private String remarks;

    public User(String userName, String firstName, String lastName, String email, String mobileNo, String passwordHash,
                Boolean activated, String activationKey, String resetKey, Instant resetTime, Boolean enabled, String remarks) {
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
        this.remarks = remarks;
    }
}
