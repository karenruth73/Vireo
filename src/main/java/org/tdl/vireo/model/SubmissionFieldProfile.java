package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.tdl.vireo.model.validation.SubmissionFieldProfileValidator;

@Entity
@DiscriminatorValue("Sub")
public class SubmissionFieldProfile extends AbstractFieldProfile<SubmissionFieldProfile> {

    @OneToMany(cascade = { ALL }, orphanRemoval=true, fetch=EAGER)
    private Set<FieldValue> fieldValues;
    
    public SubmissionFieldProfile() {
        setModelValidator(new SubmissionFieldProfileValidator());
        setOptional(true);
        setFlagged(false);
        setLogged(false);
        setFieldGlosses(new ArrayList<FieldGloss>());
        setControlledVocabularies(new ArrayList<ControlledVocabulary>());
        setFieldType(null);
        setFieldValues(new HashSet<FieldValue>());
    }

    public Set<FieldValue> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Set<FieldValue> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public void addFieldValue(FieldValue fieldValue) {
        this.fieldValues.add(fieldValue);
    }
    
    public void removeFieldValue(FieldValue fieldValue) {
        this.fieldValues.remove(fieldValue);
    }
}
