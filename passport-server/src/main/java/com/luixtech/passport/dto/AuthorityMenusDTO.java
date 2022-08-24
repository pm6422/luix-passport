package com.luixtech.passport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Schema(description = "authority menu list DTO")
@Data
public class AuthorityMenusDTO implements Serializable {
    private static final long serialVersionUID = -3119877507448443380L;

    @Schema(description = "application name")
    @NotNull
    private String appName;

    @Schema(description = "authority name")
    @NotNull
    private String authorityName;

    @Schema(description = "menu IDs")
    private List<String> menuIds;

}
