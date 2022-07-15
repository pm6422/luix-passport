package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.User;
import org.infinity.passport.repository.UserRepository;
import org.infinity.passport.service.MailService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Tag(name = "测试")
@Slf4j
public class TestController {

    @Resource
    private UserRepository userRepository;
    @Resource
    private MailService    mailService;

    @Operation(description = "测试审计功能")
    @GetMapping("/api/tests/user-audit")
    @Secured(Authority.DEVELOPER)
    public void testAudit() {
        User user1 = new User();
        user1.setUserName("test");
        user1.setFirstName("");
        user1.setLastName("Test");
        user1.setEmail("test@localhost");
        user1.setMobileNo("16000899479");
        // Raw password: user
        user1.setPasswordHash("$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K");
        user1.setActivated(true);
        user1.setActivationKey(null);
        user1.setResetKey(null);
        user1.setResetTime(null);
        user1.setEnabled(true);

        User result = userRepository.save(user1);
        log.debug(user1.toString());

        result.setEnabled(false);
        result.setModifiedBy("hello");// Current account will override this value
        result = userRepository.save(result);
        log.debug(result.toString());

        userRepository.deleteById(user1.getId());
    }

    @Operation(summary = "测试发送邮件功能")
    @GetMapping("/api/tests/email")
    @Secured(Authority.DEVELOPER)
    public void testEmail() {
        mailService.sendEmail(new String[]{"louis@luixtech.com"}, "test", "test", false, false);
    }
}
