package org.infinity.passport.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.infinity.passport.dto.AppDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the App entity.
 */
@ApiModel("应用")
@Document(collection = "App")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class App implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "应用名称", required = true)
    @NotNull
    @Size(min = 3, max = 16)
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "{EP5903}")
    @Id
    private String name;

    @ApiModelProperty(value = "是否可用")
    private Boolean enabled;

    public AppDTO toDTO() {
        return new AppDTO(this.getName(), this.getEnabled());
    }

    public static App of(AppDTO dto) {
        return new App(dto.getName(), dto.getEnabled());
    }
}
