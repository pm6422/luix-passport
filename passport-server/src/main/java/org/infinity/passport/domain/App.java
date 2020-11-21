package org.infinity.passport.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.infinity.passport.dto.AppDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the App entity.
 */
@Document(collection = "App")
@Data
@EqualsAndHashCode
public class App implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Size(max = 50)
    private String name;

    private Boolean enabled;

    public App(String name, Boolean enabled) {
        super();
        this.name = name;
        this.enabled = enabled;
    }

    public AppDTO asDTO() {
        return new AppDTO(this.getName(), this.getEnabled());
    }

    public static App of(AppDTO dto) {
        return new App(dto.getName(), dto.getEnabled());
    }
}
