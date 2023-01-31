package com.luixtech.passport.controller;

import com.luixtech.passport.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class TestController {
    private final MailService mailService;

    @Operation(summary = "test to send mail")
    @GetMapping("/api/tests/email")
    public void testEmail() {
        mailService.sendEmail(new String[]{"louis@luixtech.com"}, "test", "test", false, false);
    }
}
