package org.infinity.passport.dto;

import org.infinity.passport.domain.MongoOAuth2AccessToken;

import io.swagger.annotations.ApiModel;

@ApiModel("访问令牌信息DTO")
public class MongoOAuth2AccessTokenDTO extends MongoOAuth2AccessToken {

    private static final long serialVersionUID = 1L;

}
