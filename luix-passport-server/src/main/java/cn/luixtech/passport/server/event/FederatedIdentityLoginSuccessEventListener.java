package cn.luixtech.passport.server.event;

import cn.luixtech.passport.server.config.oauth.AuthUser;
import cn.luixtech.passport.server.config.oauth.HybridAuthenticationToken;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.UserService;
import com.luixtech.uidgenerator.core.id.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.function.BiConsumer;

@Slf4j
public class FederatedIdentityLoginSuccessEventListener implements BiConsumer<OAuth2User, Authentication> {

    private final UserRepository     userRepository;
    private final UserDetailsService userDetailsService;
    private final UserService        userService;

    public FederatedIdentityLoginSuccessEventListener(UserRepository userRepository,
                                                      UserDetailsService userDetailsService,
                                                      UserService userService) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @Override
    public void accept(OAuth2User oAuth2User, Authentication authentication) {
        log.info("Federated identity logged in successfully for user: {}", oAuth2User);
        String clientRegistrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        // email from the 3rd party provider
        String email = oAuth2User.getAttribute("email");
        HybridAuthenticationToken newAuthentication;

        if (userRepository.countByEmail(email) > 0) {
            // query existing user
            UserDetails existingUser = this.userDetailsService.loadUserByUsername(email);
            // create a new OAuth2AuthenticationToken and merge authorities
            newAuthentication = new HybridAuthenticationToken(
                    oAuth2User,
                    (AuthUser) existingUser,
                    clientRegistrationId
            );
        } else {
            // create a new user automatically
            String username = oAuth2User.getName();
            if (userRepository.findById(oAuth2User.getName()).isPresent()) {
                // if the username already exists, generate a random username
                username = "U" + IdGenerator.generateShortId();
            }
            userService.insertThirdPartyUser(username, email, clientRegistrationId);
            UserDetails newUser = this.userDetailsService.loadUserByUsername(email);
            // create a new OAuth2AuthenticationToken and merge authorities
            newAuthentication = new HybridAuthenticationToken(
                    oAuth2User,
                    (AuthUser) newUser,
                    clientRegistrationId
            );

            // 方案2: 跳转注册页面，让用户完善信息
            // response.sendRedirect("/register?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8));
        }

        // Set user in security context
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }
}
