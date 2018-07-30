package org.infinity.passport.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.infinity.passport.entity.IHanlpDictionary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Spring Data MongoDB collection for the Hanlp PersonName entity.
 */
@Document
public class HanlpPersonName implements Serializable, IHanlpDictionary {

    private static final long serialVersionUID = 1L;

    @Id
    private String            id;

    @NotNull
    private String            name;

    @NotNull
    private String            pos;

    private Integer           frequency;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toDictionaryString() {
        return name + " " + pos + " " + frequency;
    }
}
