package org.infinity.passport.config.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.Getter;
import org.infinity.passport.config.ApplicationProperties;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.core.Ordered;

/**
 * A OpenApi customizer to set up {@link OpenAPI}
 */
@Getter
public class OpenApiCustomizer implements OpenApiCustomiser, Ordered {
    private final int                           order = 0;
    private final ApplicationProperties.ApiDocs apiDocsProperties;

    public OpenApiCustomizer(ApplicationProperties.ApiDocs apiDocsProperties) {
        this.apiDocsProperties = apiDocsProperties;
    }

    public void customise(OpenAPI openApi) {
        Contact contact = new Contact()
                .name(apiDocsProperties.getContactName())
                .url(apiDocsProperties.getContactUrl())
                .email(apiDocsProperties.getContactEmail());

        openApi.info(new Info()
                .contact(contact)
                .title(apiDocsProperties.getTitle())
                .description(apiDocsProperties.getDescription())
                .version(apiDocsProperties.getVersion())
                .termsOfService(apiDocsProperties.getTermsOfServiceUrl())
                .license(new License().name(apiDocsProperties.getLicense()).url(apiDocsProperties.getLicenseUrl()))
        );

        for (ApplicationProperties.ApiDocs.Server server : apiDocsProperties.getServers()) {
            openApi.addServersItem(new Server().url(server.getUrl()).description(server.getDescription()));
        }
    }
}