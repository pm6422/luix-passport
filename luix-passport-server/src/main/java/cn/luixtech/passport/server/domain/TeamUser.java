package cn.luixtech.passport.server.domain;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Column(length = 19)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "teamId:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String teamId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "userId:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String userId;

    public TeamUser(String teamId, String userId) {
        this.teamId = teamId;
        this.userId = userId;
    }
}
