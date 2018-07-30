package org.infinity.passport.oauth2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.infinity.passport.PassportLauncher;
import org.infinity.passport.config.ApplicationConstants;
import org.infinity.passport.domain.MongoOAuth2ClientDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PassportLauncher.class)
@ActiveProfiles(ApplicationConstants.SPRING_PROFILE_TEST)
public class OAuth2ClientCredentialsTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy      springSecurityFilterChain;

    private MockMvc               mockMvc;

    private static final String   CLIENT_ID         = MongoOAuth2ClientDetails.INTERNAL_CLIENT_ID;
    private static final String   RAW_CLIENT_SECRET = MongoOAuth2ClientDetails.INTERNAL_RAW_CLIENT_SECRET;
    private static final String   CONTENT_TYPE      = "application/json;charset=UTF-8";

    /**
    * The constructor will be executed first before the spring boot starting.
    */
    public OAuth2ClientCredentialsTest() {
        super();
    }

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
    }

    private ResultActions obtainToken(String username, String password) throws Exception {
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
    
    private String obtainAccessToken(String username, String password) throws Exception {
        ResultActions result = obtainToken(username, password);
        // @formatter:on
        String resultString = result.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    @Test
    public void accessResourceWhenUseLowerAuthorityAccessTokenThenOk() throws Exception {
        String accessToken = this.obtainAccessToken("louis", "louis");

        // @formatter:off
        ResultActions result = mockMvc.perform(get("/api/account/authority-names")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE))
                .andExpect(status().isOk());
        // @formatter:on

        String resultString = result.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        List<String> resultObj = mapper.readValue(resultString, ArrayList.class);
        assertThat(resultObj).isNotEmpty();

//     // @formatter:off
//        mockMvc.perform(get("/management/env")
//                .header("Authorization", "Bearer " + accessToken)
//                .contentType(CONTENT_TYPE)
//                .accept(CONTENT_TYPE))
//                .andExpect(status().isForbidden());
//        // @formatter:on
        //
//     // @formatter:off
//         mockMvc.perform(get("/api/app/apps/all")
//                .header("Authorization", "Bearer " + accessToken)
//                .contentType(CONTENT_TYPE)
//                .accept(CONTENT_TYPE))
//                .andExpect(status().isForbidden());
//        // @formatter:on
    }
}