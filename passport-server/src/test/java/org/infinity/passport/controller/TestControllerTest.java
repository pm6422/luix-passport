package org.infinity.passport.controller;

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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    public void testThreadSafe() {
        for (int i = 0; i < 1000; i++) {
            String key = UUID.randomUUID().toString().replaceAll("-", "");
            new Thread(() -> {
                try {
                    this.mockMvc.perform(get("/open-api/test/threadsafe?key=" + key).accept(MediaType.APPLICATION_JSON));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
