package cn.luixtech.passport.resourceserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RestController
public class MessagesController {

    @GetMapping("/api/resource-server-messages")
    public List<String> getResourceServerMessages(Principal principal) {
        return Arrays.asList("authorization code mode can get user info: " + principal.getName(), "Resource Server Message 1", "Resource Server Message 2", "Resource Server Message 3");
    }
}
