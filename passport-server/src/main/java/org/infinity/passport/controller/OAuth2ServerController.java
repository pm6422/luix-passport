package org.infinity.passport.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.config.oauth2.consent.ScopeWithDescription;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.infinity.passport.config.OAuth2ServerSecurityConfiguration.CUSTOM_CONSENT_PAGE_URI;
import static org.infinity.passport.config.OAuth2ServerSecurityConfiguration.CUSTOM_LOGIN_PAGE_URI;

/**
 * Controller for OAuth2 server.
 */
@Controller
@AllArgsConstructor
@Slf4j
public class OAuth2ServerController {
    private final RegisteredClientRepository registeredClientRepository;

    @GetMapping(CUSTOM_LOGIN_PAGE_URI)
    public String forwardToLoginPage() {
        return CUSTOM_LOGIN_PAGE_URI;
    }

    @GetMapping(CUSTOM_CONSENT_PAGE_URI)
    public String forwardToConsentPage(Principal principal, Model model,
                                       @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                                       @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                                       @RequestParam(OAuth2ParameterNames.STATE) String state) {
        Set<String> scopesToApprove = new LinkedHashSet<>();
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        Set<String> scopes = registeredClient.getScopes();
        for (String requestedScope : org.springframework.util.StringUtils.delimitedListToStringArray(scope, " ")) {
            if (scopes.contains(requestedScope)) {
                scopesToApprove.add(requestedScope);
            }
        }

        model.addAttribute("clientId", clientId);
        model.addAttribute("clientName", registeredClient.getClientName());
        model.addAttribute("state", state);
        model.addAttribute("scopes", scopesToApprove.stream().map(ScopeWithDescription::new).collect(Collectors.toSet()));
        model.addAttribute("principalName", principal != null ? principal.getName() : "");
        model.addAttribute("redirectUri", registeredClient.getRedirectUris().iterator().next());
        return "oauth2/consent";
    }
}
