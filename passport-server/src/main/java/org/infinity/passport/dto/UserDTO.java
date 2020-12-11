package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.User;
import org.infinity.passport.domain.base.AbstractAuditableDomain;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * A DTO representing a user, with his authorities.
 */
@ApiModel("用户DTO")
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class UserDTO extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = -2812197562694629239L;

    private static final String USER_REGEX = "^[_'.@A-Za-z0-9-]*$";

    @ApiModelProperty(value = "用户ID")
    private String id;

    @ApiModelProperty(value = "用户名", required = true)
    @NotNull
    @Pattern(regexp = USER_REGEX)
    @Size(min = 1, max = 50)
    private String userName;

    @ApiModelProperty(value = "名", required = true)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Size(max = 50)
    private String firstName;

    @ApiModelProperty(value = "姓", required = true)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Size(max = 50)
    private String lastName;

    @ApiModelProperty(value = "电子邮件", required = true)
    @NotNull
    @Email(message = "{error.invalid.email}")
    @Size(min = 5, max = 100)
    private String email;

    @ApiModelProperty(value = "是否激活")
    private Boolean activated;

    @ApiModelProperty(value = "手机号", required = true)
    @NotNull
    @Pattern(regexp = "^(13[0-9]|15[012356789]|17[03678]|18[0-9]|14[57])[0-9]{8}$")
    @Size(min = 11, max = 13)
    private String mobileNo;

    @ApiModelProperty(value = "是否包含个人头像")
    private Boolean hasProfilePhoto;

    @ApiModelProperty(value = "是否可用")
    private Boolean enabled;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "权限名称")
    private Set<String> authorities;

    public UserDTO(User user, Set<String> authorities) {
        this(user.getId(), user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getActivated(), user.getMobileNo(), user.getHasProfilePhoto(), user.getEnabled(), user.getRemarks(), authorities);
    }

    public UserDTO(String id, String userName, String firstName, String lastName, String email, Boolean activated,
                   String mobileNo, Boolean hasProfilePhoto, Boolean enabled, String remarks, Set<String> authorities) {
        this.id = id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.activated = activated;
        this.mobileNo = mobileNo;
        this.hasProfilePhoto = hasProfilePhoto;
        this.enabled = enabled;
        this.remarks = remarks;
        this.authorities = authorities;
    }
}
