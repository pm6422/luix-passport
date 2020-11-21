package org.infinity.passport.config.oauth2;

import java.util.Date;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

/**
 * Deserialize back into an OAuth2RefreshToken Object made necessary because
 * Spring Mongo can't map oAuth2RefreshToken instance to OAuth2RefreshToken.
 */
public class OAuth2RefreshTokenReadConverter implements Converter<Document, OAuth2RefreshToken> {

    @Override
    public OAuth2RefreshToken convert(Document source) {
        return new DefaultExpiringOAuth2RefreshToken(
                (String) source.get("value"), (Date) source.get("expiration"));
    }
}
