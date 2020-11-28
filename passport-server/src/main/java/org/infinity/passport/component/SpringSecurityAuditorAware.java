package org.infinity.passport.component;

import java.util.Optional;

import org.infinity.passport.domain.Authority;
import org.infinity.passport.utils.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    @Nonnull
    public Optional<String> getCurrentAuditor() {
        String userName = SecurityUtils.getCurrentUserName();
        return Optional.of(userName != null ? userName : Authority.SYSTEM_ACCOUNT);
    }
}
