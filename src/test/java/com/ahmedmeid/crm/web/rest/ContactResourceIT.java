package com.ahmedmeid.crm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.ahmedmeid.crm.IntegrationTest;
import com.ahmedmeid.crm.domain.Contact;
import com.ahmedmeid.crm.domain.enumeration.ContactStatus;
import com.ahmedmeid.crm.repository.ContactRepository;
import com.ahmedmeid.crm.repository.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link ContactResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ContactResourceIT {

    private static final String DEFAULT_CONTACT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_JOB_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_JOB_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NO = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NO = "BBBBBBBBBB";

    private static final Integer DEFAULT_ADDRESS_NUMBER = 1;
    private static final Integer UPDATED_ADDRESS_NUMBER = 2;

    private static final String DEFAULT_ADDRESS_STREET = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_STREET = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_CITY = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_LEAD_SOURCE = "AAAAAAAAAA";
    private static final String UPDATED_LEAD_SOURCE = "BBBBBBBBBB";

    private static final ContactStatus DEFAULT_STATUS = ContactStatus.LEAD;
    private static final ContactStatus UPDATED_STATUS = ContactStatus.PROSPECT;

    private static final String ENTITY_API_URL = "/api/contacts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ContactRepository contactRepository;

    @Mock
    private ContactRepository contactRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Contact contact;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contact createEntity(EntityManager em) {
        Contact contact = new Contact()
            .contactName(DEFAULT_CONTACT_NAME)
            .jobTitle(DEFAULT_JOB_TITLE)
            .emailAddress(DEFAULT_EMAIL_ADDRESS)
            .phoneNo(DEFAULT_PHONE_NO)
            .addressNumber(DEFAULT_ADDRESS_NUMBER)
            .addressStreet(DEFAULT_ADDRESS_STREET)
            .addressCity(DEFAULT_ADDRESS_CITY)
            .leadSource(DEFAULT_LEAD_SOURCE)
            .status(DEFAULT_STATUS);
        return contact;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contact createUpdatedEntity(EntityManager em) {
        Contact contact = new Contact()
            .contactName(UPDATED_CONTACT_NAME)
            .jobTitle(UPDATED_JOB_TITLE)
            .emailAddress(UPDATED_EMAIL_ADDRESS)
            .phoneNo(UPDATED_PHONE_NO)
            .addressNumber(UPDATED_ADDRESS_NUMBER)
            .addressStreet(UPDATED_ADDRESS_STREET)
            .addressCity(UPDATED_ADDRESS_CITY)
            .leadSource(UPDATED_LEAD_SOURCE)
            .status(UPDATED_STATUS);
        return contact;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Contact.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        contact = createEntity(em);
    }

    @Test
    void createContact() throws Exception {
        int databaseSizeBeforeCreate = contactRepository.findAll().collectList().block().size();
        // Create the Contact
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(contact))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll().collectList().block();
        assertThat(contactList).hasSize(databaseSizeBeforeCreate + 1);
        Contact testContact = contactList.get(contactList.size() - 1);
        assertThat(testContact.getContactName()).isEqualTo(DEFAULT_CONTACT_NAME);
        assertThat(testContact.getJobTitle()).isEqualTo(DEFAULT_JOB_TITLE);
        assertThat(testContact.getEmailAddress()).isEqualTo(DEFAULT_EMAIL_ADDRESS);
        assertThat(testContact.getPhoneNo()).isEqualTo(DEFAULT_PHONE_NO);
        assertThat(testContact.getAddressNumber()).isEqualTo(DEFAULT_ADDRESS_NUMBER);
        assertThat(testContact.getAddressStreet()).isEqualTo(DEFAULT_ADDRESS_STREET);
        assertThat(testContact.getAddressCity()).isEqualTo(DEFAULT_ADDRESS_CITY);
        assertThat(testContact.getLeadSource()).isEqualTo(DEFAULT_LEAD_SOURCE);
        assertThat(testContact.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createContactWithExistingId() throws Exception {
        // Create the Contact with an existing ID
        contact.setId(1L);

        int databaseSizeBeforeCreate = contactRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(contact))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll().collectList().block();
        assertThat(contactList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllContactsAsStream() {
        // Initialize the database
        contactRepository.save(contact).block();

        List<Contact> contactList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Contact.class)
            .getResponseBody()
            .filter(contact::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(contactList).isNotNull();
        assertThat(contactList).hasSize(1);
        Contact testContact = contactList.get(0);
        assertThat(testContact.getContactName()).isEqualTo(DEFAULT_CONTACT_NAME);
        assertThat(testContact.getJobTitle()).isEqualTo(DEFAULT_JOB_TITLE);
        assertThat(testContact.getEmailAddress()).isEqualTo(DEFAULT_EMAIL_ADDRESS);
        assertThat(testContact.getPhoneNo()).isEqualTo(DEFAULT_PHONE_NO);
        assertThat(testContact.getAddressNumber()).isEqualTo(DEFAULT_ADDRESS_NUMBER);
        assertThat(testContact.getAddressStreet()).isEqualTo(DEFAULT_ADDRESS_STREET);
        assertThat(testContact.getAddressCity()).isEqualTo(DEFAULT_ADDRESS_CITY);
        assertThat(testContact.getLeadSource()).isEqualTo(DEFAULT_LEAD_SOURCE);
        assertThat(testContact.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void getAllContacts() {
        // Initialize the database
        contactRepository.save(contact).block();

        // Get all the contactList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(contact.getId().intValue()))
            .jsonPath("$.[*].contactName")
            .value(hasItem(DEFAULT_CONTACT_NAME))
            .jsonPath("$.[*].jobTitle")
            .value(hasItem(DEFAULT_JOB_TITLE))
            .jsonPath("$.[*].emailAddress")
            .value(hasItem(DEFAULT_EMAIL_ADDRESS))
            .jsonPath("$.[*].phoneNo")
            .value(hasItem(DEFAULT_PHONE_NO))
            .jsonPath("$.[*].addressNumber")
            .value(hasItem(DEFAULT_ADDRESS_NUMBER))
            .jsonPath("$.[*].addressStreet")
            .value(hasItem(DEFAULT_ADDRESS_STREET))
            .jsonPath("$.[*].addressCity")
            .value(hasItem(DEFAULT_ADDRESS_CITY))
            .jsonPath("$.[*].leadSource")
            .value(hasItem(DEFAULT_LEAD_SOURCE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllContactsWithEagerRelationshipsIsEnabled() {
        when(contactRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(contactRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllContactsWithEagerRelationshipsIsNotEnabled() {
        when(contactRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(contactRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getContact() {
        // Initialize the database
        contactRepository.save(contact).block();

        // Get the contact
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(contact.getId().intValue()))
            .jsonPath("$.contactName")
            .value(is(DEFAULT_CONTACT_NAME))
            .jsonPath("$.jobTitle")
            .value(is(DEFAULT_JOB_TITLE))
            .jsonPath("$.emailAddress")
            .value(is(DEFAULT_EMAIL_ADDRESS))
            .jsonPath("$.phoneNo")
            .value(is(DEFAULT_PHONE_NO))
            .jsonPath("$.addressNumber")
            .value(is(DEFAULT_ADDRESS_NUMBER))
            .jsonPath("$.addressStreet")
            .value(is(DEFAULT_ADDRESS_STREET))
            .jsonPath("$.addressCity")
            .value(is(DEFAULT_ADDRESS_CITY))
            .jsonPath("$.leadSource")
            .value(is(DEFAULT_LEAD_SOURCE))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingContact() {
        // Get the contact
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingContact() throws Exception {
        // Initialize the database
        contactRepository.save(contact).block();

        int databaseSizeBeforeUpdate = contactRepository.findAll().collectList().block().size();

        // Update the contact
        Contact updatedContact = contactRepository.findById(contact.getId()).block();
        updatedContact
            .contactName(UPDATED_CONTACT_NAME)
            .jobTitle(UPDATED_JOB_TITLE)
            .emailAddress(UPDATED_EMAIL_ADDRESS)
            .phoneNo(UPDATED_PHONE_NO)
            .addressNumber(UPDATED_ADDRESS_NUMBER)
            .addressStreet(UPDATED_ADDRESS_STREET)
            .addressCity(UPDATED_ADDRESS_CITY)
            .leadSource(UPDATED_LEAD_SOURCE)
            .status(UPDATED_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedContact.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedContact))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll().collectList().block();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
        Contact testContact = contactList.get(contactList.size() - 1);
        assertThat(testContact.getContactName()).isEqualTo(UPDATED_CONTACT_NAME);
        assertThat(testContact.getJobTitle()).isEqualTo(UPDATED_JOB_TITLE);
        assertThat(testContact.getEmailAddress()).isEqualTo(UPDATED_EMAIL_ADDRESS);
        assertThat(testContact.getPhoneNo()).isEqualTo(UPDATED_PHONE_NO);
        assertThat(testContact.getAddressNumber()).isEqualTo(UPDATED_ADDRESS_NUMBER);
        assertThat(testContact.getAddressStreet()).isEqualTo(UPDATED_ADDRESS_STREET);
        assertThat(testContact.getAddressCity()).isEqualTo(UPDATED_ADDRESS_CITY);
        assertThat(testContact.getLeadSource()).isEqualTo(UPDATED_LEAD_SOURCE);
        assertThat(testContact.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingContact() throws Exception {
        int databaseSizeBeforeUpdate = contactRepository.findAll().collectList().block().size();
        contact.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, contact.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(contact))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll().collectList().block();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchContact() throws Exception {
        int databaseSizeBeforeUpdate = contactRepository.findAll().collectList().block().size();
        contact.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(contact))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll().collectList().block();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamContact() throws Exception {
        int databaseSizeBeforeUpdate = contactRepository.findAll().collectList().block().size();
        contact.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(contact))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll().collectList().block();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateContactWithPatch() throws Exception {
        // Initialize the database
        contactRepository.save(contact).block();

        int databaseSizeBeforeUpdate = contactRepository.findAll().collectList().block().size();

        // Update the contact using partial update
        Contact partialUpdatedContact = new Contact();
        partialUpdatedContact.setId(contact.getId());

        partialUpdatedContact
            .contactName(UPDATED_CONTACT_NAME)
            .phoneNo(UPDATED_PHONE_NO)
            .addressNumber(UPDATED_ADDRESS_NUMBER)
            .addressCity(UPDATED_ADDRESS_CITY)
            .leadSource(UPDATED_LEAD_SOURCE)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedContact.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedContact))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll().collectList().block();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
        Contact testContact = contactList.get(contactList.size() - 1);
        assertThat(testContact.getContactName()).isEqualTo(UPDATED_CONTACT_NAME);
        assertThat(testContact.getJobTitle()).isEqualTo(DEFAULT_JOB_TITLE);
        assertThat(testContact.getEmailAddress()).isEqualTo(DEFAULT_EMAIL_ADDRESS);
        assertThat(testContact.getPhoneNo()).isEqualTo(UPDATED_PHONE_NO);
        assertThat(testContact.getAddressNumber()).isEqualTo(UPDATED_ADDRESS_NUMBER);
        assertThat(testContact.getAddressStreet()).isEqualTo(DEFAULT_ADDRESS_STREET);
        assertThat(testContact.getAddressCity()).isEqualTo(UPDATED_ADDRESS_CITY);
        assertThat(testContact.getLeadSource()).isEqualTo(UPDATED_LEAD_SOURCE);
        assertThat(testContact.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void fullUpdateContactWithPatch() throws Exception {
        // Initialize the database
        contactRepository.save(contact).block();

        int databaseSizeBeforeUpdate = contactRepository.findAll().collectList().block().size();

        // Update the contact using partial update
        Contact partialUpdatedContact = new Contact();
        partialUpdatedContact.setId(contact.getId());

        partialUpdatedContact
            .contactName(UPDATED_CONTACT_NAME)
            .jobTitle(UPDATED_JOB_TITLE)
            .emailAddress(UPDATED_EMAIL_ADDRESS)
            .phoneNo(UPDATED_PHONE_NO)
            .addressNumber(UPDATED_ADDRESS_NUMBER)
            .addressStreet(UPDATED_ADDRESS_STREET)
            .addressCity(UPDATED_ADDRESS_CITY)
            .leadSource(UPDATED_LEAD_SOURCE)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedContact.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedContact))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll().collectList().block();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
        Contact testContact = contactList.get(contactList.size() - 1);
        assertThat(testContact.getContactName()).isEqualTo(UPDATED_CONTACT_NAME);
        assertThat(testContact.getJobTitle()).isEqualTo(UPDATED_JOB_TITLE);
        assertThat(testContact.getEmailAddress()).isEqualTo(UPDATED_EMAIL_ADDRESS);
        assertThat(testContact.getPhoneNo()).isEqualTo(UPDATED_PHONE_NO);
        assertThat(testContact.getAddressNumber()).isEqualTo(UPDATED_ADDRESS_NUMBER);
        assertThat(testContact.getAddressStreet()).isEqualTo(UPDATED_ADDRESS_STREET);
        assertThat(testContact.getAddressCity()).isEqualTo(UPDATED_ADDRESS_CITY);
        assertThat(testContact.getLeadSource()).isEqualTo(UPDATED_LEAD_SOURCE);
        assertThat(testContact.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingContact() throws Exception {
        int databaseSizeBeforeUpdate = contactRepository.findAll().collectList().block().size();
        contact.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, contact.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(contact))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll().collectList().block();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchContact() throws Exception {
        int databaseSizeBeforeUpdate = contactRepository.findAll().collectList().block().size();
        contact.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(contact))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll().collectList().block();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamContact() throws Exception {
        int databaseSizeBeforeUpdate = contactRepository.findAll().collectList().block().size();
        contact.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(contact))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll().collectList().block();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteContact() {
        // Initialize the database
        contactRepository.save(contact).block();

        int databaseSizeBeforeDelete = contactRepository.findAll().collectList().block().size();

        // Delete the contact
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Contact> contactList = contactRepository.findAll().collectList().block();
        assertThat(contactList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
