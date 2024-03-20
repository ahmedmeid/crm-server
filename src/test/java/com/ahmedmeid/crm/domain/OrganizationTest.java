package com.ahmedmeid.crm.domain;

import static com.ahmedmeid.crm.domain.ContactTestSamples.*;
import static com.ahmedmeid.crm.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ahmedmeid.crm.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrganizationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Organization.class);
        Organization organization1 = getOrganizationSample1();
        Organization organization2 = new Organization();
        assertThat(organization1).isNotEqualTo(organization2);

        organization2.setId(organization1.getId());
        assertThat(organization1).isEqualTo(organization2);

        organization2 = getOrganizationSample2();
        assertThat(organization1).isNotEqualTo(organization2);
    }

    @Test
    void contactTest() throws Exception {
        Organization organization = getOrganizationRandomSampleGenerator();
        Contact contactBack = getContactRandomSampleGenerator();

        organization.addContact(contactBack);
        assertThat(organization.getContacts()).containsOnly(contactBack);
        assertThat(contactBack.getOrg()).isEqualTo(organization);

        organization.removeContact(contactBack);
        assertThat(organization.getContacts()).doesNotContain(contactBack);
        assertThat(contactBack.getOrg()).isNull();

        organization.contacts(new HashSet<>(Set.of(contactBack)));
        assertThat(organization.getContacts()).containsOnly(contactBack);
        assertThat(contactBack.getOrg()).isEqualTo(organization);

        organization.setContacts(new HashSet<>());
        assertThat(organization.getContacts()).doesNotContain(contactBack);
        assertThat(contactBack.getOrg()).isNull();
    }
}
