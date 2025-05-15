package cn.luixtech.passport.server.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginCount {
    private Long   loginCount;
    private String calculatedAt;
}
