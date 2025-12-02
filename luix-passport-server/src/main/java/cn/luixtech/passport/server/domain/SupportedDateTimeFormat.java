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
public class SupportedDateTimeFormat {

    @Id
    @GeneratedValue
    @TsidGenerator
    @Column(length = 19)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "displayName:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String displayName;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "dateTimeFormat:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String dateTimeFormat;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "dateFormat:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String dateFormat;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "timeFormat:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String timeFormat;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "example:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String example;

    private Boolean preset;
}
