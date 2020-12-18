package org.infinity.passport.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the AppAuthority entity.
 */
@ApiModel("应用权限")
@Document(collection = "AppAuthority")
@Data
@NoArgsConstructor
public class AppAuthority implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @ApiModelProperty(value = "应用名称")
    @Size(min = 2, max = 20)
    private String appName;

    @ApiModelProperty(value = "权限名称")
    @Size(min = 2, max = 20)
    private String authorityName;

    public AppAuthority(String appName, String authorityName) {
        this.appName = appName;
        this.authorityName = authorityName;
    }
}
