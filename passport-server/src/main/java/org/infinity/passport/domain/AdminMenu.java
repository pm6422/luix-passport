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

    private static final long   serialVersionUID = 1L;
    public static final  String ROOT_PATH        = "ROOT";
    public static final  String FIELD_LEVEL      = "level";
    public static final  String FIELD_SEQUENCE   = "sequence";

    // 主键不要定义为Long型，因为定义为Long型的字段如果超过16位的话在前端页面O会显示为0
    @Id
    private String  id;
    @NotNull
    @Size(min = 1, max = 20)
    @Indexed
    private String  appName;
    @NotNull
    @Size(min = 1, max = 50)
    private String  name;
    private String  label;
    @Field(FIELD_LEVEL)
    private Integer level;
    private String  url;
    @Field(FIELD_SEQUENCE)
    private Integer sequence;
    private String  parentId;
    private String  parentNamePath;

    public AdminMenu() {
        super();
    }

    /**
     * Constructor for creating operation
     *
     * @param appName
     * @param name
     * @param label
     * @param level
     * @param url
     * @param sequence
     * @param parentMenuId
     * @param parentNamePath
     */
    public AdminMenu(String appName, String name, String label, Integer level, String url,
                     Integer sequence, String parentMenuId, String parentNamePath) {
        super();
        this.appName = appName;
        this.name = name;
        this.label = label;
        this.level = level;
        this.url = url;
        this.sequence = sequence;
        this.parentId = parentMenuId;
        this.parentNamePath = parentNamePath;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    public String getParentNamePath() {
        return parentNamePath;
    }

    public void setParentNamePath(String parentNamePath) {
        this.parentNamePath = parentNamePath;
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
                && (this.getName() == null ? other.getName() == null
                : this.getName().equals(other.getName()))
                && (this.getLabel() == null ? other.getLabel() == null
                : this.getLabel().equals(other.getLabel()))
                && (this.getLevel() == null ? other.getLevel() == null : this.getLevel().equals(other.getLevel()))
                && (this.getUrl() == null ? other.getUrl() == null : this.getUrl().equals(other.getUrl()))
                && (this.getSequence() == null ? other.getSequence() == null
                : this.getSequence().equals(other.getSequence()))
                && (this.getParentId() == null ? other.getParentId() == null
                : this.getParentId().equals(other.getParentId()))
                && (this.getParentNamePath() == null ? other.getParentNamePath() == null
                : this.getParentNamePath().equals(other.getParentNamePath()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAppName() == null) ? 0 : getAppName().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getLabel() == null) ? 0 : getLabel().hashCode());
        result = prime * result + ((getLevel() == null) ? 0 : getLevel().hashCode());
        result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
        result = prime * result + ((getSequence() == null) ? 0 : getSequence().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getParentNamePath() == null) ? 0 : getParentNamePath().hashCode());
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