package com.ahmedmeid.crm.domain;

import static com.ahmedmeid.crm.domain.ContactTestSamples.*;
import static com.ahmedmeid.crm.domain.EmployeeTestSamples.*;
import static com.ahmedmeid.crm.domain.InteractionTestSamples.*;
import static com.ahmedmeid.crm.domain.NotesTestSamples.*;
import static com.ahmedmeid.crm.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ahmedmeid.crm.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ContactTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Contact.class);
        Contact contact1 = getContactSample1();
        Contact contact2 = new Contact();
        assertThat(contact1).isNotEqualTo(contact2);

        contact2.setId(contact1.getId());
        assertThat(contact1).isEqualTo(contact2);

        contact2 = getContactSample2();
        assertThat(contact1).isNotEqualTo(contact2);
    }

    @Test
    void interactionTest() throws Exception {
        Contact contact = getContactRandomSampleGenerator();
        Interaction interactionBack = getInteractionRandomSampleGenerator();

        contact.addInteraction(interactionBack);
        assertThat(contact.getInteractions()).containsOnly(interactionBack);
        assertThat(interactionBack.getContact()).isEqualTo(contact);

        contact.removeInteraction(interactionBack);
        assertThat(contact.getInteractions()).doesNotContain(interactionBack);
        assertThat(interactionBack.getContact()).isNull();

        contact.interactions(new HashSet<>(Set.of(interactionBack)));
        assertThat(contact.getInteractions()).containsOnly(interactionBack);
        assertThat(interactionBack.getContact()).isEqualTo(contact);

        contact.setInteractions(new HashSet<>());
        assertThat(contact.getInteractions()).doesNotContain(interactionBack);
        assertThat(interactionBack.getContact()).isNull();
    }

    @Test
    void notesTest() throws Exception {
        Contact contact = getContactRandomSampleGenerator();
        Notes notesBack = getNotesRandomSampleGenerator();

        contact.addNotes(notesBack);
        assertThat(contact.getNotes()).containsOnly(notesBack);
        assertThat(notesBack.getContact()).isEqualTo(contact);

        contact.removeNotes(notesBack);
        assertThat(contact.getNotes()).doesNotContain(notesBack);
        assertThat(notesBack.getContact()).isNull();

        contact.notes(new HashSet<>(Set.of(notesBack)));
        assertThat(contact.getNotes()).containsOnly(notesBack);
        assertThat(notesBack.getContact()).isEqualTo(contact);

        contact.setNotes(new HashSet<>());
        assertThat(contact.getNotes()).doesNotContain(notesBack);
        assertThat(notesBack.getContact()).isNull();
    }

    @Test
    void orgTest() throws Exception {
        Contact contact = getContactRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        contact.setOrg(organizationBack);
        assertThat(contact.getOrg()).isEqualTo(organizationBack);

        contact.org(null);
        assertThat(contact.getOrg()).isNull();
    }

    @Test
    void empTest() throws Exception {
        Contact contact = getContactRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        contact.addEmp(employeeBack);
        assertThat(contact.getEmps()).containsOnly(employeeBack);
        assertThat(employeeBack.getContacts()).containsOnly(contact);

        contact.removeEmp(employeeBack);
        assertThat(contact.getEmps()).doesNotContain(employeeBack);
        assertThat(employeeBack.getContacts()).doesNotContain(contact);

        contact.emps(new HashSet<>(Set.of(employeeBack)));
        assertThat(contact.getEmps()).containsOnly(employeeBack);
        assertThat(employeeBack.getContacts()).containsOnly(contact);

        contact.setEmps(new HashSet<>());
        assertThat(contact.getEmps()).doesNotContain(employeeBack);
        assertThat(employeeBack.getContacts()).doesNotContain(contact);
    }
}
