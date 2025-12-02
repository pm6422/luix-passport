package cn.luixtech.passport.server.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.luixtech.springbootframework.idgenerator.TsidGenerator;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleExecutionLog {

    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILURE = "FAILURE";

    @Id
    @GeneratedValue
    @TsidGenerator
    @Column(length = 19)
    protected String id;
    private String scheduleName;
    private Instant startAt;
    private Instant endAt;
    private Long durationMs;
    private String status;
    private String node;
    @Lob
    private String parameters;
    @Lob
    private String error;
}
