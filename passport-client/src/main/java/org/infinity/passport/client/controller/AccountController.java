package org.infinity.passport.client.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @GetMapping("/open-api/accounts/user")
    public ResponseEntity<User> getTokenUser() {
        User user = User.builder()
                .username("louis")
                .mobileNo("13800138000")
                .build();
        return ResponseEntity.ok(user);
    }
}
