package org.infinity.passport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Schema(description = "管理系统菜单列表DTO")
@Data
public class AdminAuthorityMenusDTO implements Serializable {

    private static final long serialVersionUID = -3119877507448443380L;

    @Schema(description = "应用名称")
    @NotNull
    private String appName;

    @Schema(description = "权限名")
    @NotNull
    private String authorityName;

    @Schema(description = "管理系统菜单IDs")
    private List<String> adminMenuIds;

}
