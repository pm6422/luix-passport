package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
public class UserDTO extends AbstractAuditableDomain implements Serializable {

    private static final long   serialVersionUID = -2812197562694629239L;

    private static final String USER_REGEX       = "^[_'.@A-Za-z0-9-]*$";

    @ApiModelProperty(value = "用户ID")
    private String              id;

    @ApiModelProperty(value = "用户名", required = true)
    @NotNull
    @Pattern(regexp = USER_REGEX)
    @Size(min = 1, max = 50)
    private String              userName;

    @ApiModelProperty(value = "名", required = true)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Size(max = 50)
    private String              firstName;

    @ApiModelProperty(value = "姓", required = true)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Size(max = 50)
    private String              lastName;

    @ApiModelProperty(value = "电子邮件", required = true)
    @NotNull
    @Email(message = "{error.invalid.email}")
    @Size(min = 5, max = 100)
    private String              email;

    @ApiModelProperty(value = "是否激活")
    private Boolean             activated        = false;

    @ApiModelProperty(value = "手机号", required = true)
    @NotNull
    @Pattern(regexp = "^(13[0-9]|15[012356789]|17[03678]|18[0-9]|14[57])[0-9]{8}$")
    @Size(min = 11, max = 13)
    private String              mobileNo;

    private Boolean hasProfilePhoto;

    @ApiModelProperty(value = "是否可用")
    private Boolean             enabled          = false;

    @ApiModelProperty(value = "权限名称")
    private Set<String>         authorities;

    public UserDTO() {
    }

    public UserDTO(User user, Set<String> authorities) {
        this(user.getId(), user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getActivated(), user.getMobileNo(), user.getHasProfilePhoto(), user.getEnabled(), authorities);
    }

    public UserDTO(String id, String userName, String firstName, String lastName, String email, Boolean activated,
                   String mobileNo, Boolean hasProfilePhoto, Boolean enabled, Set<String> authorities) {
        this.id = id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.activated = activated;
        this.mobileNo = mobileNo;
        this.hasProfilePhoto = hasProfilePhoto;
        this.enabled = enabled;
        this.authorities = authorities;
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

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
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

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", activated=" + activated +
                ", mobileNo='" + mobileNo + '\'' +
                ", hasProfilePhoto='" + hasProfilePhoto + '\'' +
                ", enabled=" + enabled +
                ", authorities=" + authorities +
                '}';
    }
}
