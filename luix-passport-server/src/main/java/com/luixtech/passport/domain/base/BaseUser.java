package com.luixtech.passport.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.luixtech.passport.domain.UserAuthority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@MappedSuperclass
public class BaseUser extends AbstractAuditableDomain implements Serializable {
    private static final long   serialVersionUID = -8677592622292657950L;
    public static final  String FIELD_USER_NAME  = "username";
    public static final  String FIELD_EMAIL      = "email";
    public static final  String FIELD_MOBILE_NO  = "mobileNo";

    @Schema(description = "username", required = true)
    @NotNull
    @Pattern(regexp = "^[a-z0-9-]+$", message = "{EP5901}")
    @Size(min = 3, max = 20)
    @Column(unique = true, length = 20, nullable = false)
    private String username;

    @Schema(description = "email", required = true)
    @NotNull
    @Email
    @Size(min = 3, max = 30)
    @Column(unique = true, length = 30, nullable = false)
    private String email;

    @Schema(description = "mobile number", required = true)
    @NotNull
    @Pattern(regexp = "^(13[0-9]|15[012356789]|17[03678]|18[0-9]|14[57])[0-9]{8}$", message = "{EP5951}")
    @Size(min = 11, max = 13)
    @Column(unique = true, length = 13, nullable = false)
    private String mobileNo;

    @Schema(description = "first name")
    @NotNull
    @Size(min = 1, max = 20)
    @Column(length = 20)
    private String firstName;

    @Schema(description = "last name")
    @NotNull
    @Size(min = 1, max = 20)
    @Column(length = 20)
    private String lastName;

    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    @Schema(description = "activation key")
    @JsonIgnore
    private String activationKey;

    @Schema(description = "reset key")
    @JsonIgnore
    private String resetKey;

    @Schema(description = "reset time")
    private Instant resetTime;

    @Schema(description = "profile photo enabled")
    private Boolean profilePhotoEnabled;

    @Schema(description = "remarks")
    private String remarks;

    @Schema(description = "activated")
    private Boolean activated;

    @Schema(description = "enabled")
    private Boolean enabled;

    @Schema(description = "authorities")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false)
    @ToString.Exclude
    private Set<UserAuthority> authorities = new HashSet<>();

    public void setAuthorities(Set<UserAuthority> authorities) {
        if (this.authorities == null) {
            this.authorities = authorities;
        } else {
            this.authorities.clear();
            this.authorities.addAll(authorities);
        }
    }
}
