package cn.luixtech.passport.server.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserLoginCount {
    private Long          loginCount;
    private LocalDateTime calculatedAt;
}
