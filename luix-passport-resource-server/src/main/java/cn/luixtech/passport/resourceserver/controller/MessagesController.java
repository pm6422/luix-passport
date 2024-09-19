package cn.luixtech.passport.resourceserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessagesController {

    @GetMapping("/api/messages")
    public String[] getMessages() {
        return new String[]{"Resource Server Message 1", "Resource Server Message 2", "Resource Server Message 3"};
    }
}
