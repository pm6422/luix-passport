package org.infinity.passport.component;

import org.infinity.passport.domain.Authority;
import org.infinity.passport.config.oauth2.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    @Nonnull
    public Optional<String> getCurrentAuditor() {
        String username = SecurityUtils.getCurrentUsername();
        return Optional.of(username != null ? username : Authority.SYSTEM_ACCOUNT);
    }
}
