package org.infinity.passport.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class BaseUser extends AbstractAuditableDomain implements Serializable {

    private static final long   serialVersionUID = -8677592622292657950L;
    public static final  String FIELD_USER_NAME  = "username";
    public static final  String FIELD_EMAIL      = "email";
    public static final  String FIELD_MOBILE_NO  = "mobileNo";

    @Schema(description = "用户名", required = true)
    @NotNull
    @Pattern(regexp = "^[a-z0-9-]+$", message = "{EP5901}")
    @Size(min = 3, max = 20)
    @Indexed(unique = true)
    private String username;

    @Schema(description = "名", required = true)
    @NotNull
    @Size(min = 1, max = 20)
    private String firstName;

    @Schema(description = "姓", required = true)
    @NotNull
    @Size(min = 1, max = 20)
    private String lastName;

    @Schema(description = "电子邮件", required = true)
    @NotNull
    @Email
    @Size(min = 3, max = 30)
    @Indexed
    private String email;

    @Schema(description = "手机号", required = true)
    @NotNull
    @Pattern(regexp = "^(13[0-9]|15[012356789]|17[03678]|18[0-9]|14[57])[0-9]{8}$", message = "{EP5951}")
    @Size(min = 11, max = 13)
    @Indexed
    private String mobileNo;

    @JsonIgnore
    private String passwordHash;

    @Schema(description = "是否激活")
    private Boolean activated;

    @Schema(description = "激活密钥")
    @JsonIgnore
    private String activationKey;

    @Schema(description = "重置密钥")
    @JsonIgnore
    private String resetKey;

    @Schema(description = "重置密码时间")
    private Instant resetTime;

    @Schema(description = "是否包含个人头像")
    private Boolean hasProfilePhoto;

    @Schema(description = "是否可用")
    private Boolean enabled;

    @Schema(description = "备注")
    private String remarks;

    @Schema(description = "权限列表")
    @Transient
    private Set<String> authorities;
}
