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
public class SchedulerLock {

    @Id
    @GeneratedValue
    @TsidGenerator
    @Column(length = 19)
    private String id;

    private Instant lockedAt;
    private Instant lockUntil;
    private String lockedBy;
}
