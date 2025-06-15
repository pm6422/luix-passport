package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractBaseDomain;
import cn.luixtech.passport.server.domain.base.listener.AuditableEntityListener;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@EntityListeners(AuditableEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ScheduleExecutionLog extends AbstractBaseDomain implements Serializable {
    @Serial
    private static final long   serialVersionUID = 1L;
    public static final  String STATUS_RUNNING   = "RUNNING";
    public static final  String STATUS_SUCCESS   = "SUCCESS";
    public static final  String STATUS_FAILURE   = "FAILURE";

    private String  scheduleName;
    private Instant startTime;
    private Instant endTime;
    private Long    durationMs;
    private String  status;
    private String  nodeIp;
    @Lob
    private String  parameters;
    @Lob
    private String  message;
    private String  priority;
}