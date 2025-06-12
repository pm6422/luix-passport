package cn.luixtech.passport.server;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Sets;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.luixtech.passport.server.config.AuthorizationServerConfiguration.*;
import static cn.luixtech.passport.server.pojo.Oauth2Client.SCOPE_ALL_SUPPORTED_TIME_ZONE_READ;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class OAuth2AuthorizationTests {
    private static final String                            REDIRECT_URI              = "http://127.0.0.1/login/oauth2/code/messaging-client-oidc";
    private static final String                            AUTHORIZATION_REQUEST_URI = UriComponentsBuilder
            .fromPath("/oauth2/authorize")
            .queryParam("client_id", "messaging-client")
            .queryParam("response_type", "code")
            .queryParam("scope", OidcScopes.OPENID + " " + SCOPE_ALL_SUPPORTED_TIME_ZONE_READ)
            .queryParam("state", "state")
            .queryParam("redirect_uri", REDIRECT_URI)
            .toUriString();
    private static final String                            PROTECTED_RESOURCE_URI    = "/api/accounts/all-supported-time-zones";
    @Resource
    private              MockMvc                           mockMvc;
    @Resource
    private              WebClient                         webClient;
    @MockBean
    private              OAuth2AuthorizationConsentService authorizationConsentService;

    @BeforeEach
    public void setUp() {
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
        this.webClient.getOptions().setRedirectEnabled(true);
        this.webClient.getOptions().setThrowExceptionOnScriptError(false);
        // Log out
        this.webClient.getCookieManager().clearCookies();
        when(this.authorizationConsentService.findById(any(), any())).thenReturn(null);
    }

    @Test
    @DisplayName("client credential mode")
    public void clientCredentialMode() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());
        // Get different level access token based on different scope
        params.add(OAuth2ParameterNames.SCOPE, SCOPE_ALL_SUPPORTED_TIME_ZONE_READ);
        // Request access token
        Map<String, Object> resultMap = requestToken(params);
        assertThat(resultMap.get(OAuth2ParameterNames.SCOPE)).isEqualTo(SCOPE_ALL_SUPPORTED_TIME_ZONE_READ);
        // Request resource by access token
        assertRequestResource(resultMap.get("access_token").toString());
    }

    private Map<String, Object> requestToken(MultiValueMap<String, String> params) throws Exception {
        ResultActions result = mockMvc.perform(post(TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, getBasicHeader(AUTH_CODE_CLIENT_ID, AUTH_CODE_CLIENT_SECRET))
                        .params(params)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        String resultString = result.andReturn().getResponse().getContentAsString();
        Map<String, Object> resultMap = new JacksonJsonParser().parseMap(resultString);
        log.info("Token result: {}", JSON.toJSONString(resultMap, JSONWriter.Feature.PrettyFormat));
        assertThat(resultMap).containsKey("access_token");
        return resultMap;
    }

    private String getBasicHeader(String clientId, String rawClientSecret) {
        return AUTHORIZATION_BASIC + Base64.getEncoder().encodeToString((clientId + ":" + rawClientSecret).getBytes());
    }

    private void assertRequestResource(String accessToken) throws Exception {
        // unauthorized if request has no access token
//        mockMvc.perform(get(PROTECTED_RESOURCE_URI)
//                        .contentType(APPLICATION_JSON_VALUE)
//                        .accept(APPLICATION_JSON_VALUE))
//                // Note: this is not a bug, it's a feature!
//                .andExpect(status().isUnauthorized());

        // authorized if request has an access token in header
        mockMvc.perform(get(PROTECTED_RESOURCE_URI)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_BEARER + accessToken)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("user1")
    @DisplayName("authorization code mode")
    public void authCodeMode() throws Exception {
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        // Get authorization code via web login
        String authCode = getAuthCode();
        // Get access token by authorization code
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
        params.add(OAuth2ParameterNames.CODE, authCode);
        params.add(OAuth2ParameterNames.STATE, "some-state");
        params.add(OAuth2ParameterNames.REDIRECT_URI, REDIRECT_URI);
        Map<String, Object> resultMap = requestToken(params);
        String actualScopeStr = resultMap.get(OAuth2ParameterNames.SCOPE).toString();

        assertThat(new HashSet<>(Arrays.stream(actualScopeStr.split(" ")).toList()))
                .isEqualTo(Sets.newHashSet(OidcScopes.OPENID, SCOPE_ALL_SUPPORTED_TIME_ZONE_READ));
        // Request resource by access token
        assertRequestResource(resultMap.get("access_token").toString());
    }

    @Test
    @DisplayName("introspect access token")
    public void introspectAccessToken() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());
        // Get different level access token with different scope
        params.add(OAuth2ParameterNames.SCOPE, SCOPE_ALL_SUPPORTED_TIME_ZONE_READ);
        // Request access token
        Map<String, Object> resultMap = requestToken(params);
        String accessToken = resultMap.get("access_token").toString();

        ResultActions result = mockMvc.perform(post(INTROSPECT_TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, getBasicHeader(AUTH_CODE_CLIENT_ID, AUTH_CODE_CLIENT_SECRET))
                        .param(OAuth2ParameterNames.TOKEN, accessToken)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        String resultString = result.andReturn().getResponse().getContentAsString();
        Map<String, Object> objectMap = new JacksonJsonParser().parseMap(resultString);
        log.info("Access token details: {}", JSON.toJSONString(objectMap, JSONWriter.Feature.PrettyFormat));
    }

    @Test
    @DisplayName("view JWK(JSON Web Key)")
    public void viewJwk() throws Exception {
        ResultActions result = mockMvc.perform(get(VIEW_JWK_URI)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        String resultString = result.andReturn().getResponse().getContentAsString();
        Map<String, Object> objectMap = new JacksonJsonParser().parseMap(resultString);
        log.info("JWK details: {}", JSON.toJSONString(objectMap, JSONWriter.Feature.PrettyFormat));
    }

    @Test
    @WithMockUser("user1")
    @DisplayName("refresh access token")
    public void refreshAccessToken() throws Exception {
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        // Get authorization code via web login
        String authCode = getAuthCode();
        // Get access token by authorization code
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
        params.add(OAuth2ParameterNames.CODE, authCode);
        params.add(OAuth2ParameterNames.STATE, "some-state");
        params.add(OAuth2ParameterNames.REDIRECT_URI, REDIRECT_URI);
        Map<String, Object> resultMap = requestToken(params);

        String refreshToken1 = resultMap.get("refresh_token").toString();
        String accessToken1 = resultMap.get("access_token").toString();

        // It must sleep
        TimeUnit.SECONDS.sleep(1L);

        MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
        params2.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.REFRESH_TOKEN.getValue());
        params2.add(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken1);
        Map<String, Object> resultMap2 = requestToken(params2);

        String refreshToken2 = resultMap2.get("refresh_token").toString();
        String accessToken2 = resultMap2.get("access_token").toString();

        // refresh_token值保持不变，access_token值重新生成
        assertThat(refreshToken1).isEqualTo(refreshToken2);
        assertThat(accessToken1).isNotEqualTo(accessToken2);

        // Inactive token still works
        // https://devforum.okta.com/t/inactive-token-still-works/13460

//        mockMvc.perform(get(PROTECTED_RESOURCE_URI)
//                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_BEARER + accessToken1)
//                .contentType(APPLICATION_JSON_VALUE)
//                .accept(APPLICATION_JSON_VALUE))
//        .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("revoke access token")
    public void revokeAccessToken() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());
        // Get different level access token with different scope
        params.add(OAuth2ParameterNames.SCOPE, "all_supported_time_zone:read");
        // Request access token
        Map<String, Object> resultMap = requestToken(params);

        String accessToken = resultMap.get("access_token").toString();

        // Introspect access token
        ResultActions result1 = mockMvc.perform(post(INTROSPECT_TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, getBasicHeader(AUTH_CODE_CLIENT_ID, AUTH_CODE_CLIENT_SECRET))
                        .param(OAuth2ParameterNames.TOKEN, accessToken)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        String resultString1 = result1.andReturn().getResponse().getContentAsString();
        Map<String, Object> objectMap1 = new JacksonJsonParser().parseMap(resultString1);
        assertThat(objectMap1.get("active")).isEqualTo(true);

        // Revoke access token
        mockMvc.perform(post(REVOKE_TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, getBasicHeader(AUTH_CODE_CLIENT_ID, AUTH_CODE_CLIENT_SECRET))
                        .param(OAuth2ParameterNames.TOKEN, accessToken)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        // Introspect access token
        ResultActions result2 = mockMvc.perform(post(INTROSPECT_TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, getBasicHeader(AUTH_CODE_CLIENT_ID, AUTH_CODE_CLIENT_SECRET))
                        .param(OAuth2ParameterNames.TOKEN, accessToken)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        String resultString2 = result2.andReturn().getResponse().getContentAsString();
        Map<String, Object> objectMap2 = new JacksonJsonParser().parseMap(resultString2);
        assertThat(objectMap2.get("active")).isEqualTo(false);

        // It must sleep
        TimeUnit.SECONDS.sleep(1L);

        // Inactive token still works
        // https://devforum.okta.com/t/inactive-token-still-works/13460

//        mockMvc.perform(get(PROTECTED_RESOURCE_URI)
//                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_BEARER + accessToken)
//                        .contentType(APPLICATION_JSON_VALUE)
//                        .accept(APPLICATION_JSON_VALUE))
//                .andExpect(status().isUnauthorized());
    }

    private String getAuthCode() throws IOException, URISyntaxException {
        final HtmlPage consentPage = this.webClient.getPage(AUTHORIZATION_REQUEST_URI);
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

        DomElement submitConsentButton = consentPage.querySelector("button[id='submit-consent']");
        this.webClient.getOptions().setRedirectEnabled(false);

        WebResponse approveConsentResponse = submitConsentButton.click().getWebResponse();
        assertThat(approveConsentResponse.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.value());
        String location = approveConsentResponse.getResponseHeaderValue("location");
        assertThat(location).startsWith(REDIRECT_URI);
        assertThat(location).contains("code=");

        URIBuilder uriBuilder = new URIBuilder(location);
        List<NameValuePair> params = uriBuilder.getQueryParams();
        Optional<NameValuePair> code = params.stream().filter(p -> p.getName().equals("code")).findFirst();
        assertThat(code.isPresent()).isTrue();
        return code.get().getValue();
    }
}