package cn.luixtech.passport.server;

import java.io.IOException;

import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.WebResponse;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the sample Authorization Server.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthorizationServerApplicationTests {
    private static final String BASE_URL = "http://localhost";
    public static final String REDIRECT_URI = "http://127.0.0.1:4003/login/oauth2/code/messaging-client-oidc";
    public static final String AUTHORIZATION_REQUEST_URI = UriComponentsBuilder
            .fromUriString(BASE_URL + "/oauth2/authorize")
            .queryParam("client_id", "messaging-client")
            .queryParam("response_type", "code")
            .queryParam("scope", "openid")
            .queryParam("state", "some-state")
            .queryParam("redirect_uri", REDIRECT_URI)
            .toUriString();
    @Resource
    private WebClient webClient;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.webClient = MockMvcWebClientBuilder.mockMvcSetup(this.mockMvc).build();
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
        this.webClient.getOptions().setRedirectEnabled(true);
        this.webClient.getOptions().setThrowExceptionOnScriptError(false);
        this.webClient.getOptions().setCssEnabled(false);
        // Log out
        this.webClient.getCookieManager().clearCookies();
    }

    @Test
    public void whenLoginSuccessfulThenOk() throws IOException {
        HtmlPage page = this.webClient.getPage(BASE_URL + "/login");

        assertLoginPage(page);

        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        WebResponse signInResponse = signIn(page, "user", "user").getWebResponse();

        assertThat(signInResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void whenLoginFailsThenDisplayBadCredentials() throws IOException {
        HtmlPage page = this.webClient.getPage(BASE_URL + "/login");

        HtmlPage loginErrorPage = signIn(page, "user", "wrong-password");

        HtmlElement alert = loginErrorPage.querySelector("div[id=\"error\"]");
        assertThat(alert).isNotNull();
        assertThat(alert.asNormalizedText()).isEqualTo("Invalid username or password.");
    }

    @Test
    public void whenNotLoggedInAndRequestingTokenThenRedirectsToLogin() throws IOException {
        HtmlPage loginPage = this.webClient.getPage(BASE_URL + "/login");
        assertLoginPage(loginPage);
    }

    @Test
    public void whenLoggingInAndRequestingTokenThenRedirectsToClientApplication() throws IOException {
        // Log in
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient.getOptions().setRedirectEnabled(false);
        signIn(this.webClient.getPage(BASE_URL + "/login"), "user", "user");

        // Request token
        WebResponse response = this.webClient.getPage(AUTHORIZATION_REQUEST_URI).getWebResponse();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.value());
        String location = response.getResponseHeaderValue("location");
        assertThat(location).startsWith(REDIRECT_URI);
        assertThat(location).contains("code=");
    }

    private static <P extends Page> P signIn(HtmlPage page, String username, String password) throws IOException {
        HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
        HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
        HtmlButton signInButton = page.querySelector("button");

        usernameInput.type(username);
        passwordInput.type(password);
        return signInButton.click();
    }

    private static void assertLoginPage(HtmlPage page) {
        HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
        HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
        HtmlButton signInButton = page.querySelector("button");

        assertThat(usernameInput).isNotNull();
        assertThat(passwordInput).isNotNull();
        assertThat(signInButton.getTextContent().trim()).isEqualTo("Sign In");
    }
}
