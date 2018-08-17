package org.infinity.passport.domain;

import org.infinity.passport.dto.AppDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the App entity.
 */
@Document(collection = "App")
public class App implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Size(min = 0, max = 50)
    private String            name;

    private Boolean           enabled;

    public App(String name, Boolean enabled) {
        super();
        this.name = name;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        App other = (App) that;
        return (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getEnabled() == null ? other.getEnabled() == null
                        : this.getEnabled().equals(other.getEnabled()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
        return result;
    }

    public AppDTO asDTO() {
        return new AppDTO(this.getName(), this.getEnabled());
    }

    public static App of(AppDTO dto) {
        App target = new App(dto.getName(), dto.getEnabled());
        return target;
    }
}
