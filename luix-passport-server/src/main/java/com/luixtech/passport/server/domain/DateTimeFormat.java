package com.luixtech.passport.server.domain;

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
public class DateTimeFormat {

    @Id
    @GeneratedValue
    @TsidGenerator
    @Column(length = 18)
    private String id;

    @NotEmpty(message = "displayName:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String displayName;

    @NotEmpty(message = "dateTimeFormat:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String dateTimeFormat;

    @NotEmpty(message = "dateFormat:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String dateFormat;

    @NotEmpty(message = "timeFormat:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String timeFormat;

    @NotEmpty(message = "example:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String example;

    private Boolean preset;
}
