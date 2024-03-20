package com.ahmedmeid.crm.domain;

import com.ahmedmeid.crm.domain.enumeration.ContactStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Contact.
 */
@Table("contact")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("contact_name")
    private String contactName;

    @Column("job_title")
    private String jobTitle;

    @Column("email_address")
    private String emailAddress;

    @Column("phone_no")
    private String phoneNo;

    @Column("address_number")
    private Integer addressNumber;

    @Column("address_street")
    private String addressStreet;

    @Column("address_city")
    private String addressCity;

    @Column("lead_source")
    private String leadSource;

    @Column("status")
    private ContactStatus status;

    @Transient
    @JsonIgnoreProperties(value = { "contact" }, allowSetters = true)
    private Set<Interaction> interactions = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "contact" }, allowSetters = true)
    private Set<Notes> notes = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "contacts" }, allowSetters = true)
    private Organization org;

    @Transient
    @JsonIgnoreProperties(value = { "contacts", "department" }, allowSetters = true)
    private Set<Employee> emps = new HashSet<>();

    @Column("org_id")
    private Long orgId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Contact id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContactName() {
        return this.contactName;
    }

    public Contact contactName(String contactName) {
        this.setContactName(contactName);
        return this;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getJobTitle() {
        return this.jobTitle;
    }

    public Contact jobTitle(String jobTitle) {
        this.setJobTitle(jobTitle);
        return this;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public Contact emailAddress(String emailAddress) {
        this.setEmailAddress(emailAddress);
        return this;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNo() {
        return this.phoneNo;
    }

    public Contact phoneNo(String phoneNo) {
        this.setPhoneNo(phoneNo);
        return this;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Integer getAddressNumber() {
        return this.addressNumber;
    }

    public Contact addressNumber(Integer addressNumber) {
        this.setAddressNumber(addressNumber);
        return this;
    }

    public void setAddressNumber(Integer addressNumber) {
        this.addressNumber = addressNumber;
    }

    public String getAddressStreet() {
        return this.addressStreet;
    }

    public Contact addressStreet(String addressStreet) {
        this.setAddressStreet(addressStreet);
        return this;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressCity() {
        return this.addressCity;
    }

    public Contact addressCity(String addressCity) {
        this.setAddressCity(addressCity);
        return this;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getLeadSource() {
        return this.leadSource;
    }

    public Contact leadSource(String leadSource) {
        this.setLeadSource(leadSource);
        return this;
    }

    public void setLeadSource(String leadSource) {
        this.leadSource = leadSource;
    }

    public ContactStatus getStatus() {
        return this.status;
    }

    public Contact status(ContactStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ContactStatus status) {
        this.status = status;
    }

    public Set<Interaction> getInteractions() {
        return this.interactions;
    }

    public void setInteractions(Set<Interaction> interactions) {
        if (this.interactions != null) {
            this.interactions.forEach(i -> i.setContact(null));
        }
        if (interactions != null) {
            interactions.forEach(i -> i.setContact(this));
        }
        this.interactions = interactions;
    }

    public Contact interactions(Set<Interaction> interactions) {
        this.setInteractions(interactions);
        return this;
    }

    public Contact addInteraction(Interaction interaction) {
        this.interactions.add(interaction);
        interaction.setContact(this);
        return this;
    }

    public Contact removeInteraction(Interaction interaction) {
        this.interactions.remove(interaction);
        interaction.setContact(null);
        return this;
    }

    public Set<Notes> getNotes() {
        return this.notes;
    }

    public void setNotes(Set<Notes> notes) {
        if (this.notes != null) {
            this.notes.forEach(i -> i.setContact(null));
        }
        if (notes != null) {
            notes.forEach(i -> i.setContact(this));
        }
        this.notes = notes;
    }

    public Contact notes(Set<Notes> notes) {
        this.setNotes(notes);
        return this;
    }

    public Contact addNotes(Notes notes) {
        this.notes.add(notes);
        notes.setContact(this);
        return this;
    }

    public Contact removeNotes(Notes notes) {
        this.notes.remove(notes);
        notes.setContact(null);
        return this;
    }

    public Organization getOrg() {
        return this.org;
    }

    public void setOrg(Organization organization) {
        this.org = organization;
        this.orgId = organization != null ? organization.getId() : null;
    }

    public Contact org(Organization organization) {
        this.setOrg(organization);
        return this;
    }

    public Set<Employee> getEmps() {
        return this.emps;
    }

    public void setEmps(Set<Employee> employees) {
        if (this.emps != null) {
            this.emps.forEach(i -> i.removeContact(this));
        }
        if (employees != null) {
            employees.forEach(i -> i.addContact(this));
        }
        this.emps = employees;
    }

    public Contact emps(Set<Employee> employees) {
        this.setEmps(employees);
        return this;
    }

    public Contact addEmp(Employee employee) {
        this.emps.add(employee);
        employee.getContacts().add(this);
        return this;
    }

    public Contact removeEmp(Employee employee) {
        this.emps.remove(employee);
        employee.getContacts().remove(this);
        return this;
    }

    public Long getOrgId() {
        return this.orgId;
    }

    public void setOrgId(Long organization) {
        this.orgId = organization;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Contact)) {
            return false;
        }
        return getId() != null && getId().equals(((Contact) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Contact{" +
            "id=" + getId() +
            ", contactName='" + getContactName() + "'" +
            ", jobTitle='" + getJobTitle() + "'" +
            ", emailAddress='" + getEmailAddress() + "'" +
            ", phoneNo='" + getPhoneNo() + "'" +
            ", addressNumber=" + getAddressNumber() +
            ", addressStreet='" + getAddressStreet() + "'" +
            ", addressCity='" + getAddressCity() + "'" +
            ", leadSource='" + getLeadSource() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
