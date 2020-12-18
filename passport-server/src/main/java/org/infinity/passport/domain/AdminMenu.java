package org.infinity.passport.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.infinity.passport.dto.AdminMenuTreeDTO;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the AdminMenu entity.
 */
@ApiModel("管理系统菜单")
@Document(collection = "AdminMenu")
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class AdminMenu extends AbstractAuditableDomain implements Serializable {
    private static final long   serialVersionUID = 1L;
    public static final  String FIELD_LEVEL      = "level";
    public static final  String FIELD_SEQUENCE   = "sequence";

    @ApiModelProperty("应用名称")
    @NotNull
    @Size(min = 1, max = 20)
    @Indexed
    private String appName;

    @ApiModelProperty("管理菜单名")
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @ApiModelProperty("管理菜单显示文本")
    @Size(min = 1, max = 100)
    private String label;

    @ApiModelProperty("菜单层级")
    private Integer level;

    @ApiModelProperty("菜单链接地址")
    @Size(max = 4000)
    private String url;

    @ApiModelProperty("菜单排序序号")
    @Min(1)
    @Max(999)
    private Integer sequence;

    @ApiModelProperty("父菜单ID")
    private String parentId;

    @ApiModelProperty("是否选中")
    @Transient
    private Boolean checked;

    public AdminMenu(@NotNull @Size(min = 1, max = 20) String appName) {
        this.appName = appName;
    }

    public AdminMenu(String appName, String name, String label, Integer level, String url,
                     Integer sequence, String parentId) {
        super();
        this.appName = appName;
        this.name = name;
        this.label = label;
        this.level = level;
        this.url = url;
        this.sequence = sequence;
        this.parentId = parentId;
    }

    public AdminMenuTreeDTO toTreeDTO() {
        AdminMenuTreeDTO dto = new AdminMenuTreeDTO();
        BeanCopier beanCopier = BeanCopier.create(AdminMenu.class, AdminMenuTreeDTO.class, false);
        beanCopier.copy(this, dto, null);
        return dto;
    }
}