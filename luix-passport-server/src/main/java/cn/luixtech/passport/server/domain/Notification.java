package cn.luixtech.passport.server.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification implements Serializable {
    @Serial
    private static final long   serialVersionUID = 1L;
    public static final  String TYPE_SYSTEM      = "SYSTEM";
    public static final  String TYPE_PERSONAL    = "PERSONAL";

    @Schema(description = "ID")
    @Id
    protected String  id;
    private   String  title;
    private   String  content;
    private   String  type;
    private   String  senderId;
    @Schema(description = "created time")
    @Column(updatable = false)
    private   Instant createdAt;
    @Schema(description = "last modified time")
    private   Instant modifiedAt;


}
