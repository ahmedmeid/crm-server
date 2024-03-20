package com.ahmedmeid.crm.domain;

import com.ahmedmeid.crm.domain.enumeration.InteractionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Interaction.
 */
@Table("interaction")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Interaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("interaction_timestamp")
    private Instant interactionTimestamp;

    @Column("type")
    private InteractionType type;

    @Column("summary")
    private String summary;

    @Transient
    @JsonIgnoreProperties(value = { "interactions", "notes", "org", "emps" }, allowSetters = true)
    private Contact contact;

    @Column("contact_id")
    private Long contactId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Interaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getInteractionTimestamp() {
        return this.interactionTimestamp;
    }

    public Interaction interactionTimestamp(Instant interactionTimestamp) {
        this.setInteractionTimestamp(interactionTimestamp);
        return this;
    }

    public void setInteractionTimestamp(Instant interactionTimestamp) {
        this.interactionTimestamp = interactionTimestamp;
    }

    public InteractionType getType() {
        return this.type;
    }

    public Interaction type(InteractionType type) {
        this.setType(type);
        return this;
    }

    public void setType(InteractionType type) {
        this.type = type;
    }

    public String getSummary() {
        return this.summary;
    }

    public Interaction summary(String summary) {
        this.setSummary(summary);
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Contact getContact() {
        return this.contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        this.contactId = contact != null ? contact.getId() : null;
    }

    public Interaction contact(Contact contact) {
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
        if (!(o instanceof Interaction)) {
            return false;
        }
        return getId() != null && getId().equals(((Interaction) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Interaction{" +
            "id=" + getId() +
            ", interactionTimestamp='" + getInteractionTimestamp() + "'" +
            ", type='" + getType() + "'" +
            ", summary='" + getSummary() + "'" +
            "}";
    }
}
