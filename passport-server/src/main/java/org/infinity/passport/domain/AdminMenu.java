package org.infinity.passport.domain;

import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.infinity.passport.dto.AdminMenuDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the AdminMenu entity.
 */
@Document(collection = "AdminMenu")
public class AdminMenu extends AbstractAuditableDomain implements Serializable {

    private static final long  serialVersionUID = 1L;

    public static final String FIELD_LEVEL      = "level";

    public static final String FIELD_SEQUENCE = "sequence";

    // 主键不要定义为Long型，因为定义为Long型的字段如果超过16位的话在前端页面O会显示为0
    @Id
    private String             id;

    @NotNull
    @Size(min = 1, max = 20)
    @Indexed
    private String             appName;

    @NotNull
    @Size(min = 1, max = 50)
    private String             adminMenuName;

    private String             adminMenuChineseText;

    @Field(FIELD_LEVEL)
    private Integer level;

    private String             link;

    @Field(FIELD_SEQUENCE)
    private Integer sequence;

    private String             parentMenuId;

    public AdminMenu() {
        super();
    }

    /**
     * Constructor for creating operation
     * @param appName
     * @param adminMenuName
     * @param adminMenuChineseText
     * @param level
     * @param link
     * @param sequence
     * @param parentMenuId
     */
    public AdminMenu(String appName, String adminMenuName, String adminMenuChineseText, Integer level, String link,
            Integer sequence, String parentMenuId) {
        super();
        this.appName = appName;
        this.adminMenuName = adminMenuName;
        this.adminMenuChineseText = adminMenuChineseText;
        this.level = level;
        this.link = link;
        this.sequence = sequence;
        this.parentMenuId = parentMenuId;
    }

    public String getId() {
        return id;
    }

    public void setId(String adminMenuId) {
        this.id = adminMenuId;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        AdminMenu other = (AdminMenu) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getAppName() == null ? other.getAppName() == null
                        : this.getAppName().equals(other.getAppName()))
                && (this.getAdminMenuName() == null ? other.getAdminMenuName() == null
                        : this.getAdminMenuName().equals(other.getAdminMenuName()))
                && (this.getAdminMenuChineseText() == null ? other.getAdminMenuChineseText() == null
                        : this.getAdminMenuChineseText().equals(other.getAdminMenuChineseText()))
                && (this.getLevel() == null ? other.getLevel() == null : this.getLevel().equals(other.getLevel()))
                && (this.getLink() == null ? other.getLink() == null : this.getLink().equals(other.getLink()))
                && (this.getSequence() == null ? other.getSequence() == null
                        : this.getSequence().equals(other.getSequence()))
                && (this.getParentMenuId() == null ? other.getParentMenuId() == null
                        : this.getParentMenuId().equals(other.getParentMenuId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAppName() == null) ? 0 : getAppName().hashCode());
        result = prime * result + ((getAdminMenuName() == null) ? 0 : getAdminMenuName().hashCode());
        result = prime * result + ((getAdminMenuChineseText() == null) ? 0 : getAdminMenuChineseText().hashCode());
        result = prime * result + ((getLevel() == null) ? 0 : getLevel().hashCode());
        result = prime * result + ((getLink() == null) ? 0 : getLink().hashCode());
        result = prime * result + ((getSequence() == null) ? 0 : getSequence().hashCode());
        result = prime * result + ((getParentMenuId() == null) ? 0 : getParentMenuId().hashCode());
        return result;
    }

    public AdminMenuDTO asDTO() {
        AdminMenuDTO dto = new AdminMenuDTO();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }

    public static AdminMenu of(AdminMenuDTO dto) {
        AdminMenu dest = new AdminMenu();
        BeanUtils.copyProperties(dto, dest);
        return dest;
    }
}