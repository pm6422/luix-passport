package com.luixtech.passport.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import com.luixtech.passport.domain.oauth2.OAuth2Authorization;
import com.luixtech.passport.repository.oauth2.OAuth2AuthorizationRepository;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@AllArgsConstructor
@Deprecated
public class OAuth2AuthorizationServiceImpl implements OAuth2AuthorizationService {
    private static final ObjectMapper                  OBJECT_MAPPER = new ObjectMapper();
    private              OAuth2AuthorizationRepository oAuth2AuthorizationRepository;
    private              RegisteredClientRepository    registeredClientRepository;

    static {
        ClassLoader classLoader = OAuth2AuthorizationServiceImpl.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        OBJECT_MAPPER.registerModules(securityModules);
        OBJECT_MAPPER.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }

    @Override
    public void save(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        this.oAuth2AuthorizationRepository.save(toEntity(authorization));
    }

    @Override
    public void remove(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        this.oAuth2AuthorizationRepository.deleteById(authorization.getId());
    }

    @Override
    public org.springframework.security.oauth2.server.authorization.OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        return this.oAuth2AuthorizationRepository.findById(id).map(this::toObject).orElse(null);
    }

    @Override
    public org.springframework.security.oauth2.server.authorization.OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");

        Optional<OAuth2Authorization> result;
        if (tokenType == null) {
            result = this.oAuth2AuthorizationRepository.findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValue(token, token, token, token);
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            result = this.oAuth2AuthorizationRepository.findByState(token);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            result = this.oAuth2AuthorizationRepository.findByAuthorizationCodeValue(token);
        } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenType.getValue())) {
            result = this.oAuth2AuthorizationRepository.findByAccessTokenValue(token);
        } else if (OAuth2ParameterNames.REFRESH_TOKEN.equals(tokenType.getValue())) {
            result = this.oAuth2AuthorizationRepository.findByRefreshTokenValue(token);
        } else {
            result = Optional.empty();
        }

        return result.map(this::toObject).orElse(null);
    }

    private org.springframework.security.oauth2.server.authorization.OAuth2Authorization toObject(OAuth2Authorization entity) {
        RegisteredClient registeredClient = this.registeredClientRepository.findById(entity.getRegisteredClientId());
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + entity.getRegisteredClientId() + "' was not found in the RegisteredClientRepository.");
        }

        org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Builder builder = org.springframework.security.oauth2.server.authorization.OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(entity.getId())
                .principalName(entity.getPrincipalName())
                .authorizationGrantType(resolveAuthorizationGrantType(entity.getAuthorizationGrantType()))
                .attributes(attributes -> attributes.putAll(parseMap(entity.getAttributes())));
        if (entity.getState() != null) {
            builder.attribute(OAuth2ParameterNames.STATE, entity.getState());
        }

        if (entity.getAuthorizationCodeValue() != null) {
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    entity.getAuthorizationCodeValue(),
                    entity.getAuthorizationCodeIssuedAt().atZone(ZoneId.systemDefault()).toInstant(),
                    entity.getAuthorizationCodeExpiresAt().atZone(ZoneId.systemDefault()).toInstant());
            builder.token(authorizationCode, metadata -> metadata.putAll(parseMap(entity.getAuthorizationCodeMetadata())));
        }

        if (entity.getAccessTokenValue() != null) {
            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    entity.getAccessTokenValue(),
                    entity.getAccessTokenIssuedAt().atZone(ZoneId.systemDefault()).toInstant(),
                    entity.getAccessTokenExpiresAt().atZone(ZoneId.systemDefault()).toInstant(),
                    StringUtils.commaDelimitedListToSet(entity.getAccessTokenScopes()));
            builder.token(accessToken, metadata -> metadata.putAll(parseMap(entity.getAccessTokenMetadata())));
        }

        if (entity.getRefreshTokenValue() != null) {
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    entity.getRefreshTokenValue(),
                    entity.getRefreshTokenIssuedAt().atZone(ZoneId.systemDefault()).toInstant(),
                    entity.getRefreshTokenExpiresAt().atZone(ZoneId.systemDefault()).toInstant());
            builder.token(refreshToken, metadata -> metadata.putAll(parseMap(entity.getRefreshTokenMetadata())));
        }

        if (entity.getOidcIdTokenValue() != null) {
            OidcIdToken idToken = new OidcIdToken(
                    entity.getOidcIdTokenValue(),
                    entity.getOidcIdTokenIssuedAt().atZone(ZoneId.systemDefault()).toInstant(),
                    entity.getOidcIdTokenExpiresAt().atZone(ZoneId.systemDefault()).toInstant(),
                    parseMap(entity.getOidcIdTokenClaims()));
            builder.token(idToken, metadata -> metadata.putAll(parseMap(entity.getOidcIdTokenMetadata())));
        }

        return builder.build();
    }

    private OAuth2Authorization toEntity(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization) {
        OAuth2Authorization entity = new OAuth2Authorization();
        entity.setId(authorization.getId());
        entity.setRegisteredClientId(authorization.getRegisteredClientId());
        entity.setPrincipalName(authorization.getPrincipalName());
        entity.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
        entity.setAttributes(writeMap(authorization.getAttributes()));
        entity.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));

        org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
                authorization.getToken(OAuth2AuthorizationCode.class);
        setTokenValues(authorizationCode, entity::setAuthorizationCodeValue,
                entity::setAuthorizationCodeIssuedAt,
                entity::setAuthorizationCodeExpiresAt, entity::setAuthorizationCodeMetadata
        );

        org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        setTokenValues(accessToken, entity::setAccessTokenValue, entity::setAccessTokenIssuedAt,
                entity::setAccessTokenExpiresAt, entity::setAccessTokenMetadata
        );
        if (accessToken != null && accessToken.getToken().getScopes() != null) {
            entity.setAccessTokenScopes(StringUtils.collectionToDelimitedString(accessToken.getToken().getScopes(), ","));
        }

        org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        setTokenValues(refreshToken, entity::setRefreshTokenValue, entity::setRefreshTokenIssuedAt,
                entity::setRefreshTokenExpiresAt, entity::setRefreshTokenMetadata
        );

        org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token<OidcIdToken> oidcIdToken =
                authorization.getToken(OidcIdToken.class);
        setTokenValues(oidcIdToken, entity::setOidcIdTokenValue, entity::setOidcIdTokenIssuedAt,
                entity::setOidcIdTokenExpiresAt, entity::setOidcIdTokenMetadata
        );
        if (oidcIdToken != null) {
            entity.setOidcIdTokenClaims(writeMap(oidcIdToken.getClaims()));
        }
        return entity;
    }

    private void setTokenValues(org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token<?> token, Consumer<String> tokenValueConsumer,
                                Consumer<Instant> issuedAtConsumer, Consumer<Instant> expiresAtConsumer,
                                Consumer<String> metadataConsumer) {
        if (token != null) {
            OAuth2Token oauth2Token = token.getToken();
            tokenValueConsumer.accept(oauth2Token.getTokenValue());
            issuedAtConsumer.accept(oauth2Token.getIssuedAt());
            expiresAtConsumer.accept(oauth2Token.getExpiresAt());
            metadataConsumer.accept(writeMap(token.getMetadata()));
        }
    }

    private Map<String, Object> parseMap(String data) {
        try {
            return OBJECT_MAPPER.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private String writeMap(Map<String, Object> metadata) {
        try {
            return OBJECT_MAPPER.writeValueAsString(metadata);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private static AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        // Custom authorization grant type
        return new AuthorizationGrantType(authorizationGrantType);
    }
}
