package org.infinity.passport.config.oauth2;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Deserialize back into an GrantedAuthority Object made necessary because
 * Spring Mongo can't map grantedAuthority instance to GrantedAuthority.
 */
public class OAuth2GrantedAuthorityTokenReadConverter implements Converter<Document, GrantedAuthority> {

    @Override
    public GrantedAuthority convert(Document source) {
        return new SimpleGrantedAuthority((String) source.get("role"));
    }
}
