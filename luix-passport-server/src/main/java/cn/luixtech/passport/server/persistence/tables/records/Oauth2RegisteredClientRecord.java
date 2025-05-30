/*
 * This file is generated by jOOQ.
 */
package cn.luixtech.passport.server.persistence.tables.records;


import cn.luixtech.passport.server.persistence.tables.Oauth2RegisteredClient;

import java.time.Instant;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record19;
import org.jooq.Row19;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Oauth2RegisteredClientRecord extends UpdatableRecordImpl<Oauth2RegisteredClientRecord> implements Record19<String, String, Instant, String, Instant, String, String, String, String, String, String, String, String, byte[], Boolean, Instant, String, Instant, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.oauth2_registered_client.id</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.oauth2_registered_client.client_id</code>.
     */
    public void setClientId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.client_id</code>.
     */
    public String getClientId() {
        return (String) get(1);
    }

    /**
     * Setter for
     * <code>public.oauth2_registered_client.client_id_issued_at</code>.
     */
    public void setClientIdIssuedAt(Instant value) {
        set(2, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_registered_client.client_id_issued_at</code>.
     */
    public Instant getClientIdIssuedAt() {
        return (Instant) get(2);
    }

    /**
     * Setter for <code>public.oauth2_registered_client.client_secret</code>.
     */
    public void setClientSecret(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.client_secret</code>.
     */
    public String getClientSecret() {
        return (String) get(3);
    }

    /**
     * Setter for
     * <code>public.oauth2_registered_client.client_secret_expires_at</code>.
     */
    public void setClientSecretExpiresAt(Instant value) {
        set(4, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_registered_client.client_secret_expires_at</code>.
     */
    public Instant getClientSecretExpiresAt() {
        return (Instant) get(4);
    }

    /**
     * Setter for <code>public.oauth2_registered_client.client_name</code>.
     */
    public void setClientName(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.client_name</code>.
     */
    public String getClientName() {
        return (String) get(5);
    }

    /**
     * Setter for
     * <code>public.oauth2_registered_client.client_authentication_methods</code>.
     */
    public void setClientAuthenticationMethods(String value) {
        set(6, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_registered_client.client_authentication_methods</code>.
     */
    public String getClientAuthenticationMethods() {
        return (String) get(6);
    }

    /**
     * Setter for
     * <code>public.oauth2_registered_client.authorization_grant_types</code>.
     */
    public void setAuthorizationGrantTypes(String value) {
        set(7, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_registered_client.authorization_grant_types</code>.
     */
    public String getAuthorizationGrantTypes() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.oauth2_registered_client.redirect_uris</code>.
     */
    public void setRedirectUris(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.redirect_uris</code>.
     */
    public String getRedirectUris() {
        return (String) get(8);
    }

    /**
     * Setter for
     * <code>public.oauth2_registered_client.post_logout_redirect_uris</code>.
     */
    public void setPostLogoutRedirectUris(String value) {
        set(9, value);
    }

    /**
     * Getter for
     * <code>public.oauth2_registered_client.post_logout_redirect_uris</code>.
     */
    public String getPostLogoutRedirectUris() {
        return (String) get(9);
    }

    /**
     * Setter for <code>public.oauth2_registered_client.scopes</code>.
     */
    public void setScopes(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.scopes</code>.
     */
    public String getScopes() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.oauth2_registered_client.client_settings</code>.
     */
    public void setClientSettings(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.client_settings</code>.
     */
    public String getClientSettings() {
        return (String) get(11);
    }

    /**
     * Setter for <code>public.oauth2_registered_client.token_settings</code>.
     */
    public void setTokenSettings(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.token_settings</code>.
     */
    public String getTokenSettings() {
        return (String) get(12);
    }

    /**
     * Setter for <code>public.oauth2_registered_client.photo</code>.
     */
    public void setPhoto(byte[] value) {
        set(13, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.photo</code>.
     */
    public byte[] getPhoto() {
        return (byte[]) get(13);
    }

    /**
     * Setter for <code>public.oauth2_registered_client.enabled</code>.
     */
    public void setEnabled(Boolean value) {
        set(14, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.enabled</code>.
     */
    public Boolean getEnabled() {
        return (Boolean) get(14);
    }

    /**
     * Setter for <code>public.oauth2_registered_client.created_at</code>.
     */
    public void setCreatedAt(Instant value) {
        set(15, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.created_at</code>.
     */
    public Instant getCreatedAt() {
        return (Instant) get(15);
    }

    /**
     * Setter for <code>public.oauth2_registered_client.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(16);
    }

    /**
     * Setter for <code>public.oauth2_registered_client.modified_at</code>.
     */
    public void setModifiedAt(Instant value) {
        set(17, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.modified_at</code>.
     */
    public Instant getModifiedAt() {
        return (Instant) get(17);
    }

    /**
     * Setter for <code>public.oauth2_registered_client.modified_by</code>.
     */
    public void setModifiedBy(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>public.oauth2_registered_client.modified_by</code>.
     */
    public String getModifiedBy() {
        return (String) get(18);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record19 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row19<String, String, Instant, String, Instant, String, String, String, String, String, String, String, String, byte[], Boolean, Instant, String, Instant, String> fieldsRow() {
        return (Row19) super.fieldsRow();
    }

    @Override
    public Row19<String, String, Instant, String, Instant, String, String, String, String, String, String, String, String, byte[], Boolean, Instant, String, Instant, String> valuesRow() {
        return (Row19) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.ID;
    }

    @Override
    public Field<String> field2() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.CLIENT_ID;
    }

    @Override
    public Field<Instant> field3() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.CLIENT_ID_ISSUED_AT;
    }

    @Override
    public Field<String> field4() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.CLIENT_SECRET;
    }

    @Override
    public Field<Instant> field5() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.CLIENT_SECRET_EXPIRES_AT;
    }

    @Override
    public Field<String> field6() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.CLIENT_NAME;
    }

    @Override
    public Field<String> field7() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.CLIENT_AUTHENTICATION_METHODS;
    }

    @Override
    public Field<String> field8() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.AUTHORIZATION_GRANT_TYPES;
    }

    @Override
    public Field<String> field9() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.REDIRECT_URIS;
    }

    @Override
    public Field<String> field10() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.POST_LOGOUT_REDIRECT_URIS;
    }

    @Override
    public Field<String> field11() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.SCOPES;
    }

    @Override
    public Field<String> field12() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.CLIENT_SETTINGS;
    }

    @Override
    public Field<String> field13() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.TOKEN_SETTINGS;
    }

    @Override
    public Field<byte[]> field14() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.PHOTO;
    }

    @Override
    public Field<Boolean> field15() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.ENABLED;
    }

    @Override
    public Field<Instant> field16() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.CREATED_AT;
    }

    @Override
    public Field<String> field17() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.CREATED_BY;
    }

    @Override
    public Field<Instant> field18() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.MODIFIED_AT;
    }

    @Override
    public Field<String> field19() {
        return Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.MODIFIED_BY;
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getClientId();
    }

    @Override
    public Instant component3() {
        return getClientIdIssuedAt();
    }

    @Override
    public String component4() {
        return getClientSecret();
    }

    @Override
    public Instant component5() {
        return getClientSecretExpiresAt();
    }

    @Override
    public String component6() {
        return getClientName();
    }

    @Override
    public String component7() {
        return getClientAuthenticationMethods();
    }

    @Override
    public String component8() {
        return getAuthorizationGrantTypes();
    }

    @Override
    public String component9() {
        return getRedirectUris();
    }

    @Override
    public String component10() {
        return getPostLogoutRedirectUris();
    }

    @Override
    public String component11() {
        return getScopes();
    }

    @Override
    public String component12() {
        return getClientSettings();
    }

    @Override
    public String component13() {
        return getTokenSettings();
    }

    @Override
    public byte[] component14() {
        return getPhoto();
    }

    @Override
    public Boolean component15() {
        return getEnabled();
    }

    @Override
    public Instant component16() {
        return getCreatedAt();
    }

    @Override
    public String component17() {
        return getCreatedBy();
    }

    @Override
    public Instant component18() {
        return getModifiedAt();
    }

    @Override
    public String component19() {
        return getModifiedBy();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getClientId();
    }

    @Override
    public Instant value3() {
        return getClientIdIssuedAt();
    }

    @Override
    public String value4() {
        return getClientSecret();
    }

    @Override
    public Instant value5() {
        return getClientSecretExpiresAt();
    }

    @Override
    public String value6() {
        return getClientName();
    }

    @Override
    public String value7() {
        return getClientAuthenticationMethods();
    }

    @Override
    public String value8() {
        return getAuthorizationGrantTypes();
    }

    @Override
    public String value9() {
        return getRedirectUris();
    }

    @Override
    public String value10() {
        return getPostLogoutRedirectUris();
    }

    @Override
    public String value11() {
        return getScopes();
    }

    @Override
    public String value12() {
        return getClientSettings();
    }

    @Override
    public String value13() {
        return getTokenSettings();
    }

    @Override
    public byte[] value14() {
        return getPhoto();
    }

    @Override
    public Boolean value15() {
        return getEnabled();
    }

    @Override
    public Instant value16() {
        return getCreatedAt();
    }

    @Override
    public String value17() {
        return getCreatedBy();
    }

    @Override
    public Instant value18() {
        return getModifiedAt();
    }

    @Override
    public String value19() {
        return getModifiedBy();
    }

    @Override
    public Oauth2RegisteredClientRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value2(String value) {
        setClientId(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value3(Instant value) {
        setClientIdIssuedAt(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value4(String value) {
        setClientSecret(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value5(Instant value) {
        setClientSecretExpiresAt(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value6(String value) {
        setClientName(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value7(String value) {
        setClientAuthenticationMethods(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value8(String value) {
        setAuthorizationGrantTypes(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value9(String value) {
        setRedirectUris(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value10(String value) {
        setPostLogoutRedirectUris(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value11(String value) {
        setScopes(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value12(String value) {
        setClientSettings(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value13(String value) {
        setTokenSettings(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value14(byte[] value) {
        setPhoto(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value15(Boolean value) {
        setEnabled(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value16(Instant value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value17(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value18(Instant value) {
        setModifiedAt(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord value19(String value) {
        setModifiedBy(value);
        return this;
    }

    @Override
    public Oauth2RegisteredClientRecord values(String value1, String value2, Instant value3, String value4, Instant value5, String value6, String value7, String value8, String value9, String value10, String value11, String value12, String value13, byte[] value14, Boolean value15, Instant value16, String value17, Instant value18, String value19) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        value18(value18);
        value19(value19);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached Oauth2RegisteredClientRecord
     */
    public Oauth2RegisteredClientRecord() {
        super(Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT);
    }

    /**
     * Create a detached, initialised Oauth2RegisteredClientRecord
     */
    public Oauth2RegisteredClientRecord(String id, String clientId, Instant clientIdIssuedAt, String clientSecret, Instant clientSecretExpiresAt, String clientName, String clientAuthenticationMethods, String authorizationGrantTypes, String redirectUris, String postLogoutRedirectUris, String scopes, String clientSettings, String tokenSettings, byte[] photo, Boolean enabled, Instant createdAt, String createdBy, Instant modifiedAt, String modifiedBy) {
        super(Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT);

        setId(id);
        setClientId(clientId);
        setClientIdIssuedAt(clientIdIssuedAt);
        setClientSecret(clientSecret);
        setClientSecretExpiresAt(clientSecretExpiresAt);
        setClientName(clientName);
        setClientAuthenticationMethods(clientAuthenticationMethods);
        setAuthorizationGrantTypes(authorizationGrantTypes);
        setRedirectUris(redirectUris);
        setPostLogoutRedirectUris(postLogoutRedirectUris);
        setScopes(scopes);
        setClientSettings(clientSettings);
        setTokenSettings(tokenSettings);
        setPhoto(photo);
        setEnabled(enabled);
        setCreatedAt(createdAt);
        setCreatedBy(createdBy);
        setModifiedAt(modifiedAt);
        setModifiedBy(modifiedBy);
        resetChangedOnNotNull();
    }
}
