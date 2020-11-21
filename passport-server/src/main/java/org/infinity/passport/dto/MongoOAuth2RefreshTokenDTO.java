package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.infinity.passport.domain.MongoOAuth2RefreshToken;

@ApiModel("刷新令牌信息DTO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MongoOAuth2RefreshTokenDTO extends MongoOAuth2RefreshToken {

    private static final long serialVersionUID = 1L;

}
