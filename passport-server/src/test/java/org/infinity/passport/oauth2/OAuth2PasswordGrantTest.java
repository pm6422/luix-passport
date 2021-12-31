package org.infinity.passport.oauth2;

import org.infinity.passport.IntegrationTest;
import org.infinity.passport.domain.MongoOAuth2ClientDetails;
import org.junit.jupiter.api.Test;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Pair;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@IntegrationTest
public class OAuth2PasswordGrantTest {

    @Resource
    private MockMvc mockMvc;

    private static final String CLIENT_ID         = MongoOAuth2ClientDetails.INTERNAL_CLIENT_ID;
    private static final String RAW_CLIENT_SECRET = MongoOAuth2ClientDetails.INTERNAL_RAW_CLIENT_SECRET;
    private static final String CONTENT_TYPE      = "application/json;charset=UTF-8";

    private ResultActions obtainToken(String username, String password) throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", RAW_CLIENT_SECRET);
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);

        // @formatter:off
        ResultActions result = mockMvc.perform(post("/oauth/token")
                        .params(params)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE));
        return result;
    }

    private String refreshAccessToken(String refreshToken) throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", RAW_CLIENT_SECRET);
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);

        // @formatter:off
        ResultActions result = mockMvc.perform(post("/oauth/token")
                        .params(params)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE));
        String resultString = result.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    private String obtainAccessToken(String username, String password) throws Exception {
        ResultActions result = obtainToken(username, password);
        // @formatter:on
        String resultString = result.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    private Pair<String, String> obtainTokenPair(String username, String password) throws Exception {
        ResultActions result = obtainToken(username, password);
        // @formatter:on
        String resultString = result.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return Pair.of(jsonParser.parseMap(resultString).get("access_token").toString(),
                jsonParser.parseMap(resultString).get("refresh_token").toString());
    }

    @Test
    public void accessResourceWhenUseAccessTokenThenOk() throws Exception {
        String accessToken = this.obtainAccessToken("louis", "louis");

        // @formatter:off
        ResultActions result = mockMvc.perform(get("/management/env")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(CONTENT_TYPE)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isOk());
        // @formatter:on

        String resultString = result.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        String profilesString = jsonParser.parseMap(resultString).get("activeProfiles").toString();
        assertThat(profilesString).isNotEmpty();
    }

    @Test
    public void accessResourceWhenNoAccessTokenThenUnauthorized() throws Exception {
        // @formatter:off
        mockMvc.perform(get("/management/env")
                        .contentType(CONTENT_TYPE)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isUnauthorized());
        // @formatter:on
    }

    @Test
    public void accessResourceWhenUseLowerAuthorityThenForbidden() throws Exception {
        String accessToken = this.obtainAccessToken("admin", "admin");

        // @formatter:off
        mockMvc.perform(get("/management/env")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(CONTENT_TYPE)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isForbidden());
        // @formatter:on
    }

    @Test
    public void accessResourceWhenAjaxLogoutThenUnauthorized() throws Exception {
        String accessToken = this.obtainAccessToken("louis", "louis");

        // @formatter:off
        mockMvc.perform(post("/api/accounts/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(CONTENT_TYPE)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isOk());

        mockMvc.perform(get("/management/env")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(CONTENT_TYPE)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isUnauthorized());
        // @formatter:on
    }

    @Test
    public void accessResourceAfterRefreshAccessTokenThenOk() throws Exception {
        Pair<String, String> tokenPair = this.obtainTokenPair("louis", "louis");
        String originalAccessToken = tokenPair.getFirst();
        String originalRefreshToken = tokenPair.getSecond();

        String newAccessToken = refreshAccessToken(originalRefreshToken);

        // @formatter:off
        mockMvc.perform(get("/management/env")
                        .header("Authorization", "Bearer " + originalAccessToken)
                        .contentType(CONTENT_TYPE)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isUnauthorized());
        // @formatter:on

        // @formatter:off
        mockMvc.perform(get("/management/env")
                        .header("Authorization", "Bearer " + newAccessToken)
                        .contentType(CONTENT_TYPE)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isOk());
        // @formatter:on
    }
}