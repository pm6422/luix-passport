package org.infinity.passport.config;

import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.entity.RequesterIdHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.infinity.passport.config.ApplicationConstants.GLOBAL_HEADER_REQUESTER_ID;

@Configuration
public class GlobalRequestParamsConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GlobalRequestParamsInterceptor());
    }

    static class GlobalRequestParamsInterceptor extends HandlerInterceptorAdapter {
        @Override
        public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
            if (handler instanceof HandlerMethod) {
                String requesterIdHeader = request.getHeader(GLOBAL_HEADER_REQUESTER_ID);
                RequesterIdHolder.setRequesterId(StringUtils.defaultString(requesterIdHeader));
//                if () {
//                    // access deny
//                    return false;
//                }
            }
            // Proceed
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            super.afterCompletion(request, response, handler, ex);
            RequesterIdHolder.destroy();
        }
    }
}
