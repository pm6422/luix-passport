package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.infinity.passport.domain.MongoOAuth2Approval;

@ApiModel("单点登录授权信息DTO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MongoOAuth2ApprovalDTO extends MongoOAuth2Approval {

    private static final long serialVersionUID = 1L;

}
