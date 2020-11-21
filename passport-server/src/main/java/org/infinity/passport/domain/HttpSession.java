package org.infinity.passport.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

/**
 * Http会话
 */
@Document(collection = "HttpSession")
@Data
@EqualsAndHashCode
public class HttpSession implements Serializable {

    private static final long     serialVersionUID = -1L;
    @Id
    protected            String   id;
    @ApiModelProperty("用户名")
    private              String   principal;
    @ApiModelProperty("创建时间")
    private              Instant  created;
    @ApiModelProperty("访问时间")
    private              Instant  accessed;
    @ApiModelProperty("有效期")
    private              Duration interval;
    @ApiModelProperty("失效时间")
    private              Instant  expireAt;

}
