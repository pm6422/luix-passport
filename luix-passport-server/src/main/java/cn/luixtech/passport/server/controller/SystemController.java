package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.config.oauth.ScopeWithDescription;
import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.repository.UserRepository;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

import static cn.luixtech.passport.server.service.impl.UserProfilePicServiceImpl.CURRENT_USER_PHOTO_URL;
import static com.luixtech.springbootframework.utils.NetworkUtils.getRequestUrl;

@Controller
@AllArgsConstructor
@Slf4j
public class SystemController {
    private final RegisteredClientRepository        registeredClientRepository;
    private final OAuth2AuthorizationConsentService authorizationConsentService;
    private final UserRepository                    userRepository;

    @RequestMapping("/system/error")
    public String handleError(Model model, HttpServletRequest request) {
        String errorMessage = getErrorMessage(request);
        if (errorMessage.startsWith("[access_denied]")) {
            model.addAttribute("errorTitle", "Access Denied");
            model.addAttribute("errorMessage", "You have denied access.");
            log.warn("Access denied for {}", request.getRequestURL());
        } else {
            model.addAttribute("errorTitle", "Error");
            model.addAttribute("errorMessage", errorMessage);
        }
        return "error";
    }

    private String getErrorMessage(HttpServletRequest request) {
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        return StringUtils.hasText(errorMessage) ? errorMessage : "";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        // Check if the user is already authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping(value = "/oauth2/consent")
    public String consent(Principal principal, Model model,
                          HttpServletRequest request,
                          @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                          @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                          @RequestParam(OAuth2ParameterNames.STATE) String state,
                          @RequestParam(name = OAuth2ParameterNames.USER_CODE, required = false) String userCode) {

        // Remove scopes that were already approved
        Set<String> scopesToApprove = new HashSet<>();
        Set<String> previouslyApprovedScopes = new HashSet<>();
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        OAuth2AuthorizationConsent currentAuthorizationConsent =
                this.authorizationConsentService.findById(Objects.requireNonNull(registeredClient).getId(), principal.getName());
        Optional<User> user = userRepository.findById(principal.getName());
        Set<String> authorizedScopes;
        if (currentAuthorizationConsent != null) {
            authorizedScopes = currentAuthorizationConsent.getScopes();
        } else {
            authorizedScopes = Collections.emptySet();
        }
        for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
            if (OidcScopes.OPENID.equals(requestedScope)) {
                continue;
            }
            if (authorizedScopes.contains(requestedScope)) {
                previouslyApprovedScopes.add(requestedScope);
            } else {
                scopesToApprove.add(requestedScope);
            }
        }

        model.addAttribute("clientId", clientId);
        model.addAttribute("state", state);
        model.addAttribute("scopes", withDescription(scopesToApprove));
        model.addAttribute("previouslyApprovedScopes", withDescription(previouslyApprovedScopes));
        model.addAttribute("principalName", user.isPresent() ? user.get().getEmail() : principal.getName());
        model.addAttribute("userCode", userCode);
        model.addAttribute("userProfilePic", getRequestUrl(request) + CURRENT_USER_PHOTO_URL);
        if (StringUtils.hasText(userCode)) {
            model.addAttribute("requestURI", "/oauth2/device_verification");
        } else {
            model.addAttribute("requestURI", "/oauth2/authorize");
        }

        return "consent";
    }

    private static Set<ScopeWithDescription> withDescription(Set<String> scopes) {
        Set<ScopeWithDescription> scopeWithDescriptions = new HashSet<>();
        for (String scope : scopes) {
            scopeWithDescriptions.add(new ScopeWithDescription(scope));
        }
        return scopeWithDescriptions;
    }
}
