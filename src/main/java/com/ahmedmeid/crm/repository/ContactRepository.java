package com.ahmedmeid.crm.repository;

import com.ahmedmeid.crm.domain.Contact;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Contact entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContactRepository
    extends ReactiveCrudRepository<Contact, Long>, ContactRepositoryInternal, ReactiveQueryByExampleExecutor<Contact> {
    @Override
    Mono<Contact> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Contact> findAllWithEagerRelationships();

    @Override
    Flux<Contact> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM contact entity WHERE entity.org_id = :id")
    Flux<Contact> findByOrg(Long id);

    @Query("SELECT * FROM contact entity WHERE entity.org_id IS NULL")
    Flux<Contact> findAllWhereOrgIsNull();

    @Override
    <S extends Contact> Mono<S> save(S entity);

    @Override
    Flux<Contact> findAll();

    @Override
    Mono<Contact> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ContactRepositoryInternal {
    <S extends Contact> Mono<S> save(S entity);

    Flux<Contact> findAllBy(Pageable pageable);

    Flux<Contact> findAll();

    Mono<Contact> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Contact> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Contact> findOneWithEagerRelationships(Long id);

    Flux<Contact> findAllWithEagerRelationships();

    Flux<Contact> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
