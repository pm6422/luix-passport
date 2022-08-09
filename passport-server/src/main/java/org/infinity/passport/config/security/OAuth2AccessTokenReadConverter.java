package org.infinity.passport.config.security;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Deserialize back into an OAuth2AccessToken Object made necessary because
 * Spring Mongo can't map oAuth2AccessToken instance to OAuth2AccessToken.
 */
public class OAuth2AccessTokenReadConverter implements Converter<Document, OAuth2AccessToken> {

    @SuppressWarnings({"unchecked"})
    @Override
    public OAuth2AccessToken convert(Document source) {
        DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken((String) source.get("value"));
        oAuth2AccessToken.setExpiration((Date) source.get("expiration"));
        oAuth2AccessToken.setTokenType((String) source.get("tokenType"));
        Document refreshToken = (Document) source.get("refreshToken");
        if (refreshToken != null) {
            DefaultExpiringOAuth2RefreshToken oAuth2RefreshToken = new DefaultExpiringOAuth2RefreshToken(
                    (String) refreshToken.get("value"), (Date) refreshToken.get("expiration"));
            oAuth2AccessToken.setRefreshToken(oAuth2RefreshToken);
        }
        oAuth2AccessToken.setScope(new HashSet<>((List<String>) source.get("scope")));
        oAuth2AccessToken.setAdditionalInformation((Map<String, Object>) source.get("additionalInformation"));
        return oAuth2AccessToken;
    }
}
