package cn.luixtech.passport.server.config.oauth.authorization.token;

import cn.luixtech.passport.server.config.oauth.AuthUser;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An {@link OAuth2TokenCustomizer} to map claims from a federated identity to
 * the {@code id_token} produced by this authorization server.
 */
public final class LuixOAuth2TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {
    @Override
    public void customize(JwtEncodingContext context) {
        if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
            Map<String, Object> thirdPartyClaims = extractClaims(context.getPrincipal(), context.getAuthorizedScopes());
            context.getClaims().claims(existingClaims -> {
                // Remove conflicting claims set by this authorization server
                existingClaims.keySet().forEach(thirdPartyClaims::remove);

                // Remove standard id_token claims that could cause problems with clients
                LuixClaimNames.ID_TOKEN_CLAIMS.forEach(thirdPartyClaims::remove);

                // Add all other claims directly to id_token
                existingClaims.putAll(thirdPartyClaims);
            });
        }
    }

    private Map<String, Object> extractClaims(Authentication authentication, Set<String> authorizedScopes) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(LuixClaimNames.COMPANY, "https://luixtech.cn");

        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            OidcIdToken idToken = oidcUser.getIdToken();
            claims = idToken.getClaims();
        } else if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            claims = oauth2User.getAttributes();
        } else if (authentication.getPrincipal() instanceof AuthUser) {
            claims = new HashMap<>(32);
            addAuthoritiesClaim(claims, authentication);
            addClaimsByScopes(claims, authentication, authorizedScopes);
        }
        return new HashMap<>(claims);
    }

    private void addAuthoritiesClaim(Map<String, Object> claims, Authentication authentication) {
        if (CollectionUtils.isNotEmpty(authentication.getAuthorities())) {
            Set<String> authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            claims.put(LuixClaimNames.CLAIM_NAMES_AUTHORITIES, authorities);
        }
    }

    private void addClaimsByScopes(Map<String, Object> claims, Authentication authentication, Set<String> authorizedScopes) {
        Set<String> authorizedClaimNames = new HashSet<>(32);

        authorizedClaimNames.add(StandardClaimNames.SUB);
        if (authorizedScopes.contains(OidcScopes.EMAIL)) {
            authorizedClaimNames.addAll(LuixClaimNames.EMAIL_CLAIMS);
        }
        if (authorizedScopes.contains(OidcScopes.PHONE)) {
            authorizedClaimNames.addAll(LuixClaimNames.PHONE_CLAIMS);
        }
        if (authorizedScopes.contains(OidcScopes.PROFILE)) {
            authorizedClaimNames.addAll(LuixClaimNames.PROFILE_CLAIMS);
        }

        // first add all claims
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            addAllClaims(claims, authentication.getPrincipal());
        } else if (authentication instanceof OAuth2AccessTokenAuthenticationToken) {
            addAllClaims(claims, authentication.getDetails());
        }
        // then remove claims which are not in authorized scopes
        claims.keySet().removeIf(claimName -> !authorizedClaimNames.contains(claimName));
    }

    private void addAllClaims(Map<String, Object> claims, Object object) {
        if (object instanceof AuthUser user) {
            claims.put(StandardClaimNames.NAME, user.getUsername());
            claims.put(StandardClaimNames.PREFERRED_USERNAME, user.getUsername());
            claims.put(StandardClaimNames.EMAIL, user.getEmail());
            claims.put(StandardClaimNames.EMAIL_VERIFIED, true);
            claims.put(StandardClaimNames.PHONE_NUMBER, user.getMobileNo());
            claims.put(StandardClaimNames.PHONE_NUMBER_VERIFIED, user.getMobileNo());
            claims.put(StandardClaimNames.PICTURE, user.getProfilePicUrl());
            claims.put(StandardClaimNames.LOCALE, user.getLocale());
            claims.put(StandardClaimNames.UPDATED_AT, user.getModifiedTime());
            claims.put(LuixClaimNames.ROLES, user.getRoles());
        }
    }
}
