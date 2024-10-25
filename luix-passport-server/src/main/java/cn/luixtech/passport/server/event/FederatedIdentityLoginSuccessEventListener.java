package cn.luixtech.passport.server.event;

import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashSet;
import java.util.function.Consumer;

@Slf4j
public class FederatedIdentityLoginSuccessEventListener implements Consumer<OAuth2User> {

    private final UserRepository userRepository;
    private final UserService    userService;

    public FederatedIdentityLoginSuccessEventListener(UserRepository userRepository,
                                                      UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void accept(OAuth2User oAuth2User) {
        log.info("Federated identity logged in successfully for user: {}", oAuth2User);
        // Capture user in a local data store on first authentication
        if (this.userRepository.findOneByEmail(oAuth2User.getAttribute("email")).isEmpty()) {
            User domain = new User();
            domain.setEmail(oAuth2User.getAttribute("email"));
            domain.setUsername(oAuth2User.getName());
            userService.insert(domain, new HashSet<>(), "", false);
        }
    }
}
