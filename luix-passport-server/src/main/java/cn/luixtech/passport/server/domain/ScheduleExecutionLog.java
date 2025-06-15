package cn.luixtech.passport.server.domain;

import com.luixtech.uidgenerator.core.id.IdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleExecutionLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILURE = "FAILURE";

    @Id
    protected String  id;
    private   String  scheduleName;
    private   Instant startAt;
    private   Instant endAt;
    private   Long    durationMs;
    private   String  status;
    private   String  node;
    @Lob
    private   String  parameters;
    @Lob
    private   String  error;

    @PrePersist
    public void prePersist() {
        if (StringUtils.isEmpty(id)) {
            id = "S" + IdGenerator.generateShortId();
        }
    }
}