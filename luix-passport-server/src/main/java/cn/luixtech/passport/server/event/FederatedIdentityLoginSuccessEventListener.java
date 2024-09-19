package cn.luixtech.passport.server.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class FederatedIdentityLoginSuccessEventListener implements Consumer<OAuth2User> {

    //todo: save to db
    private final UserRepository userRepository = new UserRepository();

    @Override
    public void accept(OAuth2User oAuth2User) {
        log.info("Federated identity logged in successfully for user: {}", oAuth2User);
        // Capture user in a local data store on first authentication
        if (this.userRepository.findByName(oAuth2User.getName()) == null) {
            System.out.println("Saving first-time user: name=" + oAuth2User.getName() + ", " +
                    "claims=" + oAuth2User.getAttributes() + ", authorities=" + oAuth2User.getAuthorities());
            this.userRepository.save(oAuth2User);
        }
    }

    static class UserRepository {

        private final Map<String, OAuth2User> userCache = new ConcurrentHashMap<>();

        public OAuth2User findByName(String name) {
            return this.userCache.get(name);
        }

        public void save(OAuth2User oauth2User) {
            this.userCache.put(oauth2User.getName(), oauth2User);
        }

    }
}
