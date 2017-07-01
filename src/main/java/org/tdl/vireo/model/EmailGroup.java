package org.tdl.vireo.model;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class EmailGroup extends BaseEntity {

    private String name;
    
    @ElementCollection
    private List<String> emails;
    
    public EmailGroup() {}
    
    public EmailGroup(String name, List<String> emails) {
        setName(name);
        setEmails(emails);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
    
    public void addEmail(String email) {
        this.emails.add(email);
    }
}
