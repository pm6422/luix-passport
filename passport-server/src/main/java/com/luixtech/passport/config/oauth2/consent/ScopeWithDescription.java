package com.luixtech.passport.config.oauth2.consent;

import java.util.HashMap;
import java.util.Map;

public class ScopeWithDescription {
    private static final String              DEFAULT_DESCRIPTION = "我们无法提供有关此权限的信息";
    private static final Map<String, String> scopeDescriptions   = new HashMap<>();

    static {
        scopeDescriptions.put(
                "profile",
                "profile information"
        );
        scopeDescriptions.put(
                "openid",
                "open ID"
        );
    }

    public final String scope;
    public final String description;

    public ScopeWithDescription(String scope) {
        this.scope = scope;
        this.description = scopeDescriptions.getOrDefault(scope, DEFAULT_DESCRIPTION);
    }

    @Override
    public String toString() {
        return scope + "(" + description + ")";
    }
}