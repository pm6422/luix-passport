package org.infinity.passport.dto;

import org.infinity.passport.domain.MongoOAuth2AuthorizationCode;

import io.swagger.annotations.ApiModel;

@ApiModel("单点登录授权码信息DTO")
public class MongoOAuth2AuthorizationCodeDTO extends MongoOAuth2AuthorizationCode {

    private static final long serialVersionUID = 1L;

}
