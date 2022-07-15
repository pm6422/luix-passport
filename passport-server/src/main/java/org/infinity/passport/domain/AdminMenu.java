package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

/**
 * Spring Data MongoDB collection for the AdminMenu entity.
 */
@Schema(description = "管理系统菜单")
@Document(collection = "AdminMenu")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class AdminMenu extends AbstractAuditableDomain implements Serializable {

    private static final long   serialVersionUID = 5423774898556939254L;
    public static final  String FIELD_LEVEL      = "level";
    public static final  String FIELD_SEQUENCE   = "sequence";

    @Schema(description = "应用名称", required = true)
    @NotNull
    @Size(min = 1, max = 20)
    @Indexed
    private String appName;

    @Schema(description = "代码", required = true)
    @NotNull
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[a-z0-9-]+$", message = "{EP5901}")
    @Indexed(unique = true)
    private String code;

    @Schema(description = "名称", required = true)
    @NotNull
    @Size(min = 1, max = 30)
    private String name;

    @Schema(description = "层级", required = true)
    @Min(1)
    @Max(9)
    private Integer level;

    @Schema(description = "链接地址", required = true)
    @NotNull
    @Size(min = 3, max = 200)
    private String url;

    @Schema(description = "排序序号", required = true)
    @Min(1)
    @Max(999)
    private Integer sequence;

    @Schema(description = "父菜单ID")
    private String parentId;

    @Schema(description = "是否选中")
    @Transient
    private Boolean checked;

    @Schema(description = "叶子节点")
    @Transient
    private List<AdminMenu> children;

    public AdminMenu(String appName, String code, String name, Integer level, String url,
                     Integer sequence, String parentId) {
        super();
        this.appName = appName;
        this.code = code;
        this.name = name;
        this.level = level;
        this.url = url;
        this.sequence = sequence;
        this.parentId = parentId;
    }

}