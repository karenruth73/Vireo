package org.tdl.vireo.model;

import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class FieldType extends BaseEntity {

    private String name;

    @ManyToOne(cascade = REFRESH, fetch = EAGER, targetEntity = InputType.class)
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
