package org.tdl.vireo.model;

import java.util.List;

import static javax.persistence.FetchType.EAGER;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class FieldType extends BaseEntity {

    private String name;

    @ManyToOne(fetch = EAGER)
    private List<InputType> inputTypes;
    
    public FieldType() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<InputType> getInputTypes() {
        return inputTypes;
    }

    public void setInputTypes(List<InputType> inputTypes) {
        this.inputTypes = inputTypes;
    }
}
