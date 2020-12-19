package org.infinity.passport.config.oauth2;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.io.Serializable;
import java.util.*;

/**
 * Deserialize back into an OAuth2Authentication Object made necessary because
 * Spring Mongo can't map clientAuthentication instance to authorizationRequest.
 */
public class OAuth2AuthenticationReadConverter implements Converter<Document, OAuth2Authentication> {

    @SuppressWarnings("unchecked")
    @Override
    public OAuth2Authentication convert(Document source) {
        Document storedRequest = (Document) source.get("storedRequest");
        OAuth2Request oAuth2Request = new OAuth2Request((Map<String, String>) storedRequest.get("requestParameters"),
                (String) storedRequest.get("clientId"),
                getAuthorities((List<Map<String, String>>) storedRequest.get("authorities")),
                (boolean) storedRequest.get("approved"), new HashSet<>((List<String>) storedRequest.get("scope")),
                new HashSet<>((List<String>) storedRequest.get("resourceIds")),
                (String) storedRequest.get("redirectUri"),
                new HashSet<>((List<String>) storedRequest.get("responseTypes")),
                (Map<String, Serializable>) storedRequest.get("extensionProperties"));

        Authentication userAuthentication = null;
        if (source.get("userAuthentication") != null) {
            Document userAuthorization = (Document) source.get("userAuthentication");
            Object principal = getPrincipalObject(userAuthorization.get("principal"));
            if (principal == null) {
                // For nested userAuthorization in OAuth2Authentication
                userAuthorization = (Document) userAuthorization.get("userAuthentication");
                if (userAuthorization != null) {
                    principal = getPrincipalObject(userAuthorization.get("principal"));
                }
            }
            if (principal != null) {
                userAuthentication = new UsernamePasswordAuthenticationToken(principal,
                        userAuthorization.get("credentials"),
                        getAuthorities((List<Map<String, String>>) userAuthorization.get("authorities")));
            }
        }
        return new OAuth2Authentication(oAuth2Request, userAuthentication);
    }

    @SuppressWarnings("unchecked")
    private Object getPrincipalObject(Object principal) {
        if (principal instanceof Document) {
            Document principalDBObject = (Document) principal;

            String userId = (String) principalDBObject.get("userId");
            String userName = (String) principalDBObject.get("username");
            String password = "";
            boolean enabled = (boolean) principalDBObject.get("enabled");
            boolean accountNonExpired = (boolean) principalDBObject.get("accountNonExpired");
            boolean credentialsNonExpired = (boolean) principalDBObject.get("credentialsNonExpired");
            boolean accountNonLocked = (boolean) principalDBObject.get("accountNonLocked");

            return new SecurityUser(userId, userName, password, enabled,
                    accountNonExpired, credentialsNonExpired, accountNonLocked,
                    getAuthorities((List<Map<String, String>>) principalDBObject.get("authorities")));
        } else {
            return principal;
        }
    }

    private Collection<GrantedAuthority> getAuthorities(List<Map<String, String>> authorities) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>(authorities.size());
        for (Map<String, String> authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.get("role")));
        }
        return grantedAuthorities;
    }
}
