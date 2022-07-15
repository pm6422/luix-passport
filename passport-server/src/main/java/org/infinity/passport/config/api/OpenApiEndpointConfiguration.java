package org.infinity.passport.config.api;

import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty("springdoc.api-docs.enabled")
public class OpenApiEndpointConfiguration {

    @Value("${spring.application.name}")
    private String appName;

    /**
     * <p>OpenApiEndpoint</p>
     *
     * @param springDocConfigProperties a {@link SpringDocConfigProperties} object.
     * @return a {@link OpenApiEndpoint} object.
     */
    @Bean
    @ConditionalOnAvailableEndpoint
    public OpenApiEndpoint openApiEndpoint(SpringDocConfigProperties springDocConfigProperties) {
        return new OpenApiEndpoint(springDocConfigProperties, appName);
    }
}