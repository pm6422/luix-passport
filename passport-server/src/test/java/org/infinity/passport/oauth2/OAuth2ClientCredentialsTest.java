package org.infinity.passport.oauth2;

import org.infinity.passport.IntegrationTest;
import org.infinity.passport.domain.MongoOAuth2ClientDetails;
import org.junit.jupiter.api.Test;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@IntegrationTest
public class OAuth2ClientCredentialsTest {

    private static final String  CLIENT_ID         = MongoOAuth2ClientDetails.INTERNAL_CLIENT_ID;
    private static final String  RAW_CLIENT_SECRET = MongoOAuth2ClientDetails.INTERNAL_RAW_CLIENT_SECRET;
    private static final String  CONTENT_TYPE      = "application/json;charset=UTF-8";
    @Resource
    private              MockMvc mockMvc;

    private ResultActions obtainToken() throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", RAW_CLIENT_SECRET);
        params.add("grant_type", "client_credentials");

        // @formatter:off
        ResultActions result = mockMvc.perform(post("/oauth/token")
                        .params(params)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE));
        return result;
    }

    private String obtainAccessToken() throws Exception {
        ResultActions result = obtainToken();
        // @formatter:on
        String resultString = result.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    @Test
    public void accessAnyResourceWhenUseClientCredentialsAccessTokenThenOk() throws Exception {
        String accessToken = this.obtainAccessToken();

        // @formatter:off
        ResultActions result = mockMvc.perform(get("/management/env")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(CONTENT_TYPE)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isOk());
        // @formatter:on
    }
}