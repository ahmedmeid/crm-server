package com.ahmedmeid.crm.domain;

import static com.ahmedmeid.crm.domain.ContactTestSamples.*;
import static com.ahmedmeid.crm.domain.InteractionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ahmedmeid.crm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InteractionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Interaction.class);
        Interaction interaction1 = getInteractionSample1();
        Interaction interaction2 = new Interaction();
        assertThat(interaction1).isNotEqualTo(interaction2);

        interaction2.setId(interaction1.getId());
        assertThat(interaction1).isEqualTo(interaction2);

        interaction2 = getInteractionSample2();
        assertThat(interaction1).isNotEqualTo(interaction2);
    }

    @Test
    void contactTest() throws Exception {
        Interaction interaction = getInteractionRandomSampleGenerator();
        Contact contactBack = getContactRandomSampleGenerator();

        interaction.setContact(contactBack);
        assertThat(interaction.getContact()).isEqualTo(contactBack);

        interaction.contact(null);
        assertThat(interaction.getContact()).isNull();
    }
}
