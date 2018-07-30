package org.infinity.passport.dto;

import org.infinity.passport.domain.MongoOAuth2RefreshToken;

import io.swagger.annotations.ApiModel;

@ApiModel("刷新令牌信息DTO")
public class MongoOAuth2RefreshTokenDTO extends MongoOAuth2RefreshToken {

    private static final long serialVersionUID = 1L;

}
