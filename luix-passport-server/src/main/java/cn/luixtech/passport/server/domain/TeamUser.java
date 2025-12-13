package cn.luixtech.passport.server.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.luixtech.springbootframework.idgenerator.TsidGenerator;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TeamUser {

    public static final String ROLE_OWNER = "OWNER";
    public static final String ROLE_MEMBER = "MEMBER";

    @Id
    @GeneratedValue
    @TsidGenerator
    @Column(length = 18)
    private String id;

    @NotEmpty(message = "teamId:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String teamId;

    @NotEmpty(message = "userId:{Validation.NotEmpty}")
    @Column(nullable = false, name = "username")
    private String userId;

    @NotEmpty(message = "role:{Validation.NotEmpty}")
    @Column(nullable = false, length = 20)
    private String role;

    public TeamUser(String teamId, String userId) {
        this.teamId = teamId;
        this.userId = userId;
        this.role = ROLE_MEMBER;
    }

    public TeamUser(String teamId, String userId, String role) {
        this.teamId = teamId;
        this.userId = userId;
        this.role = role;
    }
}
