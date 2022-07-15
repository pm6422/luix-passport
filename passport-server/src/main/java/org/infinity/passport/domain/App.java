package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * Spring Data MongoDB collection for the App entity.
 */
@Schema(description = "应用")
@Document(collection = "App")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class App implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "应用名称", required = true)
    @NotNull
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "{EP5903}")
    @Id
    private String name;

    @Schema(description = "是否可用")
    private Boolean enabled;

    @Schema(description = "权限名称")
    @Transient
    private Set<String> authorities;

    public App(String name, Boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }
}
