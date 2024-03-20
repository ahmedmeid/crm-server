package com.ahmedmeid.crm.repository;

import com.ahmedmeid.crm.domain.Notes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Notes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotesRepository extends ReactiveCrudRepository<Notes, Long>, NotesRepositoryInternal {
    @Override
    Mono<Notes> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Notes> findAllWithEagerRelationships();

    @Override
    Flux<Notes> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM notes entity WHERE entity.contact_id = :id")
    Flux<Notes> findByContact(Long id);

    @Query("SELECT * FROM notes entity WHERE entity.contact_id IS NULL")
    Flux<Notes> findAllWhereContactIsNull();

    @Override
    <S extends Notes> Mono<S> save(S entity);

    @Override
    Flux<Notes> findAll();

    @Override
    Mono<Notes> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface NotesRepositoryInternal {
    <S extends Notes> Mono<S> save(S entity);

    Flux<Notes> findAllBy(Pageable pageable);

    Flux<Notes> findAll();

    Mono<Notes> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Notes> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Notes> findOneWithEagerRelationships(Long id);

    Flux<Notes> findAllWithEagerRelationships();

    Flux<Notes> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
