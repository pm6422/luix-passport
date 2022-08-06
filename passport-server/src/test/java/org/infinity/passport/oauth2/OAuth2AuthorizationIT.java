package org.infinity.passport.oauth2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.infinity.passport.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.infinity.passport.config.OAuth2AuthServerSecurityConfiguration.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@Slf4j
public class OAuth2AuthorizationIT {
    private static final String    REDIRECT_URI = "https://www.baidu.com";
    @Resource
    private              MockMvc   mockMvc;
    @Resource
    private              WebClient webClient;

    @BeforeEach
    public void setUp() {
        // log out
        this.webClient.getCookieManager().clearCookies();
    }

    /**
     * Result:
     * {
     * "access_token": "eyJraWQiOiJhYjE2MmJmOS1jNmY1LTQ2MGUtOTUwNS00MmQ2ZjQwYWYzYTkiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJpbnRlcm5hbC1jbGllbnQiLCJhdWQiOiJpbnRlcm5hbC1jbGllbnQiLCJuYmYiOjE2NTkzNDM3NDUsInNjb3BlIjpbInJlYWQiLCJvcGVuaWQiLCJ3cml0ZSIsInVzZXJpbmZvIl0sImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdDo2MDMwIiwiZXhwIjoxNjU5MzQ3MzQ1LCJpYXQiOjE2NTkzNDM3NDV9.m-oy9ImGl0YcUO_MeOJOQ1x9nFW6KLeg0PNG9gU_OJLoA4EzL52XIJV1qilRD7uD27hVBn4W4Snkzyp1Ue7oY9e5Dm863fjwc68oQueflJua_9CJYrDxeo368N1F8lpBd6lh5BUrCk1Pc0uiigTPNiut0Lb_cqvdjNgV8LnsYw0CuddXLa6-N4cSk8awhpB7H45ym55u0OGoYozZNte4ef9O3twZ3WRSi6G5F-O1SzCliWGj9f4dw4NaV_umsfykNHgxm0AUdVzSR1TpJDNzCkN4Yx6IN3kjopvi2BFRJOZBfaVR41KWBTrftxvujPYlNffr_JNr9mZZwcCu8wAX6w",
     * "scope": "read openid write userinfo",
     * "token_type": "Bearer",
     * "expires_in": 3599
     * }
     *
     * @throws Exception
     */
    @Test
    @DisplayName("client credential mode")
    public void clientCredentialMode() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());
        params.add(OAuth2ParameterNames.SCOPE, "message:read");
        // Request access token
        Map<String, Object> resultMap = requestToken(INTERNAL_CLIENT_ID, INTERNAL_RAW_CLIENT_SECRET, params);
        // Request resource by access token
        assertRequestResource(resultMap);
    }

    /**
     * Result:
     * {
     * "access_token": "eyJraWQiOiIyOGM0OTIzMi1iZDIxLTQ0NDYtYmZjOC01ZjgxOGM2ZTRhYTMiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJsb3VpcyIsImF1ZCI6ImludGVybmFsLWNsaWVudCIsIm5iZiI6MTY1OTM0MzgwMiwidXNlcl9pZCI6IjYyZTc5M2IzNmVhM2VhMmIyOTNmZGU4MiIsInNjb3BlIjpbInJlYWQiLCJvcGVuaWQiLCJ3cml0ZSIsInVzZXJpbmZvIl0sImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdDo2MDMwIiwiZXhwIjoxNjU5MzQ3NDAyLCJpYXQiOjE2NTkzNDM4MDJ9.qKukybt12_VAaLdwl-rgRx8f7hhG6w03s2HJM9V-EKDYuoDxaIKRAsT9hUHcuEh9TLb6GErEcOX_bukxjS0ndQQPsVQ_LCWkOvYrq0wsY7IWsCEsZzDyDPIQ_PMKjGppNTLz2ZmuwOdT8HYILxy3p-WefJ993dKbRkcjtWMP_Yn0bCnBxFkdWrGATc9shUm6UKSVwP2z5H0U-VJ46znhWXpBbWyoPjAdFaifTfPmf3IsWWeNLaDl9jTpcilS6-chO4qHIagxVuHjqrcRzrUbC1i6GFhlgbOwclaZrNvIaq20wcVlMcOZCO8u4_2Z4TOnXh8oXpOw9rcFnT60QQchNA",
     * "refresh_token": "jOVbAITFMHnSNdDjxyXYge7TbaJvF9cE8MqOf7ZV5Uw4X2i7__pJC-KisJCHD1dBfve_jobxsPfelM7KIYKA0dNgn_0QacT2J6azWFMIK__kaxZtH4uVlFdIhHyuuVSU",
     * "scope": "read openid write userinfo",
     * "id_token": "eyJraWQiOiIyOGM0OTIzMi1iZDIxLTQ0NDYtYmZjOC01ZjgxOGM2ZTRhYTMiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJsb3VpcyIsImF1ZCI6ImludGVybmFsLWNsaWVudCIsImF6cCI6ImludGVybmFsLWNsaWVudCIsImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdDo2MDMwIiwiZXhwIjoxNjU5MzQ1NjAyLCJpYXQiOjE2NTkzNDM4MDJ9.qh8yAcGkhIeaGxgv_mO-nPA3Obl6qJjpL6Q7Bt3yx_ToyFhMpgiGy0tD9Kw-BwuIbRkxw5trIDCgctLcwXgK1jkb3KmWlshWK4Kb1y2-0FPE9epTE97P1EFWH_AmlF95h6dQGwM83mNjoph3khXwQY9TFKHIxDhGwPaY7AhXicEv-N_lVkShccyYH6aLKC7rk7Nv6mDPcp0-i_lDziiIvyPmDpUgHVcc-NLcYg5TNoNFr9-yZxeQH-ElvPQqSz84SHqHFUehDyKhJIRI_khXlhFqTx9bhLX0LrZeQ9to4lvW-Go0crKSoVXjad43Ioz0AbesiGj2KKzcsCarreloPA",
     * "token_type": "Bearer",
     * "expires_in": 3599
     * }
     *
     * @throws Exception
     */
    @Test
    @DisplayName("password mode")
    public void passwordMode() throws Exception {
        Map<String, Object> resultMap = requestTokenByPasswordMode();
        // Request resource by access token
        assertRequestResource(resultMap);
    }

    private Map<String, Object> requestTokenByPasswordMode() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue());
        params.add(OAuth2ParameterNames.USERNAME, "user");
        params.add(OAuth2ParameterNames.PASSWORD, "password");
        // Request access token
        return requestToken(INTERNAL_CLIENT_ID, INTERNAL_RAW_CLIENT_SECRET, params);
    }

    /**
     * Result:
     * {
     * "access_token": "eyJraWQiOiIxZmZiNGRjYy0wZWFjLTRmZmItYTYwZS0xZTljMWE4ZjU5MDgiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJsb3VpcyIsImF1ZCI6ImludGVybmFsLWNsaWVudCIsIm5iZiI6MTY1OTM0MzYyNywidXNlcl9pZCI6IjYyZTc5MzA0YWIyN2U5NGMwZjQ2NmE3MyIsInNjb3BlIjpbIm9wZW5pZCJdLCJpc3MiOiJodHRwOlwvXC9sb2NhbGhvc3Q6NjAzMCIsImV4cCI6MTY1OTM0NzIyNywiaWF0IjoxNjU5MzQzNjI3fQ.x5qy3qH0iPieVSc-JjyQsjUoKqFsQZYGMyWJ3DxdZlTjDks4QC_9HxF9GqVdq1nYYdogs0RfzGc5Jr2uHhtceR92HQDCtaDCcWvDpP3C9dIE5kIXXVFEXk4ly8jfjstQkRTaOxY3fhRzWcxbn3I1keVOXCQRNyP1zVl4svh5Ua_LM4iNPc8Eu9Vgy-fQcem54nL79ytU2konZWD9boGjYlEZRl_6zMYyQXjaUqPqgxpEj2VF6P4o4EeK_Bv88U8XAbvHY9J6RCH8WUqb_ULpKYGR_B5rKezOZkUYddhgmM3Sy3cyIyf2_xo7GB7a7lz7giMjZa8uCx5a7kMY6kWEpg",
     * "refresh_token": "Y1LRzoWiKicNcajY4MqjxBX9oHaTbT1vTRoKuDyireFV_09MFdqmZrG1tRJATHIUAvrjdXGl6gmk-t1_vMItRGU7Egj8t3yUrWQfjt9ujHuswmA6s4ng6eYyMiRelo6H",
     * "scope": "openid",
     * "id_token": "eyJraWQiOiIxZmZiNGRjYy0wZWFjLTRmZmItYTYwZS0xZTljMWE4ZjU5MDgiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJsb3VpcyIsImF1ZCI6ImludGVybmFsLWNsaWVudCIsImF6cCI6ImludGVybmFsLWNsaWVudCIsImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdDo2MDMwIiwiZXhwIjoxNjU5MzQ1NDI3LCJpYXQiOjE2NTkzNDM2Mjd9.SrzWqL666ADuLF3MRuNUT27YX3boL75VjANggzLz2B_aKhDeBEsF4xwFHeaIoupRnOw2s5LznQXwCHv_h2J7W8dHYJjqmtSALvhUIkslxdN6ODWjprtoeIcf4uDAnQBZlJ_64iTMBMotKrHfqgtiv43BMrxzFL3BE4_OHYXV8fuCsQGJ7QTK4ObDs-L8zfzk2qz_Olecgm9BShIfNMTifYkyhlUET4E19Qgy0VauMq0HTVGCGGfqCnVBOCMjhM5PYlRXqYccdrK9R_FhQY5hwXgDZfWkqgGXMpl50LDpsjzqZh8MdJuO9rIY75ZOra-DTeoLxc90wRs2ixOtzgW17Q",
     * "token_type": "Bearer",
     * "expires_in": 3599
     * }
     *
     * @throws Exception
     */
    @Test
    @DisplayName("password mode with the specified scope")
    public void passwordModeWithOpenIdScope() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue());
        params.add(OAuth2ParameterNames.USERNAME, "user");
        params.add(OAuth2ParameterNames.PASSWORD, "password");
        params.add(OAuth2ParameterNames.SCOPE, "openid");
        // Request access token
        Map<String, Object> resultMap = requestToken(INTERNAL_CLIENT_ID, INTERNAL_RAW_CLIENT_SECRET, params);
        assertThat(resultMap.get("scope")).isEqualTo("openid");
    }

    /**
     * Result:
     * {
     * "access_token": "eyJraWQiOiI4NTZkM2IwMi04MmVjLTRiYTItYTM1Yi1mZjNkMDNjYWYxMTgiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJsb3VpcyIsImF1ZCI6ImludGVybmFsLWNsaWVudCIsIm5iZiI6MTY1OTQxMDYxOSwic2NvcGUiOlsib3BlbmlkIl0sImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdDowIiwiZXhwIjoxNjU5NDE0MjE5LCJpYXQiOjE2NTk0MTA2MTl9.nMrU32nSfbr4w9Cdw_NNW5LBrdm6CJ-KKZYj-85sM5T4ux6yKwHaVRsfzbQWc7EOVNi4NYj9d8xLt2n-HqPjZMy_3RLKNkE9POCUVv23uHc42WF9L1V3pih38FM7F5_L56pY-gfAd6IU_8CM2dRZGAWMQ-6FOKzxOAxp6I-NVuVPxF-ZyaOdqrf-cBj1SOPdMiS9lpwXOFj8woOLylMxqcyUxpUAzHAvjWXyCEe3FcOptvs8zJ6xInVLfA_gr7b1O-OU5sdqRlmMOXodZzGeUezdS6v-oJEbvZSB-UbUNzW6l45GfjKJ_VE4jiRC_viFrVg6m3Llz586fn8CA-DsCA",
     * "refresh_token": "OMJfk7e3gbIqi7KAc_E0bwXnEWMc68ZHWlZEr4zk8k0F69-qBwq8A4HHmIV-z9oSoJC2hErroGt5D6yN2j-KGeK6-XgM6jOCh11-sznSLBLgPJ-iOo81jxwvuxMLmYJY",
     * "scope": "openid",
     * "id_token": "eyJraWQiOiI4NTZkM2IwMi04MmVjLTRiYTItYTM1Yi1mZjNkMDNjYWYxMTgiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJsb3VpcyIsImF1ZCI6ImludGVybmFsLWNsaWVudCIsImF6cCI6ImludGVybmFsLWNsaWVudCIsImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdDowIiwiZXhwIjoxNjU5NDEyNDE5LCJpYXQiOjE2NTk0MTA2MTl9.PmBJU9n6ktV_aP_yS2XgtyOEcL2yvZSu6VseRykx7Ynf-M0TntFxDI4BbViBnDQKzeYCdgJA1aWTBeu4pR5q2bGNvp79V3QfEKGF8dgLzqpJLFy7xfdD01I-aCp-y5cxBv2WNeh-7yIuteVbi8AI7xwhgmX56WIZEioF4_l_unJ4se59vEoWYQmyIe7k-edcl7owwaL_YBFyJu3KqFrQr207yT_Rw5XipkrUfHO8g3tk7Obpb2arqLwab6O1VtQ4Z6E0ROTjWCnKJkhnaRqncC_DcbRKUEq1osqIXhngWKB9haI1K2VADOBfG1-hiONaFoKo4MbaIjnFVCiu1VZ1Sg",
     * "token_type": "Bearer",
     * "expires_in": 3599
     * }
     *
     * @throws Exception
     */
    @Test
    @DisplayName("authorization code mode")
    public void authCodeMode() throws Exception {
        // Get authorization code via web login
        String authCode = getAuthCode(AUTH_CODE_CLIENT_ID, OidcScopes.OPENID);
        // Get access token by authorization code
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
        params.add(OAuth2ParameterNames.CODE, authCode);
        params.add(OAuth2ParameterNames.STATE, "some-state");
        params.add(OAuth2ParameterNames.REDIRECT_URI, REDIRECT_URI);
        Map<String, Object> resultMap = requestToken(AUTH_CODE_CLIENT_ID, INTERNAL_RAW_CLIENT_SECRET, params);
        assertThat(resultMap.get("scope")).isEqualTo("openid");
        // Request resource by access token
        assertRequestResource(resultMap);
    }

    @Test
    @DisplayName("introspect access token")
    void introspectAccessToken() throws Exception {
        // Request access token
        Map<String, Object> resultMap = requestTokenByPasswordMode();
        String accessToken = resultMap.get("access_token").toString();

        ResultActions result = mockMvc.perform(post(INTROSPECT_TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, getBasicHeader(INTERNAL_CLIENT_ID, INTERNAL_RAW_CLIENT_SECRET))
                        .param(OAuth2ParameterNames.TOKEN, accessToken)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        String resultString = result.andReturn().getResponse().getContentAsString();
        Map<String, Object> objectMap = new JacksonJsonParser().parseMap(resultString);
        log.info("Access token details: {}", JSON.toJSONString(objectMap, JSONWriter.Feature.PrettyFormat));
    }

    @Test
    @DisplayName("view JWK(JSON Web Key)")
    void viewJwk() throws Exception {
        ResultActions result = mockMvc.perform(get(VIEW_JWK_URI)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        String resultString = result.andReturn().getResponse().getContentAsString();
        Map<String, Object> objectMap = new JacksonJsonParser().parseMap(resultString);
        log.info("JWK details: {}", JSON.toJSONString(objectMap, JSONWriter.Feature.PrettyFormat));
    }

    @Test
    @DisplayName("refresh access token")
    public void refreshAccessToken() throws Exception {
        // Request access token
        Map<String, Object> resultMap = requestTokenByPasswordMode();
        String refreshToken1 = resultMap.get("refresh_token").toString();
        String accessToken1 = resultMap.get("access_token").toString();

        // It must sleep
        TimeUnit.SECONDS.sleep(1L);

        MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
        params2.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.REFRESH_TOKEN.getValue());
        params2.add(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken1);
        Map<String, Object> resultMap2 = requestToken(INTERNAL_CLIENT_ID, INTERNAL_RAW_CLIENT_SECRET, params2);

        String refreshToken2 = resultMap2.get("refresh_token").toString();
        String accessToken2 = resultMap2.get("access_token").toString();

        // refresh_token值保持不变，access_token值重新生成
        assertThat(refreshToken1).isEqualTo(refreshToken2);
        assertThat(accessToken1).isNotEqualTo(accessToken2);
    }

    @Test
    @DisplayName("revoke access token")
    public void revokeAccessToken() throws Exception {
        // Request access token
        Map<String, Object> resultMap = requestTokenByPasswordMode();

        String accessToken = resultMap.get("access_token").toString();
        String refreshToken = resultMap.get("access_token").toString();

        // Introspect access token
        ResultActions result1 = mockMvc.perform(post(INTROSPECT_TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, getBasicHeader(INTERNAL_CLIENT_ID, INTERNAL_RAW_CLIENT_SECRET))
                        .param(OAuth2ParameterNames.TOKEN, accessToken)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        String resultString1 = result1.andReturn().getResponse().getContentAsString();
        Map<String, Object> objectMap1 = new JacksonJsonParser().parseMap(resultString1);
        assertThat(objectMap1.get("active")).isEqualTo(true);

        // Revoke access token
        mockMvc.perform(post(REVOKE_TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, getBasicHeader(INTERNAL_CLIENT_ID, INTERNAL_RAW_CLIENT_SECRET))
                        .param(OAuth2ParameterNames.TOKEN, accessToken)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        // Introspect access token
        ResultActions result2 = mockMvc.perform(post(INTROSPECT_TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, getBasicHeader(INTERNAL_CLIENT_ID, INTERNAL_RAW_CLIENT_SECRET))
                        .param(OAuth2ParameterNames.TOKEN, accessToken)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        String resultString2 = result2.andReturn().getResponse().getContentAsString();
        Map<String, Object> objectMap2 = new JacksonJsonParser().parseMap(resultString2);
        assertThat(objectMap2.get("active")).isEqualTo(false);

//        mockMvc.perform(get("/api/userinfo")
//                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_BEARER + accessToken)
//                        .contentType(APPLICATION_JSON_VALUE)
//                        .accept(APPLICATION_JSON_VALUE))
//                .andExpect(status().isUnauthorized());
    }


    private Map<String, Object> requestToken(String clientId, String rawClientSecret, MultiValueMap<String, String> params) throws Exception {
        ResultActions result = mockMvc.perform(post(TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, getBasicHeader(clientId, rawClientSecret))
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

    private String getAuthCode(String clientId, String scope) throws IOException, URISyntaxException {
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient.getOptions().setRedirectEnabled(false);

        HtmlPage page = this.webClient.getPage(CUSTOM_LOGIN_PAGE_URI);
        // Redirect to Sign-in page
        assertLoginPage(page);
        // Sign in
        signIn(page, "user", "password");

        String authorizationRequest = UriComponentsBuilder
                .fromPath("/oauth2/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("scope", scope)
                .queryParam("state", "some-state")
                .queryParam("redirect_uri", REDIRECT_URI)
                .toUriString();
        // Request token
        WebResponse response = this.webClient.getPage(authorizationRequest).getWebResponse();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.value());

        String location = response.getResponseHeaderValue("location");
        assertThat(location).startsWith(REDIRECT_URI);
        URIBuilder uriBuilder = new URIBuilder(location);
        List<NameValuePair> params = uriBuilder.getQueryParams();
        Optional<NameValuePair> code = params.stream().filter(p -> p.getName().equals("code")).findFirst();
        assertThat(code.isPresent()).isTrue();
        return code.get().getValue();
    }

    private static void assertLoginPage(HtmlPage page) {
        assertThat(page.getUrl().toString()).endsWith(CUSTOM_LOGIN_PAGE_URI);

        HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
        HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
        HtmlButton signInButton = page.querySelector("button");

        assertThat(usernameInput).isNotNull();
        assertThat(passwordInput).isNotNull();
        assertThat(signInButton.getTextContent()).isEqualTo("Sign in");
    }

    private static <P extends Page> P signIn(HtmlPage page, String username, String password) throws IOException {
        HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
        HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
        HtmlButton signInButton = page.querySelector("button");

        usernameInput.type(username);
        passwordInput.type(password);
        return signInButton.click();
    }

    private void assertRequestResource(Map<String, Object> resultMap) throws Exception {
        // unauthorized if request has no access token
        mockMvc.perform(get("/api/dicts")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE))
                // Note: this is not a bug, it's a feature!
                .andExpect(status().isFound());

        // authorized if request has an access token
        String accessToken = resultMap.get("access_token").toString();
        mockMvc.perform(get("/api/dicts")
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_BEARER + accessToken)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }
}