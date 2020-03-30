package org.infinity.passport.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "测试")
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @ApiOperation("测试审计功能")
    @GetMapping("/api/test/user-audit")
    @Secured(Authority.DEVELOPER)
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
        user2.setEnabled(true);
        user2.setModifiedBy("AAAA");

        result = userRepository.save(user2);
        LOGGER.debug(user2.toString());

        result.setEnabled(false);
        result = userRepository.save(result);
        LOGGER.debug(result.toString());

        userRepository.deleteById(user2.getId());

        return ResponseEntity.ok(null);
    }

    @ApiOperation("测试发送邮件功能")
    @GetMapping("/api/test/email")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<Void> testEmail() {
        mailService.sendEmail(new String[]{"pm6422@126.com", "pm6422@gmail.com"}, "test", "test", false, false);
        return ResponseEntity.ok(null);
    }


    /**
     * 测试结果显示为线程安全
     *
     * @param key
     * @throws InterruptedException
     */
    @ApiOperation("测试Request线程安全")
    @GetMapping("/open-api/test/threadsafe")
    public void testThreadSafe(@RequestParam(value = "key", required = true) String key) throws InterruptedException {
        Set<String> keys = new HashSet<>();
        if (keys.contains(key)) {
            LOGGER.error("Key {} already existed, request is not threadsafe!", key);
        } else {
            LOGGER.debug(key);
            keys.add(key);
        }

        TimeUnit.MILLISECONDS.sleep(1000);
    }
}
