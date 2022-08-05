package org.infinity.passport.config.oauth2;


import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Utility methods for the OAuth 2.0 Configurers.
 */
public abstract class OAuth2ConfigurerUtils {

    @SuppressWarnings("unchecked")
    public static <B extends HttpSecurityBuilder<B>> OAuth2TokenGenerator<? extends OAuth2Token> getTokenGenerator(B builder) {
        OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator = builder.getSharedObject(OAuth2TokenGenerator.class);
        if (tokenGenerator == null) {
            tokenGenerator = getOptionalBean(builder, OAuth2TokenGenerator.class);
            if (tokenGenerator == null) {
                JwtGenerator jwtGenerator = getJwtGenerator(builder);
                OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
                OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer = getAccessTokenCustomizer(builder);
                if (accessTokenCustomizer != null) {
                    accessTokenGenerator.setAccessTokenCustomizer(accessTokenCustomizer);
                }
                OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
                if (jwtGenerator != null) {
                    tokenGenerator = new DelegatingOAuth2TokenGenerator(
                            jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
                } else {
                    tokenGenerator = new DelegatingOAuth2TokenGenerator(
                            accessTokenGenerator, refreshTokenGenerator);
                }
            }
            builder.setSharedObject(OAuth2TokenGenerator.class, tokenGenerator);
        }
        return tokenGenerator;
    }

    private static <B extends HttpSecurityBuilder<B>> JwtGenerator getJwtGenerator(B builder) {
        JwtGenerator jwtGenerator = builder.getSharedObject(JwtGenerator.class);
        if (jwtGenerator == null) {
            JwtEncoder jwtEncoder = getJwtEncoder(builder);
            if (jwtEncoder != null) {
                jwtGenerator = new JwtGenerator(jwtEncoder);
                OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer = getJwtCustomizer(builder);
                if (jwtCustomizer != null) {
                    jwtGenerator.setJwtCustomizer(jwtCustomizer);
                }
                builder.setSharedObject(JwtGenerator.class, jwtGenerator);
            }
        }
        return jwtGenerator;
    }

    private static <B extends HttpSecurityBuilder<B>> JwtEncoder getJwtEncoder(B builder) {
        JwtEncoder jwtEncoder = builder.getSharedObject(JwtEncoder.class);
        if (jwtEncoder == null) {
            jwtEncoder = getOptionalBean(builder, JwtEncoder.class);
            if (jwtEncoder == null) {
                JWKSource<SecurityContext> jwkSource = getJwkSource(builder);
                if (jwkSource != null) {
                    jwtEncoder = new NimbusJwtEncoder(jwkSource);
                }
            }
            if (jwtEncoder != null) {
                builder.setSharedObject(JwtEncoder.class, jwtEncoder);
            }
        }
        return jwtEncoder;
    }

    @SuppressWarnings("unchecked")
    public static <B extends HttpSecurityBuilder<B>> JWKSource<SecurityContext> getJwkSource(B builder) {
        JWKSource<SecurityContext> jwkSource = builder.getSharedObject(JWKSource.class);
        if (jwkSource == null) {
            ResolvableType type = ResolvableType.forClassWithGenerics(JWKSource.class, SecurityContext.class);
            jwkSource = getOptionalBean(builder, type);
            if (jwkSource != null) {
                builder.setSharedObject(JWKSource.class, jwkSource);
            }
        }
        return jwkSource;
    }

    private static <B extends HttpSecurityBuilder<B>> OAuth2TokenCustomizer<JwtEncodingContext> getJwtCustomizer(B builder) {
        ResolvableType type = ResolvableType.forClassWithGenerics(OAuth2TokenCustomizer.class, JwtEncodingContext.class);
        return getOptionalBean(builder, type);
    }

    private static <B extends HttpSecurityBuilder<B>> OAuth2TokenCustomizer<OAuth2TokenClaimsContext> getAccessTokenCustomizer(B builder) {
        ResolvableType type = ResolvableType.forClassWithGenerics(OAuth2TokenCustomizer.class, OAuth2TokenClaimsContext.class);
        return getOptionalBean(builder, type);
    }

    public static <B extends HttpSecurityBuilder<B>, T> T getOptionalBean(B builder, Class<T> type) {
        Map<String, T> beansMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(
                builder.getSharedObject(ApplicationContext.class), type);
        if (beansMap.size() > 1) {
            throw new NoUniqueBeanDefinitionException(type, beansMap.size(),
                    "Expected single matching bean of type '" + type.getName() + "' but found " +
                            beansMap.size() + ": " + StringUtils.collectionToCommaDelimitedString(beansMap.keySet()));
        }
        return (!beansMap.isEmpty() ? beansMap.values().iterator().next() : null);
    }

    @SuppressWarnings("unchecked")
    public static <B extends HttpSecurityBuilder<B>, T> T getOptionalBean(B builder, ResolvableType type) {
        ApplicationContext context = builder.getSharedObject(ApplicationContext.class);
        String[] names = context.getBeanNamesForType(type);
        if (names.length > 1) {
            throw new NoUniqueBeanDefinitionException(type, names);
        }
        return names.length == 1 ? (T) context.getBean(names[0]) : null;
    }
}
