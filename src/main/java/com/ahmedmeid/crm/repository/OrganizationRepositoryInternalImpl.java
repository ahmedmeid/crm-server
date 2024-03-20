package com.ahmedmeid.crm.repository;

import com.ahmedmeid.crm.domain.Organization;
import com.ahmedmeid.crm.repository.rowmapper.OrganizationRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Organization entity.
 */
@SuppressWarnings("unused")
class OrganizationRepositoryInternalImpl extends SimpleR2dbcRepository<Organization, Long> implements OrganizationRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OrganizationRowMapper organizationMapper;

    private static final Table entityTable = Table.aliased("organization", EntityManager.ENTITY_ALIAS);

    public OrganizationRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OrganizationRowMapper organizationMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Organization.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.organizationMapper = organizationMapper;
    }

    @Override
    public Flux<Organization> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Organization> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OrganizationSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Organization.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Organization> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Organization> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Organization process(Row row, RowMetadata metadata) {
        Organization entity = organizationMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Organization> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
