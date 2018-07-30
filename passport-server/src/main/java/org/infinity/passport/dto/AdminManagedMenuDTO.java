package org.infinity.passport.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("管理系统扩展菜单DTO")
public class AdminManagedMenuDTO extends AdminMenuDTO {

    private static final long         serialVersionUID = 1L;

    @ApiModelProperty("菜单子项")
    private List<AdminManagedMenuDTO> subItems;

    public AdminManagedMenuDTO() {
        super();
    }

    public List<AdminManagedMenuDTO> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<AdminManagedMenuDTO> subItems) {
        this.subItems = subItems;
    }

    @Override
    public String toString() {
        return "AdminManagedMenuDTO [subItems=" + subItems + ", getAdminMenuName()=" + getAdminMenuName()
                + ", getAdminMenuChineseText()=" + getAdminMenuChineseText() + ", getLink()=" + getLink()
                + ", getSequence()=" + getSequence() + ", getParentMenuId()=" + getParentMenuId() + ", getLevel()="
                + getLevel() + ", getAdminMenuId()=" + getId() + ", isChecked()=" + isChecked() + "]";
    }
}
