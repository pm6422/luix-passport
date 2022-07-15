package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the UserProfilePhoto entity.
 */
@Schema(description = "用户照片")
@Document(collection = "UserProfilePhoto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserProfilePhoto extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = -8375847941374800940L;

    @Schema(description = "用户ID", required = true)
    @NotNull
    @Indexed(unique = true)
    private String userId;

    @Schema(description = "用户头像", required = true)
    @NotNull
    private Binary profilePhoto;

}
