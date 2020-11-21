package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.infinity.passport.domain.MongoOAuth2AuthorizationCode;

@ApiModel("单点登录授权码信息DTO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MongoOAuth2AuthorizationCodeDTO extends MongoOAuth2AuthorizationCode {

    private static final long serialVersionUID = 1L;

}
