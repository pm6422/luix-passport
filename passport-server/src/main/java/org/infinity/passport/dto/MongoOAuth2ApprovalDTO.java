package org.infinity.passport.dto;

import org.infinity.passport.domain.MongoOAuth2Approval;

import io.swagger.annotations.ApiModel;

@ApiModel("单点登录授权信息DTO")
public class MongoOAuth2ApprovalDTO extends MongoOAuth2Approval {

    private static final long serialVersionUID = 1L;

}
