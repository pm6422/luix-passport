package org.infinity.passport.dto;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.infinity.passport.domain.base.AbstractAuditableDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("管理系统菜单DTO")
public class AdminMenuDTO extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("管理菜单ID")
    private String            id;

    @ApiModelProperty("应用名称")
    private String            appName;

    @Size(min = 1, max = 100)
    @ApiModelProperty("管理菜单名")
    private String            adminMenuName;

    @Size(min = 1, max = 100)
    @ApiModelProperty("管理菜单中文文本")
    private String            adminMenuChineseText;

    @ApiModelProperty("菜单层级")
    private Integer           level;

    @Size(max = 4000)
    @ApiModelProperty("菜单链接地址")
    private String            link;

    @Min(1)
    @Max(999)
    @ApiModelProperty("菜单排序序号")
    private Integer           sequence;

    @ApiModelProperty("父菜单ID")
    private String            parentMenuId;

    @ApiModelProperty("是否选中")
    private boolean           checked;

    public AdminMenuDTO() {
    }

    public AdminMenuDTO(String id, String appName, String adminMenuName, String adminMenuChineseText, Integer level,
            String link, Integer sequence, String parentMenuId, boolean checked) {
        super();
        this.id = id;
        this.appName = appName;
        this.adminMenuName = adminMenuName;
        this.adminMenuChineseText = adminMenuChineseText;
        this.level = level;
        this.link = link;
        this.sequence = sequence;
        this.parentMenuId = parentMenuId;
        this.checked = checked;
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

    public String getAdminMenuName() {
        return adminMenuName;
    }

    public void setAdminMenuName(String adminMenuName) {
        this.adminMenuName = adminMenuName;
    }

    public String getAdminMenuChineseText() {
        return adminMenuChineseText;
    }

    public void setAdminMenuChineseText(String adminMenuChineseText) {
        this.adminMenuChineseText = adminMenuChineseText;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getParentMenuId() {
        return parentMenuId;
    }

    public void setParentMenuId(String parentMenuId) {
        this.parentMenuId = parentMenuId;
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

    @Override
    public String toString() {
        return "AdminMenuDTO [id=" + id + ", adminMenuName=" + adminMenuName + ", adminMenuChineseText="
                + adminMenuChineseText + ", level=" + level + ", link=" + link + ", sequence=" + sequence
                + ", parentMenuId=" + parentMenuId + ", checked=" + checked + "]";
    }
}
