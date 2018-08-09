package org.infinity.passport.controller;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.User;
import org.infinity.passport.repository.UserRepository;
import org.infinity.passport.service.MailService;
import org.infinity.passport.utils.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "测试")
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private UserRepository      userRepository;

    @Autowired
    private MailService         mailService;

    @ApiOperation("测试审计功能")
    @GetMapping("/api/test/user-audit")
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<Void> testAudit() {
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
        user1.setAvatarImageUrl(null);
        user1.setEnabled(true);

        User result = userRepository.save(user1);
        LOGGER.debug(user1.toString());

        result.setEnabled(false);
        result.setModifiedBy("QQQQQQ");// Current account will override this value
        result = userRepository.save(result);
        LOGGER.debug(result.toString());

        userRepository.deleteById(user1.getId());

        User user2 = new User();
        user2.setId(RandomUtils.generateId());
        user2.setUserName("test");
        user2.setFirstName("");
        user2.setLastName("Test");
        user2.setEmail("test@localhost");
        user2.setMobileNo("16000899479");
        // Raw password: user
        user2.setPasswordHash("$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K");
        user2.setActivated(true);
        user2.setActivationKey(null);
        user2.setResetKey(null);
        user2.setResetTime(null);
        user2.setAvatarImageUrl(null);
        user2.setEnabled(true);
        user2.setModifiedBy("AAAA");

        result = userRepository.save(user2);
        LOGGER.debug(user2.toString());

        result.setEnabled(false);
        result = userRepository.save(result);
        LOGGER.debug(result.toString());

        userRepository.deleteById(user2.getId());

        return ResponseEntity.ok().build();
    }

    @ApiOperation("测试发送邮件功能")
    @GetMapping("/api/test/email")
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<Void> testEmail() {
        mailService.sendEmail("pm6422@126.com", "test", "test", false, false);
        return ResponseEntity.ok().build();
    }
}
