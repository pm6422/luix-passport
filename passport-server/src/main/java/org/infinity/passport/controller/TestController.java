package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.service.MailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Tag(name = "测试")
@Slf4j
public class TestController {
    @Resource
    private MailService mailService;

    @Operation(summary = "测试发送邮件功能")
    @GetMapping("/api/tests/email")
    public void testEmail() {
        mailService.sendEmail(new String[]{"louis@luixtech.com"}, "test", "test", false, false);
    }
}
