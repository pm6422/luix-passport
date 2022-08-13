package org.infinity.passport.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;

@Document
@Data
@NoArgsConstructor
@Deprecated
public class OAuth2Authorization implements Serializable {
    private static final long   serialVersionUID     = 5961084257759790938L;
    public static final  String AUTHORIZATION_BEARER = "Bearer ";
    public static final  String AUTHORIZATION_BASIC  = "Basic ";

    @Id
    private String  id;
    private String  registeredClientId;
    private String  principalName;
    private String  authorizationGrantType;
    private String  attributes;
    private String  state;
    private String  authorizationCodeValue;
    private Instant authorizationCodeIssuedAt;
    private Instant authorizationCodeExpiresAt;
    private String  authorizationCodeMetadata;
    private String  accessTokenValue;
    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;
    private String  accessTokenMetadata;
    private String  accessTokenType;
    private String  accessTokenScopes;
    private String  refreshTokenValue;
    private Instant refreshTokenIssuedAt;
    private Instant refreshTokenExpiresAt;
    private String  refreshTokenMetadata;
    private String  oidcIdTokenValue;
    private Instant oidcIdTokenIssuedAt;
    private Instant oidcIdTokenExpiresAt;
    private String  oidcIdTokenMetadata;
    private String  oidcIdTokenClaims;
}
