package cn.luixtech.passport.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import static cn.luixtech.passport.server.pojo.Oauth2Client.SCOPE_ALL_SUPPORTED_TIME_ZONE_READ;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Consent screen integration tests for the sample Authorization Server.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthorizationServerConsentTests {
    private static final String                            REDIRECT_URI              = "http://127.0.0.1/login/oauth2/code/messaging-client-oidc";
    private static final String                            AUTHORIZATION_REQUEST_URI = UriComponentsBuilder
            .fromPath("/oauth2/authorize")
            .queryParam("client_id", "messaging-client")
            .queryParam("response_type", "code")
            .queryParam("scope", OidcScopes.OPENID + " " + SCOPE_ALL_SUPPORTED_TIME_ZONE_READ)
            .queryParam("state", "state")
            .queryParam("redirect_uri", REDIRECT_URI)
            .toUriString();
    @Resource
    private              WebClient                         webClient;
    @MockBean
    private              OAuth2AuthorizationConsentService authorizationConsentService;

    @BeforeEach
    public void setUp() {
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient.getOptions().setRedirectEnabled(true);
        this.webClient.getOptions().setThrowExceptionOnScriptError(false);
        this.webClient.getCookieManager().clearCookies();
        when(this.authorizationConsentService.findById(any(), any())).thenReturn(null);
    }

    @Test
    @WithMockUser("user1")
    public void whenUserConsentsToAllScopesThenReturnAuthorizationCode() throws IOException {
        final HtmlPage consentPage = this.webClient.getPage(AUTHORIZATION_REQUEST_URI);
        assertThat(consentPage.getTitleText()).isEqualTo("Passport | Authorization");

        List<HtmlCheckBoxInput> scopes = new ArrayList<>();
        consentPage.querySelectorAll("input[name='scope']").forEach(scope ->
                scopes.add((HtmlCheckBoxInput) scope));
        for (HtmlCheckBoxInput scope : scopes) {
            scope.click();
        }

        List<String> scopeIds = new ArrayList<>();
        scopes.forEach(scope -> {
            assertThat(scope.isChecked()).isTrue();
            scopeIds.add(scope.getId());
        });
        assertThat(scopeIds).containsExactlyInAnyOrder(SCOPE_ALL_SUPPORTED_TIME_ZONE_READ);

        DomElement submitConsentButton = consentPage.querySelector("button[id='submit-consent']");
        this.webClient.getOptions().setRedirectEnabled(false);

        WebResponse approveConsentResponse = submitConsentButton.click().getWebResponse();
        assertThat(approveConsentResponse.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.value());
        String location = approveConsentResponse.getResponseHeaderValue("location");
        assertThat(location).startsWith(REDIRECT_URI);
        assertThat(location).contains("code=");
    }

    @Test
    @WithMockUser("user1")
    public void whenUserCancelsConsentThenReturnAccessDeniedError() throws IOException {
        final HtmlPage consentPage = this.webClient.getPage(AUTHORIZATION_REQUEST_URI);
        assertThat(consentPage.getTitleText()).isEqualTo("Passport | Authorization");

        DomElement cancelConsentButton = consentPage.querySelector("button[id='cancel-consent']");
        this.webClient.getOptions().setRedirectEnabled(false);

        WebResponse cancelConsentResponse = cancelConsentButton.click().getWebResponse();
        // 禁用导致
        assertThat(cancelConsentResponse.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.value());
        String location = cancelConsentResponse.getResponseHeaderValue("location");
        assertThat(location).startsWith(REDIRECT_URI);
        assertThat(location).contains("error=access_denied");
    }
}
