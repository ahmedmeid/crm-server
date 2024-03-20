package com.ahmedmeid.crm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.ahmedmeid.crm.IntegrationTest;
import com.ahmedmeid.crm.domain.Organization;
import com.ahmedmeid.crm.repository.EntityManager;
import com.ahmedmeid.crm.repository.OrganizationRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link OrganizationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrganizationResourceIT {

    private static final String DEFAULT_ORGANIZATION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ORGANIZATION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_INDUSTRY = "AAAAAAAAAA";
    private static final String UPDATED_INDUSTRY = "BBBBBBBBBB";

    private static final String DEFAULT_WEBSITE = "AAAAAAAAAA";
    private static final String UPDATED_WEBSITE = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/organizations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Organization organization;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organization createEntity(EntityManager em) {
        Organization organization = new Organization()
            .organizationName(DEFAULT_ORGANIZATION_NAME)
            .industry(DEFAULT_INDUSTRY)
            .website(DEFAULT_WEBSITE)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .address(DEFAULT_ADDRESS);
        return organization;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organization createUpdatedEntity(EntityManager em) {
        Organization organization = new Organization()
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .industry(UPDATED_INDUSTRY)
            .website(UPDATED_WEBSITE)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS);
        return organization;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Organization.class).block();
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
        organization = createEntity(em);
    }

    @Test
    void createOrganization() throws Exception {
        int databaseSizeBeforeCreate = organizationRepository.findAll().collectList().block().size();
        // Create the Organization
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeCreate + 1);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getOrganizationName()).isEqualTo(DEFAULT_ORGANIZATION_NAME);
        assertThat(testOrganization.getIndustry()).isEqualTo(DEFAULT_INDUSTRY);
        assertThat(testOrganization.getWebsite()).isEqualTo(DEFAULT_WEBSITE);
        assertThat(testOrganization.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testOrganization.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    void createOrganizationWithExistingId() throws Exception {
        // Create the Organization with an existing ID
        organization.setId(1L);

        int databaseSizeBeforeCreate = organizationRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkOrganizationNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = organizationRepository.findAll().collectList().block().size();
        // set the field null
        organization.setOrganizationName(null);

        // Create the Organization, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllOrganizationsAsStream() {
        // Initialize the database
        organizationRepository.save(organization).block();

        List<Organization> organizationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Organization.class)
            .getResponseBody()
            .filter(organization::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(organizationList).isNotNull();
        assertThat(organizationList).hasSize(1);
        Organization testOrganization = organizationList.get(0);
        assertThat(testOrganization.getOrganizationName()).isEqualTo(DEFAULT_ORGANIZATION_NAME);
        assertThat(testOrganization.getIndustry()).isEqualTo(DEFAULT_INDUSTRY);
        assertThat(testOrganization.getWebsite()).isEqualTo(DEFAULT_WEBSITE);
        assertThat(testOrganization.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testOrganization.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    void getAllOrganizations() {
        // Initialize the database
        organizationRepository.save(organization).block();

        // Get all the organizationList
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
            .value(hasItem(organization.getId().intValue()))
            .jsonPath("$.[*].organizationName")
            .value(hasItem(DEFAULT_ORGANIZATION_NAME))
            .jsonPath("$.[*].industry")
            .value(hasItem(DEFAULT_INDUSTRY))
            .jsonPath("$.[*].website")
            .value(hasItem(DEFAULT_WEBSITE))
            .jsonPath("$.[*].phoneNumber")
            .value(hasItem(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS));
    }

    @Test
    void getOrganization() {
        // Initialize the database
        organizationRepository.save(organization).block();

        // Get the organization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, organization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(organization.getId().intValue()))
            .jsonPath("$.organizationName")
            .value(is(DEFAULT_ORGANIZATION_NAME))
            .jsonPath("$.industry")
            .value(is(DEFAULT_INDUSTRY))
            .jsonPath("$.website")
            .value(is(DEFAULT_WEBSITE))
            .jsonPath("$.phoneNumber")
            .value(is(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS));
    }

    @Test
    void getNonExistingOrganization() {
        // Get the organization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOrganization() throws Exception {
        // Initialize the database
        organizationRepository.save(organization).block();

        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();

        // Update the organization
        Organization updatedOrganization = organizationRepository.findById(organization.getId()).block();
        updatedOrganization
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .industry(UPDATED_INDUSTRY)
            .website(UPDATED_WEBSITE)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedOrganization.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedOrganization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getOrganizationName()).isEqualTo(UPDATED_ORGANIZATION_NAME);
        assertThat(testOrganization.getIndustry()).isEqualTo(UPDATED_INDUSTRY);
        assertThat(testOrganization.getWebsite()).isEqualTo(UPDATED_WEBSITE);
        assertThat(testOrganization.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testOrganization.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    void putNonExistingOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();
        organization.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, organization.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();
        organization.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();
        organization.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOrganizationWithPatch() throws Exception {
        // Initialize the database
        organizationRepository.save(organization).block();

        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();

        // Update the organization using partial update
        Organization partialUpdatedOrganization = new Organization();
        partialUpdatedOrganization.setId(organization.getId());

        partialUpdatedOrganization.organizationName(UPDATED_ORGANIZATION_NAME).website(UPDATED_WEBSITE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getOrganizationName()).isEqualTo(UPDATED_ORGANIZATION_NAME);
        assertThat(testOrganization.getIndustry()).isEqualTo(DEFAULT_INDUSTRY);
        assertThat(testOrganization.getWebsite()).isEqualTo(UPDATED_WEBSITE);
        assertThat(testOrganization.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testOrganization.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    void fullUpdateOrganizationWithPatch() throws Exception {
        // Initialize the database
        organizationRepository.save(organization).block();

        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();

        // Update the organization using partial update
        Organization partialUpdatedOrganization = new Organization();
        partialUpdatedOrganization.setId(organization.getId());

        partialUpdatedOrganization
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .industry(UPDATED_INDUSTRY)
            .website(UPDATED_WEBSITE)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getOrganizationName()).isEqualTo(UPDATED_ORGANIZATION_NAME);
        assertThat(testOrganization.getIndustry()).isEqualTo(UPDATED_INDUSTRY);
        assertThat(testOrganization.getWebsite()).isEqualTo(UPDATED_WEBSITE);
        assertThat(testOrganization.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testOrganization.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    void patchNonExistingOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();
        organization.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, organization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();
        organization.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();
        organization.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOrganization() {
        // Initialize the database
        organizationRepository.save(organization).block();

        int databaseSizeBeforeDelete = organizationRepository.findAll().collectList().block().size();

        // Delete the organization
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, organization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
