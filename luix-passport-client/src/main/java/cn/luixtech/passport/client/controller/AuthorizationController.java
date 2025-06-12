package cn.luixtech.passport.client.controller;

import cn.luixtech.passport.client.config.ApplicationProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Controller
@AllArgsConstructor
public class AuthorizationController {
    private final WebClient             webClient;
    private final ApplicationProperties applicationProperties;

    @GetMapping(value = "/authorize", params = "grant_type=client_credentials")
    public String clientCredentialsGrant(Model model) {
        String[] oauthServerMessages = this.webClient
                .get()
                .uri(applicationProperties.getUrl().getOauthServerMessages())
                .attributes(clientRegistrationId("messaging-client-client-credentials"))
                .retrieve()
                .bodyToMono(String[].class)
                .block();
        model.addAttribute("messages", oauthServerMessages);
        return "index";
    }

    @GetMapping(value = "/authorize", params = "grant_type=authorization_code")
    public String authorizationCodeGrant(Model model,
                                         @RegisteredOAuth2AuthorizedClient("messaging-client-authorization-code")
                                         OAuth2AuthorizedClient authorizedClient) {
        String[] resourceServerMessages = this.webClient
                .get()
                .uri(applicationProperties.getUrl().getResourceServerMessages())
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String[].class)
                .block();
        model.addAttribute("messages", resourceServerMessages);
        return "index";
    }

    // '/authorized' is the registered 'redirect_uri' for authorization_code
    @GetMapping(value = "/authorized", params = OAuth2ParameterNames.ERROR)
    public String authorizationFailed(Model model, HttpServletRequest request) {
        String errorCode = request.getParameter(OAuth2ParameterNames.ERROR);
        if (StringUtils.hasText(errorCode)) {
            model.addAttribute("error",
                    new OAuth2Error(
                            errorCode,
                            request.getParameter(OAuth2ParameterNames.ERROR_DESCRIPTION),
                            request.getParameter(OAuth2ParameterNames.ERROR_URI))
            );
        }
        return "index";
    }
}
