package org.infinity.passport.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Date;

import static org.infinity.passport.config.ApplicationConstants.GLOBAL_HEADER_REQUESTER_ID;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Springfox Swagger configuration.
 * <p>
 * Warning! When having a lot of REST endpoints, Springfox can become a
 * performance issue. In that case, you can use a specific Spring profile for
 * this class, so that only front-end developers have access to the Swagger
 * view.
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(prefix = "application.swagger", value = "enabled", havingValue = "true")
@Slf4j
public class SwaggerConfiguration {
    private static final String                DEFAULT_API_INCLUDE_PATTERN      = "/api/.*";
    private static final String                DEFAULT_OPEN_API_INCLUDE_PATTERN = "/open-api/.*";
    private final        ApplicationProperties applicationProperties;

    public SwaggerConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public Docket apiDocket() {
        return createDocket("api-group", DEFAULT_API_INCLUDE_PATTERN);
    }

    @Bean
    public Docket openApiDocket() {
        return createDocket("open-api-group", DEFAULT_OPEN_API_INCLUDE_PATTERN);
    }

    private Docket createDocket(String groupName, String path) {
        log.debug("Building Swagger API docket with group [{}]", groupName);
        Docket docket = new Docket(DocumentationType.SWAGGER_2).groupName(groupName).apiInfo(apiInfo())
                .forCodeGeneration(true);
        if (System.getProperty("specified.uri.scheme.host") != null
                && "true".equals(System.getProperty("specified.uri.scheme.host"))) {
            docket.host(applicationProperties.getSwagger().getHost());
        }

        // Add global parameters for all the API
        ParameterBuilder requesterParamBuilder = new ParameterBuilder();
        requesterParamBuilder.name(GLOBAL_HEADER_REQUESTER_ID)
                .description("请求方ID(全局参数)")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build();

//        ParameterBuilder tokenParamBuilder = new ParameterBuilder();
//        tokenParamBuilder.name("x-access-token")
//                .description("令牌")
//                .modelRef(new ModelRef("string"))
//                .parameterType("header")
//                .required(false)
//                .build();

        docket.genericModelSubstitutes(ResponseEntity.class)
                .ignoredParameterTypes(java.sql.Date.class)
                .directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
                .directModelSubstitute(java.time.LocalDateTime.class, Date.class)
                .select()
                .paths(regex(path))
                .build()
                .globalOperationParameters(Arrays.asList(requesterParamBuilder.build()));
        log.debug("Built Swagger API docket with group [{}]", groupName);
        return docket;
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact(applicationProperties.getSwagger().getContactName(),
                null,
                applicationProperties.getSwagger().getContactEmail());

        return new ApiInfoBuilder()
                .title(applicationProperties.getSwagger().getApi().getTitle())
                .description(applicationProperties.getSwagger().getApi().getDescription())
                .version(applicationProperties.getSwagger().getVersion())
                .contact(contact)
                .build();
    }
}
