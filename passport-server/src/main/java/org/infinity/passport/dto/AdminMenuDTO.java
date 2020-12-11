package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.infinity.passport.entity.MenuTreeNode;
import org.springframework.cglib.beans.BeanCopier;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;

@ApiModel("管理系统菜单DTO")
@Data
public class AdminMenuDTO extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("管理菜单ID")
    private String  id;
    @ApiModelProperty("应用名称")
    private String  appName;
    @Size(min = 1, max = 100)
    @ApiModelProperty("管理菜单名")
    private String  name;
    @Size(min = 1, max = 100)
    @ApiModelProperty("管理菜单显示文本")
    private String  label;
    @ApiModelProperty("菜单层级")
    private Integer level;
    @Size(max = 4000)
    @ApiModelProperty("菜单链接地址")
    private String  url;
    @Min(1)
    @Max(999)
    @ApiModelProperty("菜单排序序号")
    private Integer sequence;
    @ApiModelProperty("父菜单ID")
    private String  parentId;
    @ApiModelProperty("是否选中")
    private Boolean checked;

    public MenuTreeNode asNode() {
        MenuTreeNode dto = new MenuTreeNode();
        BeanCopier beanCopier = BeanCopier.create(AdminMenuDTO.class, MenuTreeNode.class, false);
        beanCopier.copy(this, dto, null);
        return dto;
    }
}
