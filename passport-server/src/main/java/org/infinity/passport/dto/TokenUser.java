package org.infinity.passport.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class TokenUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;

    private String firstName;

    private String lastName;

    private String avatarImageUrl;

    private Boolean enabled;

    private Set<String> authorities;

}
