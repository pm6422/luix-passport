/*
 * This file is generated by jOOQ.
 */
package cn.luixtech.passport.server.persistence.tables.records;


import cn.luixtech.passport.server.persistence.tables.Oauth2Authorization;

import java.time.Instant;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Oauth2AuthorizationRecord extends UpdatableRecordImpl<Oauth2AuthorizationRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.oauth2_authorization.id</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.oauth2_authorization.registered_client_id</code>.
     */
    public void setRegisteredClientId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.registered_client_id</code>.
     */
    public String getRegisteredClientId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.oauth2_authorization.principal_name</code>.
     */
    public void setPrincipalName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.principal_name</code>.
     */
    public String getPrincipalName() {
        return (String) get(2);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.authorization_grant_type</code>.
     */
    public void setAuthorizationGrantType(String value) {
        set(3, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.authorization_grant_type</code>.
     */
    public String getAuthorizationGrantType() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.oauth2_authorization.authorized_scopes</code>.
     */
    public void setAuthorizedScopes(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.authorized_scopes</code>.
     */
    public String getAuthorizedScopes() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.oauth2_authorization.attributes</code>.
     */
    public void setAttributes(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.attributes</code>.
     */
    public String getAttributes() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.oauth2_authorization.state</code>.
     */
    public void setState(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.state</code>.
     */
    public String getState() {
        return (String) get(6);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.authorization_code_value</code>.
     */
    public void setAuthorizationCodeValue(String value) {
        set(7, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.authorization_code_value</code>.
     */
    public String getAuthorizationCodeValue() {
        return (String) get(7);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.authorization_code_issued_at</code>.
     */
    public void setAuthorizationCodeIssuedAt(Instant value) {
        set(8, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.authorization_code_issued_at</code>.
     */
    public Instant getAuthorizationCodeIssuedAt() {
        return (Instant) get(8);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.authorization_code_expires_at</code>.
     */
    public void setAuthorizationCodeExpiresAt(Instant value) {
        set(9, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.authorization_code_expires_at</code>.
     */
    public Instant getAuthorizationCodeExpiresAt() {
        return (Instant) get(9);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.authorization_code_metadata</code>.
     */
    public void setAuthorizationCodeMetadata(String value) {
        set(10, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.authorization_code_metadata</code>.
     */
    public String getAuthorizationCodeMetadata() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.oauth2_authorization.access_token_value</code>.
     */
    public void setAccessTokenValue(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.access_token_value</code>.
     */
    public String getAccessTokenValue() {
        return (String) get(11);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.access_token_issued_at</code>.
     */
    public void setAccessTokenIssuedAt(Instant value) {
        set(12, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.access_token_issued_at</code>.
     */
    public Instant getAccessTokenIssuedAt() {
        return (Instant) get(12);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.access_token_expires_at</code>.
     */
    public void setAccessTokenExpiresAt(Instant value) {
        set(13, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.access_token_expires_at</code>.
     */
    public Instant getAccessTokenExpiresAt() {
        return (Instant) get(13);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.access_token_metadata</code>.
     */
    public void setAccessTokenMetadata(String value) {
        set(14, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.access_token_metadata</code>.
     */
    public String getAccessTokenMetadata() {
        return (String) get(14);
    }

    /**
     * Setter for <code>public.oauth2_authorization.access_token_type</code>.
     */
    public void setAccessTokenType(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.access_token_type</code>.
     */
    public String getAccessTokenType() {
        return (String) get(15);
    }

    /**
     * Setter for <code>public.oauth2_authorization.access_token_scopes</code>.
     */
    public void setAccessTokenScopes(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.access_token_scopes</code>.
     */
    public String getAccessTokenScopes() {
        return (String) get(16);
    }

    /**
     * Setter for <code>public.oauth2_authorization.oidc_id_token_value</code>.
     */
    public void setOidcIdTokenValue(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.oidc_id_token_value</code>.
     */
    public String getOidcIdTokenValue() {
        return (String) get(17);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.oidc_id_token_issued_at</code>.
     */
    public void setOidcIdTokenIssuedAt(Instant value) {
        set(18, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.oidc_id_token_issued_at</code>.
     */
    public Instant getOidcIdTokenIssuedAt() {
        return (Instant) get(18);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.oidc_id_token_expires_at</code>.
     */
    public void setOidcIdTokenExpiresAt(Instant value) {
        set(19, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.oidc_id_token_expires_at</code>.
     */
    public Instant getOidcIdTokenExpiresAt() {
        return (Instant) get(19);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.oidc_id_token_metadata</code>.
     */
    public void setOidcIdTokenMetadata(String value) {
        set(20, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.oidc_id_token_metadata</code>.
     */
    public String getOidcIdTokenMetadata() {
        return (String) get(20);
    }

    /**
     * Setter for <code>public.oauth2_authorization.refresh_token_value</code>.
     */
    public void setRefreshTokenValue(String value) {
        set(21, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.refresh_token_value</code>.
     */
    public String getRefreshTokenValue() {
        return (String) get(21);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.refresh_token_issued_at</code>.
     */
    public void setRefreshTokenIssuedAt(Instant value) {
        set(22, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.refresh_token_issued_at</code>.
     */
    public Instant getRefreshTokenIssuedAt() {
        return (Instant) get(22);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.refresh_token_expires_at</code>.
     */
    public void setRefreshTokenExpiresAt(Instant value) {
        set(23, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.refresh_token_expires_at</code>.
     */
    public Instant getRefreshTokenExpiresAt() {
        return (Instant) get(23);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.refresh_token_metadata</code>.
     */
    public void setRefreshTokenMetadata(String value) {
        set(24, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.refresh_token_metadata</code>.
     */
    public String getRefreshTokenMetadata() {
        return (String) get(24);
    }

    /**
     * Setter for <code>public.oauth2_authorization.user_code_value</code>.
     */
    public void setUserCodeValue(String value) {
        set(25, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.user_code_value</code>.
     */
    public String getUserCodeValue() {
        return (String) get(25);
    }

    /**
     * Setter for <code>public.oauth2_authorization.user_code_issued_at</code>.
     */
    public void setUserCodeIssuedAt(Instant value) {
        set(26, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.user_code_issued_at</code>.
     */
    public Instant getUserCodeIssuedAt() {
        return (Instant) get(26);
    }

    /**
     * Setter for <code>public.oauth2_authorization.user_code_expires_at</code>.
     */
    public void setUserCodeExpiresAt(Instant value) {
        set(27, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.user_code_expires_at</code>.
     */
    public Instant getUserCodeExpiresAt() {
        return (Instant) get(27);
    }

    /**
     * Setter for <code>public.oauth2_authorization.user_code_metadata</code>.
     */
    public void setUserCodeMetadata(String value) {
        set(28, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.user_code_metadata</code>.
     */
    public String getUserCodeMetadata() {
        return (String) get(28);
    }

    /**
     * Setter for <code>public.oauth2_authorization.device_code_value</code>.
     */
    public void setDeviceCodeValue(String value) {
        set(29, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.device_code_value</code>.
     */
    public String getDeviceCodeValue() {
        return (String) get(29);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.device_code_issued_at</code>.
     */
    public void setDeviceCodeIssuedAt(Instant value) {
        set(30, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.device_code_issued_at</code>.
     */
    public Instant getDeviceCodeIssuedAt() {
        return (Instant) get(30);
    }

    /**
     * Setter for
     * <code>public.oauth2_authorization.device_code_expires_at</code>.
     */
    public void setDeviceCodeExpiresAt(Instant value) {
        set(31, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_authorization.device_code_expires_at</code>.
     */
    public Instant getDeviceCodeExpiresAt() {
        return (Instant) get(31);
    }

    /**
     * Setter for <code>public.oauth2_authorization.device_code_metadata</code>.
     */
    public void setDeviceCodeMetadata(String value) {
        set(32, value);
    }

    /**
     * Getter for <code>public.oauth2_authorization.device_code_metadata</code>.
     */
    public String getDeviceCodeMetadata() {
        return (String) get(32);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached Oauth2AuthorizationRecord
     */
    public Oauth2AuthorizationRecord() {
        super(Oauth2Authorization.OAUTH2_AUTHORIZATION);
    }

    /**
     * Create a detached, initialised Oauth2AuthorizationRecord
     */
    public Oauth2AuthorizationRecord(String id, String registeredClientId, String principalName, String authorizationGrantType, String authorizedScopes, String attributes, String state, String authorizationCodeValue, Instant authorizationCodeIssuedAt, Instant authorizationCodeExpiresAt, String authorizationCodeMetadata, String accessTokenValue, Instant accessTokenIssuedAt, Instant accessTokenExpiresAt, String accessTokenMetadata, String accessTokenType, String accessTokenScopes, String oidcIdTokenValue, Instant oidcIdTokenIssuedAt, Instant oidcIdTokenExpiresAt, String oidcIdTokenMetadata, String refreshTokenValue, Instant refreshTokenIssuedAt, Instant refreshTokenExpiresAt, String refreshTokenMetadata, String userCodeValue, Instant userCodeIssuedAt, Instant userCodeExpiresAt, String userCodeMetadata, String deviceCodeValue, Instant deviceCodeIssuedAt, Instant deviceCodeExpiresAt, String deviceCodeMetadata) {
        super(Oauth2Authorization.OAUTH2_AUTHORIZATION);

        setId(id);
        setRegisteredClientId(registeredClientId);
        setPrincipalName(principalName);
        setAuthorizationGrantType(authorizationGrantType);
        setAuthorizedScopes(authorizedScopes);
        setAttributes(attributes);
        setState(state);
        setAuthorizationCodeValue(authorizationCodeValue);
        setAuthorizationCodeIssuedAt(authorizationCodeIssuedAt);
        setAuthorizationCodeExpiresAt(authorizationCodeExpiresAt);
        setAuthorizationCodeMetadata(authorizationCodeMetadata);
        setAccessTokenValue(accessTokenValue);
        setAccessTokenIssuedAt(accessTokenIssuedAt);
        setAccessTokenExpiresAt(accessTokenExpiresAt);
        setAccessTokenMetadata(accessTokenMetadata);
        setAccessTokenType(accessTokenType);
        setAccessTokenScopes(accessTokenScopes);
        setOidcIdTokenValue(oidcIdTokenValue);
        setOidcIdTokenIssuedAt(oidcIdTokenIssuedAt);
        setOidcIdTokenExpiresAt(oidcIdTokenExpiresAt);
        setOidcIdTokenMetadata(oidcIdTokenMetadata);
        setRefreshTokenValue(refreshTokenValue);
        setRefreshTokenIssuedAt(refreshTokenIssuedAt);
        setRefreshTokenExpiresAt(refreshTokenExpiresAt);
        setRefreshTokenMetadata(refreshTokenMetadata);
        setUserCodeValue(userCodeValue);
        setUserCodeIssuedAt(userCodeIssuedAt);
        setUserCodeExpiresAt(userCodeExpiresAt);
        setUserCodeMetadata(userCodeMetadata);
        setDeviceCodeValue(deviceCodeValue);
        setDeviceCodeIssuedAt(deviceCodeIssuedAt);
        setDeviceCodeExpiresAt(deviceCodeExpiresAt);
        setDeviceCodeMetadata(deviceCodeMetadata);
        resetChangedOnNotNull();
    }
}
