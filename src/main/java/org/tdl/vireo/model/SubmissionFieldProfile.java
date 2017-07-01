package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.tdl.vireo.model.validation.SubmissionFieldProfileValidator;

@Entity
@DiscriminatorValue("Sub")
public class SubmissionFieldProfile extends AbstractFieldProfile<SubmissionFieldProfile> {

    @OneToMany(orphanRemoval=true)
    private List<FieldValue> fieldValues;
    
    public SubmissionFieldProfile() {
        setModelValidator(new SubmissionFieldProfileValidator());
        setOptional(true);
        setFlagged(false);
        setLogged(false);
        setFieldGlosses(new ArrayList<FieldGloss>());
        setControlledVocabularies(new ArrayList<ControlledVocabulary>());
        setFieldType(null);
    }

    public List<FieldValue> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValue(List<FieldValue> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public void addFieldValue(FieldValue fieldValue) {
        this.fieldValues.add(fieldValue);
    }
    
    public void removeFieldValue(FieldValue fieldValue) {
        this.fieldValues.remove(fieldValue);
    }
}
