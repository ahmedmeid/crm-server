package com.ahmedmeid.crm.web.rest;

import com.ahmedmeid.crm.domain.Interaction;
import com.ahmedmeid.crm.repository.InteractionRepository;
import com.ahmedmeid.crm.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.ahmedmeid.crm.domain.Interaction}.
 */
@RestController
@RequestMapping("/api/interactions")
@Transactional
public class InteractionResource {

    private final Logger log = LoggerFactory.getLogger(InteractionResource.class);

    private static final String ENTITY_NAME = "interaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InteractionRepository interactionRepository;

    public InteractionResource(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    /**
     * {@code POST  /interactions} : Create a new interaction.
     *
     * @param interaction the interaction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new interaction, or with status {@code 400 (Bad Request)} if the interaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Interaction>> createInteraction(@Valid @RequestBody Interaction interaction) throws URISyntaxException {
        log.debug("REST request to save Interaction : {}", interaction);
        if (interaction.getId() != null) {
            throw new BadRequestAlertException("A new interaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return interactionRepository
            .save(interaction)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/interactions/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /interactions/:id} : Updates an existing interaction.
     *
     * @param id the id of the interaction to save.
     * @param interaction the interaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated interaction,
     * or with status {@code 400 (Bad Request)} if the interaction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the interaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Interaction>> updateInteraction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Interaction interaction
    ) throws URISyntaxException {
        log.debug("REST request to update Interaction : {}, {}", id, interaction);
        if (interaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, interaction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return interactionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return interactionRepository
                    .save(interaction)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /interactions/:id} : Partial updates given fields of an existing interaction, field will ignore if it is null
     *
     * @param id the id of the interaction to save.
     * @param interaction the interaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated interaction,
     * or with status {@code 400 (Bad Request)} if the interaction is not valid,
     * or with status {@code 404 (Not Found)} if the interaction is not found,
     * or with status {@code 500 (Internal Server Error)} if the interaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Interaction>> partialUpdateInteraction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Interaction interaction
    ) throws URISyntaxException {
        log.debug("REST request to partial update Interaction partially : {}, {}", id, interaction);
        if (interaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, interaction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return interactionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Interaction> result = interactionRepository
                    .findById(interaction.getId())
                    .map(existingInteraction -> {
                        if (interaction.getInteractionTimestamp() != null) {
                            existingInteraction.setInteractionTimestamp(interaction.getInteractionTimestamp());
                        }
                        if (interaction.getType() != null) {
                            existingInteraction.setType(interaction.getType());
                        }
                        if (interaction.getSummary() != null) {
                            existingInteraction.setSummary(interaction.getSummary());
                        }

                        return existingInteraction;
                    })
                    .flatMap(interactionRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /interactions} : get all the interactions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of interactions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Interaction>> getAllInteractions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all Interactions");
        if (eagerload) {
            return interactionRepository.findAllWithEagerRelationships().collectList();
        } else {
            return interactionRepository.findAll().collectList();
        }
    }

    /**
     * {@code GET  /interactions} : get all the interactions as a stream.
     * @return the {@link Flux} of interactions.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Interaction> getAllInteractionsAsStream() {
        log.debug("REST request to get all Interactions as a stream");
        return interactionRepository.findAll();
    }

    /**
     * {@code GET  /interactions/:id} : get the "id" interaction.
     *
     * @param id the id of the interaction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the interaction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Interaction>> getInteraction(@PathVariable("id") Long id) {
        log.debug("REST request to get Interaction : {}", id);
        Mono<Interaction> interaction = interactionRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(interaction);
    }

    /**
     * {@code DELETE  /interactions/:id} : delete the "id" interaction.
     *
     * @param id the id of the interaction to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInteraction(@PathVariable("id") Long id) {
        log.debug("REST request to delete Interaction : {}", id);
        return interactionRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
