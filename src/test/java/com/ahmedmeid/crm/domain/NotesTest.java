package com.ahmedmeid.crm.domain;

import static com.ahmedmeid.crm.domain.ContactTestSamples.*;
import static com.ahmedmeid.crm.domain.NotesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ahmedmeid.crm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notes.class);
        Notes notes1 = getNotesSample1();
        Notes notes2 = new Notes();
        assertThat(notes1).isNotEqualTo(notes2);

        notes2.setId(notes1.getId());
        assertThat(notes1).isEqualTo(notes2);

        notes2 = getNotesSample2();
        assertThat(notes1).isNotEqualTo(notes2);
    }

    @Test
    void contactTest() throws Exception {
        Notes notes = getNotesRandomSampleGenerator();
        Contact contactBack = getContactRandomSampleGenerator();

        notes.setContact(contactBack);
        assertThat(notes.getContact()).isEqualTo(contactBack);

        notes.contact(null);
        assertThat(notes.getContact()).isNull();
    }
}
