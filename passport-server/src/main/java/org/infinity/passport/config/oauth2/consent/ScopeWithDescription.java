package org.infinity.passport.config.oauth2.consent;

import java.util.HashMap;
import java.util.Map;

public class ScopeWithDescription {
    private static final String              DEFAULT_DESCRIPTION = "我们无法提供有关此权限的信息";
    private static final Map<String, String> scopeDescriptions   = new HashMap<>();

    static {
        scopeDescriptions.put(
                "profile",
                "验证您的身份"
        );
        scopeDescriptions.put(
                "message.read",
                "了解您可以访问哪些权限"
        );
        scopeDescriptions.put(
                "message.write",
                "代表您行事"
        );
    }

    public final String scope;
    public final String description;

    public ScopeWithDescription(String scope) {
        this.scope = scope;
        this.description = scopeDescriptions.getOrDefault(scope, DEFAULT_DESCRIPTION);
    }
}