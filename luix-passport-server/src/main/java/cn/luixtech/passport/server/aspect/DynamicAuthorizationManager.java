package cn.luixtech.passport.server.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Refer to <a href=
 * "https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html">Using
 * a Custom Authorization Manager</a>
 */
@Component
@Deprecated
@Slf4j
public class DynamicAuthorizationManager implements AuthorizationManager<MethodInvocation> {
    @Override
    @Deprecated
    public AuthorizationDecision authorize(Supplier<? extends Authentication> authentication,
            MethodInvocation invocation) {
        return new AuthorizationDecision(true);
    }
}
