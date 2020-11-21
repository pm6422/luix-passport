package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.infinity.passport.domain.MongoOAuth2ClientDetails;

@ApiModel("单点登录客户端信息DTO")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MongoOAuth2ClientDetailsDTO extends MongoOAuth2ClientDetails {

    private static final long serialVersionUID = 1L;

}
