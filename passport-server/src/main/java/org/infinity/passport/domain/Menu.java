package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.AbstractAuditableDomain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Menu extends AbstractAuditableDomain implements Serializable {
    private static final long   serialVersionUID = 5423774898556939254L;
    public static final  String FIELD_LEVEL      = "level";
    public static final  String FIELD_SEQUENCE   = "sequence";

    @Schema(required = true)
    @NotNull
    @Size(max = 20)
    private String appId;

    @Schema(required = true)
    @NotNull
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[a-z0-9-]+$", message = "{EP5901}")
    @Column(unique = true, length = 30, nullable = false)
    private String code;

    @Schema(required = true)
    @NotNull
    @Size(min = 1, max = 30)
    private String name;

    @Schema(required = true)
    @Min(1)
    @Max(9)
    private Integer depth;

    @Schema(required = true)
    @NotNull
    @Size(min = 3, max = 200)
    private String url;

    @Schema(required = true)
    @Min(1)
    @Max(9999)
    private Integer sequence;

    @Schema
    private String parentId;

    @Schema
    @Transient
    private Boolean checked;

    @Schema
    @Transient
    private List<Menu> children;

    public Menu(String appId, String code, String name, Integer depth, String url,
                Integer sequence, String parentId) {
        super();
        this.appId = appId;
        this.code = code;
        this.name = name;
        this.depth = depth;
        this.url = url;
        this.sequence = sequence;
        this.parentId = parentId;
    }
}