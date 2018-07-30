package org.infinity.passport.dto;

import org.infinity.passport.domain.MongoOAuth2ClientDetails;

import io.swagger.annotations.ApiModel;

@ApiModel("单点登录客户端信息DTO")
public class MongoOAuth2ClientDetailsDTO extends MongoOAuth2ClientDetails {

    private static final long serialVersionUID = 1L;

}
