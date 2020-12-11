package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.infinity.passport.domain.MongoOAuth2AuthorizationCode;

@ApiModel("单点登录授权码信息DTO")
@Data
public class MongoOAuth2AuthorizationCodeDTO extends MongoOAuth2AuthorizationCode {

    private static final long serialVersionUID = 1L;

}
