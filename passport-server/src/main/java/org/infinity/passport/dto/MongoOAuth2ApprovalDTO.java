package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.infinity.passport.domain.MongoOAuth2Approval;

@ApiModel("单点登录授权信息DTO")
@Data
public class MongoOAuth2ApprovalDTO extends MongoOAuth2Approval {

    private static final long serialVersionUID = 1L;

}
