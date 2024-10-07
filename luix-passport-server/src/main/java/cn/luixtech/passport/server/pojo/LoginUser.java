package cn.luixtech.passport.server.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class LoginUser {
    private String        id;
    private String        username;
    private String        firstName;
    private String        lastName;
    private String        email;
    private LocalDateTime signInAt;
}
