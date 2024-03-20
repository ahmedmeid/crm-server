package com.ahmedmeid.crm.web.rest;

import com.ahmedmeid.crm.domain.Notes;
import com.ahmedmeid.crm.repository.NotesRepository;
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
 * REST controller for managing {@link com.ahmedmeid.crm.domain.Notes}.
 */
@RestController
@RequestMapping("/api/notes")
@Transactional
public class NotesResource {

    private final Logger log = LoggerFactory.getLogger(NotesResource.class);

    private static final String ENTITY_NAME = "notes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotesRepository notesRepository;

    public NotesResource(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    /**
     * {@code POST  /notes} : Create a new notes.
     *
     * @param notes the notes to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notes, or with status {@code 400 (Bad Request)} if the notes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Notes>> createNotes(@Valid @RequestBody Notes notes) throws URISyntaxException {
        log.debug("REST request to save Notes : {}", notes);
        if (notes.getId() != null) {
            throw new BadRequestAlertException("A new notes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return notesRepository
            .save(notes)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/notes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /notes/:id} : Updates an existing notes.
     *
     * @param id the id of the notes to save.
     * @param notes the notes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notes,
     * or with status {@code 400 (Bad Request)} if the notes is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Notes>> updateNotes(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Notes notes
    ) throws URISyntaxException {
        log.debug("REST request to update Notes : {}, {}", id, notes);
        if (notes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return notesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return notesRepository
                    .save(notes)
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
     * {@code PATCH  /notes/:id} : Partial updates given fields of an existing notes, field will ignore if it is null
     *
     * @param id the id of the notes to save.
     * @param notes the notes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notes,
     * or with status {@code 400 (Bad Request)} if the notes is not valid,
     * or with status {@code 404 (Not Found)} if the notes is not found,
     * or with status {@code 500 (Internal Server Error)} if the notes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Notes>> partialUpdateNotes(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Notes notes
    ) throws URISyntaxException {
        log.debug("REST request to partial update Notes partially : {}, {}", id, notes);
        if (notes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return notesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Notes> result = notesRepository
                    .findById(notes.getId())
                    .map(existingNotes -> {
                        if (notes.getNoteTimestamp() != null) {
                            existingNotes.setNoteTimestamp(notes.getNoteTimestamp());
                        }
                        if (notes.getNote() != null) {
                            existingNotes.setNote(notes.getNote());
                        }

                        return existingNotes;
                    })
                    .flatMap(notesRepository::save);

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
     * {@code GET  /notes} : get all the notes.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notes in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Notes>> getAllNotes(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        log.debug("REST request to get all Notes");
        if (eagerload) {
            return notesRepository.findAllWithEagerRelationships().collectList();
        } else {
            return notesRepository.findAll().collectList();
        }
    }

    /**
     * {@code GET  /notes} : get all the notes as a stream.
     * @return the {@link Flux} of notes.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Notes> getAllNotesAsStream() {
        log.debug("REST request to get all Notes as a stream");
        return notesRepository.findAll();
    }

    /**
     * {@code GET  /notes/:id} : get the "id" notes.
     *
     * @param id the id of the notes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Notes>> getNotes(@PathVariable("id") Long id) {
        log.debug("REST request to get Notes : {}", id);
        Mono<Notes> notes = notesRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(notes);
    }

    /**
     * {@code DELETE  /notes/:id} : delete the "id" notes.
     *
     * @param id the id of the notes to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteNotes(@PathVariable("id") Long id) {
        log.debug("REST request to delete Notes : {}", id);
        return notesRepository
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
