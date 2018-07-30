package org.infinity.passport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import springfox.documentation.staticdocs.Swagger2MarkupResultHandler;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Swagger2MarkupTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc               mockMvc;

    /**
     * The constructor will be executed first before the spring boot starting.
     */
    public Swagger2MarkupTest() {
        super();
    }

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    public void convertApiToAsciiDoc() throws Exception {
        this.mockMvc.perform(get("/v2/api-docs?group=api-group").accept(MediaType.APPLICATION_JSON)).andDo(
                Swagger2MarkupResultHandler.outputDirectory(System.getProperty("api.staticdocs.outputDir")).build())
                .andExpect(status().isOk());
    }

    @Test
    public void convertOpenApiToAsciiDoc() throws Exception {
        this.mockMvc.perform(get("/v2/api-docs?group=open-api-group").accept(MediaType.APPLICATION_JSON))
                .andDo(Swagger2MarkupResultHandler.outputDirectory(System.getProperty("open.api.staticdocs.outputDir"))
                        .build())
                .andExpect(status().isOk());
    }
}