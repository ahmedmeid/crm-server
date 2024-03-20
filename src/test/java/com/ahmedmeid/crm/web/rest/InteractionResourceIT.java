package com.ahmedmeid.crm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.ahmedmeid.crm.IntegrationTest;
import com.ahmedmeid.crm.domain.Contact;
import com.ahmedmeid.crm.domain.Interaction;
import com.ahmedmeid.crm.domain.enumeration.InteractionType;
import com.ahmedmeid.crm.repository.EntityManager;
import com.ahmedmeid.crm.repository.InteractionRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link InteractionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InteractionResourceIT {

    private static final Instant DEFAULT_INTERACTION_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_INTERACTION_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final InteractionType DEFAULT_TYPE = InteractionType.CALL;
    private static final InteractionType UPDATED_TYPE = InteractionType.EMAIL;

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/interactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InteractionRepository interactionRepository;

    @Mock
    private InteractionRepository interactionRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Interaction interaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Interaction createEntity(EntityManager em) {
        Interaction interaction = new Interaction()
            .interactionTimestamp(DEFAULT_INTERACTION_TIMESTAMP)
            .type(DEFAULT_TYPE)
            .summary(DEFAULT_SUMMARY);
        // Add required entity
        Contact contact;
        contact = em.insert(ContactResourceIT.createEntity(em)).block();
        interaction.setContact(contact);
        return interaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Interaction createUpdatedEntity(EntityManager em) {
        Interaction interaction = new Interaction()
            .interactionTimestamp(UPDATED_INTERACTION_TIMESTAMP)
            .type(UPDATED_TYPE)
            .summary(UPDATED_SUMMARY);
        // Add required entity
        Contact contact;
        contact = em.insert(ContactResourceIT.createUpdatedEntity(em)).block();
        interaction.setContact(contact);
        return interaction;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Interaction.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ContactResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        interaction = createEntity(em);
    }

    @Test
    void createInteraction() throws Exception {
        int databaseSizeBeforeCreate = interactionRepository.findAll().collectList().block().size();
        // Create the Interaction
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interaction))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Interaction in the database
        List<Interaction> interactionList = interactionRepository.findAll().collectList().block();
        assertThat(interactionList).hasSize(databaseSizeBeforeCreate + 1);
        Interaction testInteraction = interactionList.get(interactionList.size() - 1);
        assertThat(testInteraction.getInteractionTimestamp()).isEqualTo(DEFAULT_INTERACTION_TIMESTAMP);
        assertThat(testInteraction.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testInteraction.getSummary()).isEqualTo(DEFAULT_SUMMARY);
    }

    @Test
    void createInteractionWithExistingId() throws Exception {
        // Create the Interaction with an existing ID
        interaction.setId(1L);

        int databaseSizeBeforeCreate = interactionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interaction in the database
        List<Interaction> interactionList = interactionRepository.findAll().collectList().block();
        assertThat(interactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllInteractionsAsStream() {
        // Initialize the database
        interactionRepository.save(interaction).block();

        List<Interaction> interactionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Interaction.class)
            .getResponseBody()
            .filter(interaction::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(interactionList).isNotNull();
        assertThat(interactionList).hasSize(1);
        Interaction testInteraction = interactionList.get(0);
        assertThat(testInteraction.getInteractionTimestamp()).isEqualTo(DEFAULT_INTERACTION_TIMESTAMP);
        assertThat(testInteraction.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testInteraction.getSummary()).isEqualTo(DEFAULT_SUMMARY);
    }

    @Test
    void getAllInteractions() {
        // Initialize the database
        interactionRepository.save(interaction).block();

        // Get all the interactionList
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
            .value(hasItem(interaction.getId().intValue()))
            .jsonPath("$.[*].interactionTimestamp")
            .value(hasItem(DEFAULT_INTERACTION_TIMESTAMP.toString()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].summary")
            .value(hasItem(DEFAULT_SUMMARY.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInteractionsWithEagerRelationshipsIsEnabled() {
        when(interactionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(interactionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInteractionsWithEagerRelationshipsIsNotEnabled() {
        when(interactionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(interactionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getInteraction() {
        // Initialize the database
        interactionRepository.save(interaction).block();

        // Get the interaction
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, interaction.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(interaction.getId().intValue()))
            .jsonPath("$.interactionTimestamp")
            .value(is(DEFAULT_INTERACTION_TIMESTAMP.toString()))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE.toString()))
            .jsonPath("$.summary")
            .value(is(DEFAULT_SUMMARY.toString()));
    }

    @Test
    void getNonExistingInteraction() {
        // Get the interaction
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInteraction() throws Exception {
        // Initialize the database
        interactionRepository.save(interaction).block();

        int databaseSizeBeforeUpdate = interactionRepository.findAll().collectList().block().size();

        // Update the interaction
        Interaction updatedInteraction = interactionRepository.findById(interaction.getId()).block();
        updatedInteraction.interactionTimestamp(UPDATED_INTERACTION_TIMESTAMP).type(UPDATED_TYPE).summary(UPDATED_SUMMARY);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedInteraction.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedInteraction))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Interaction in the database
        List<Interaction> interactionList = interactionRepository.findAll().collectList().block();
        assertThat(interactionList).hasSize(databaseSizeBeforeUpdate);
        Interaction testInteraction = interactionList.get(interactionList.size() - 1);
        assertThat(testInteraction.getInteractionTimestamp()).isEqualTo(UPDATED_INTERACTION_TIMESTAMP);
        assertThat(testInteraction.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testInteraction.getSummary()).isEqualTo(UPDATED_SUMMARY);
    }

    @Test
    void putNonExistingInteraction() throws Exception {
        int databaseSizeBeforeUpdate = interactionRepository.findAll().collectList().block().size();
        interaction.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, interaction.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interaction in the database
        List<Interaction> interactionList = interactionRepository.findAll().collectList().block();
        assertThat(interactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInteraction() throws Exception {
        int databaseSizeBeforeUpdate = interactionRepository.findAll().collectList().block().size();
        interaction.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interaction in the database
        List<Interaction> interactionList = interactionRepository.findAll().collectList().block();
        assertThat(interactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInteraction() throws Exception {
        int databaseSizeBeforeUpdate = interactionRepository.findAll().collectList().block().size();
        interaction.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interaction))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Interaction in the database
        List<Interaction> interactionList = interactionRepository.findAll().collectList().block();
        assertThat(interactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInteractionWithPatch() throws Exception {
        // Initialize the database
        interactionRepository.save(interaction).block();

        int databaseSizeBeforeUpdate = interactionRepository.findAll().collectList().block().size();

        // Update the interaction using partial update
        Interaction partialUpdatedInteraction = new Interaction();
        partialUpdatedInteraction.setId(interaction.getId());

        partialUpdatedInteraction.interactionTimestamp(UPDATED_INTERACTION_TIMESTAMP).summary(UPDATED_SUMMARY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInteraction.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedInteraction))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Interaction in the database
        List<Interaction> interactionList = interactionRepository.findAll().collectList().block();
        assertThat(interactionList).hasSize(databaseSizeBeforeUpdate);
        Interaction testInteraction = interactionList.get(interactionList.size() - 1);
        assertThat(testInteraction.getInteractionTimestamp()).isEqualTo(UPDATED_INTERACTION_TIMESTAMP);
        assertThat(testInteraction.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testInteraction.getSummary()).isEqualTo(UPDATED_SUMMARY);
    }

    @Test
    void fullUpdateInteractionWithPatch() throws Exception {
        // Initialize the database
        interactionRepository.save(interaction).block();

        int databaseSizeBeforeUpdate = interactionRepository.findAll().collectList().block().size();

        // Update the interaction using partial update
        Interaction partialUpdatedInteraction = new Interaction();
        partialUpdatedInteraction.setId(interaction.getId());

        partialUpdatedInteraction.interactionTimestamp(UPDATED_INTERACTION_TIMESTAMP).type(UPDATED_TYPE).summary(UPDATED_SUMMARY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInteraction.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedInteraction))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Interaction in the database
        List<Interaction> interactionList = interactionRepository.findAll().collectList().block();
        assertThat(interactionList).hasSize(databaseSizeBeforeUpdate);
        Interaction testInteraction = interactionList.get(interactionList.size() - 1);
        assertThat(testInteraction.getInteractionTimestamp()).isEqualTo(UPDATED_INTERACTION_TIMESTAMP);
        assertThat(testInteraction.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testInteraction.getSummary()).isEqualTo(UPDATED_SUMMARY);
    }

    @Test
    void patchNonExistingInteraction() throws Exception {
        int databaseSizeBeforeUpdate = interactionRepository.findAll().collectList().block().size();
        interaction.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, interaction.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(interaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interaction in the database
        List<Interaction> interactionList = interactionRepository.findAll().collectList().block();
        assertThat(interactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInteraction() throws Exception {
        int databaseSizeBeforeUpdate = interactionRepository.findAll().collectList().block().size();
        interaction.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(interaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interaction in the database
        List<Interaction> interactionList = interactionRepository.findAll().collectList().block();
        assertThat(interactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInteraction() throws Exception {
        int databaseSizeBeforeUpdate = interactionRepository.findAll().collectList().block().size();
        interaction.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(interaction))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Interaction in the database
        List<Interaction> interactionList = interactionRepository.findAll().collectList().block();
        assertThat(interactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInteraction() {
        // Initialize the database
        interactionRepository.save(interaction).block();

        int databaseSizeBeforeDelete = interactionRepository.findAll().collectList().block().size();

        // Delete the interaction
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, interaction.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Interaction> interactionList = interactionRepository.findAll().collectList().block();
        assertThat(interactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
