package com.ahmedmeid.crm.repository;

import com.ahmedmeid.crm.domain.Interaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Interaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InteractionRepository extends ReactiveCrudRepository<Interaction, Long>, InteractionRepositoryInternal {
    @Override
    Mono<Interaction> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Interaction> findAllWithEagerRelationships();

    @Override
    Flux<Interaction> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM interaction entity WHERE entity.contact_id = :id")
    Flux<Interaction> findByContact(Long id);

    @Query("SELECT * FROM interaction entity WHERE entity.contact_id IS NULL")
    Flux<Interaction> findAllWhereContactIsNull();

    @Override
    <S extends Interaction> Mono<S> save(S entity);

    @Override
    Flux<Interaction> findAll();

    @Override
    Mono<Interaction> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface InteractionRepositoryInternal {
    <S extends Interaction> Mono<S> save(S entity);

    Flux<Interaction> findAllBy(Pageable pageable);

    Flux<Interaction> findAll();

    Mono<Interaction> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Interaction> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Interaction> findOneWithEagerRelationships(Long id);

    Flux<Interaction> findAllWithEagerRelationships();

    Flux<Interaction> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
