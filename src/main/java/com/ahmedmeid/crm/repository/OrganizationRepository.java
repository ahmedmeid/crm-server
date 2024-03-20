package com.ahmedmeid.crm.repository;

import com.ahmedmeid.crm.domain.Organization;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Organization entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrganizationRepository extends ReactiveCrudRepository<Organization, Long>, OrganizationRepositoryInternal {
    @Override
    <S extends Organization> Mono<S> save(S entity);

    @Override
    Flux<Organization> findAll();

    @Override
    Mono<Organization> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface OrganizationRepositoryInternal {
    <S extends Organization> Mono<S> save(S entity);

    Flux<Organization> findAllBy(Pageable pageable);

    Flux<Organization> findAll();

    Mono<Organization> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Organization> findAllBy(Pageable pageable, Criteria criteria);
}
