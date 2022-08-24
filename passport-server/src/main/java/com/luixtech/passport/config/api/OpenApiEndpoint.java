package com.luixtech.passport.config.api;

import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springdoc.core.Constants.DEFAULT_GROUP_NAME;

@WebEndpoint(id = "openapigroups")
public class OpenApiEndpoint {
    private final SpringDocConfigProperties springDocConfigProperties;
    private final String                    appName;

    public OpenApiEndpoint(SpringDocConfigProperties springDocConfigProperties, String appName) {
        this.springDocConfigProperties = springDocConfigProperties;
        this.appName = appName;
    }

    /**
     * GET /management/openapigroups
     * <p>
     * Give openApi displayed on OpenApi page
     *
     * @return a Map with a String defining a category of openApi as Key and
     * another Map containing openApi related to this category as Value
     */
    @ReadOperation
    public List<Map<String, String>> allOpenApi() {
        return this.springDocConfigProperties.getGroupConfigs().stream().map(this::createGroupMap).collect(Collectors.toList());
    }

    private Map<String, String> createGroupMap(SpringDocConfigProperties.GroupConfig group) {
        String groupName = group.getGroup();
        Map<String, String> map = new HashMap<>(2);
        map.put("group", groupName);
        String description = this.appName + "(" + (DEFAULT_GROUP_NAME.equals(groupName) ? "default" : groupName) + ")";
        map.put("description", description);
        return map;
    }
}