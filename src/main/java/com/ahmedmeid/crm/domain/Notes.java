package com.ahmedmeid.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Notes.
 */
@Table("notes")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Notes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("note_timestamp")
    private Instant noteTimestamp;

    @Column("note")
    private String note;

    @Transient
    @JsonIgnoreProperties(value = { "interactions", "notes", "org", "emps" }, allowSetters = true)
    private Contact contact;

    @Column("contact_id")
    private Long contactId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Notes id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getNoteTimestamp() {
        return this.noteTimestamp;
    }

    public Notes noteTimestamp(Instant noteTimestamp) {
        this.setNoteTimestamp(noteTimestamp);
        return this;
    }

    public void setNoteTimestamp(Instant noteTimestamp) {
        this.noteTimestamp = noteTimestamp;
    }

    public String getNote() {
        return this.note;
    }

    public Notes note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Contact getContact() {
        return this.contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        this.contactId = contact != null ? contact.getId() : null;
    }

    public Notes contact(Contact contact) {
        this.setContact(contact);
        return this;
    }

    public Long getContactId() {
        return this.contactId;
    }

    public void setContactId(Long contact) {
        this.contactId = contact;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notes)) {
            return false;
        }
        return getId() != null && getId().equals(((Notes) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notes{" +
            "id=" + getId() +
            ", noteTimestamp='" + getNoteTimestamp() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
