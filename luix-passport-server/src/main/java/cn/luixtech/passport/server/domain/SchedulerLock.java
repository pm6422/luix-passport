package cn.luixtech.passport.server.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerLock {

    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;

    private Instant lockedAt;
    private Instant lockUntil;
    private String lockedBy;
}
