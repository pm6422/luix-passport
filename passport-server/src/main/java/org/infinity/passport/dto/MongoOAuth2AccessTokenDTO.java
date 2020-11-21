package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.infinity.passport.domain.MongoOAuth2AccessToken;

@ApiModel("访问令牌信息DTO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MongoOAuth2AccessTokenDTO extends MongoOAuth2AccessToken {

    private static final long serialVersionUID = 1L;

}
