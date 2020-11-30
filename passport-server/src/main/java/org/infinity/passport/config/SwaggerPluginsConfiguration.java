package org.infinity.passport.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Register Springfox plugins.
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnBean(Docket.class)
@AutoConfigureAfter(SwaggerConfiguration.class)
public class SwaggerPluginsConfiguration {

    @Configuration
    @ConditionalOnClass(Pageable.class)
    public static class SpringPagePluginConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(prefix = "application.swagger", value = "enabled", havingValue = "true")
        public PageableParameterBuilderPlugin pageableParameterBuilderPlugin(TypeNameExtractor typeNameExtractor,
                                                                             TypeResolver typeResolver) {
            return new PageableParameterBuilderPlugin(typeNameExtractor, typeResolver);
        }
    }
}