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

    public TeamUser(String teamId, String userId) {
        this.teamId = teamId;
        this.userId = userId;
    }

}
