package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.infinity.passport.entity.MenuTreeNode;
import org.springframework.cglib.beans.BeanCopier;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;

@ApiModel("管理系统菜单DTO")
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
    private boolean checked;

    public AdminMenuDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public MenuTreeNode asNode() {
        MenuTreeNode dto = new MenuTreeNode();
        BeanCopier beanCopier = BeanCopier.create(AdminMenuDTO.class, MenuTreeNode.class, false);
        beanCopier.copy(this, dto, null);
        return dto;
    }

    @Override
    public String toString() {
        return "AdminMenuDTO{" +
                "id='" + id + '\'' +
                ", appName='" + appName + '\'' +
                ", name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", level=" + level +
                ", url='" + url + '\'' +
                ", sequence=" + sequence +
                ", parentId='" + parentId + '\'' +
                ", checked=" + checked +
                '}';
    }
}
