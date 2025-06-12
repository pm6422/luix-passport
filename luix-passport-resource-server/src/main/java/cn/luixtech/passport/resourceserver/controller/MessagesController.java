package cn.luixtech.passport.resourceserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class MessagesController {

    @GetMapping("/api/resource-server-messages")
    public List<String> getResourceServerMessages() {
        return Arrays.asList("Resource Server Message 1", "Resource Server Message 2", "Resource Server Message 3");
    }
}
