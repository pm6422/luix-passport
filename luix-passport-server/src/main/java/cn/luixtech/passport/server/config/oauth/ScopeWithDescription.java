package cn.luixtech.passport.server.config.oauth;

import lombok.Data;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import java.util.HashMap;
import java.util.Map;

import static cn.luixtech.passport.server.pojo.Oauth2Client.SCOPE_MESSAGE_READ;

@Data
public class ScopeWithDescription {
    private static final String              DEFAULT_DESCRIPTION = "UNKNOWN SCOPE - We cannot provide information about this permission, use caution when granting this.";
    private static final Map<String, String> scopeDescriptions   = new HashMap<>();
    private final        String              scope;
    private final        String              description;

    public ScopeWithDescription(String scope) {
        this.scope = scope;
        this.description = scopeDescriptions.getOrDefault(scope, DEFAULT_DESCRIPTION);
    }

    static {
        scopeDescriptions.put(
                OidcScopes.PROFILE,
                "Read your all profile data"
        );
        scopeDescriptions.put(
                OidcScopes.EMAIL,
                "Read your email data"
        );
        scopeDescriptions.put(
                SCOPE_MESSAGE_READ,
                "Read messages from your account"
        );
        scopeDescriptions.put(
                "other.scope",
                "This is another scope example of a scope description"
        );
    }
}