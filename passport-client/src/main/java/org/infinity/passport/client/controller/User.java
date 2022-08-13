package org.infinity.passport.client.controller;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
public class User implements Serializable {
    private static final long serialVersionUID = 5015416047116474093L;
    private String username;

    private String mobileNo;

    private Set<String> authorities;
}
