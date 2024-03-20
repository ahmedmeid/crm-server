package com.ahmedmeid.crm.repository;

import com.ahmedmeid.crm.domain.Contact;
import com.ahmedmeid.crm.repository.rowmapper.ContactRowMapper;
import com.ahmedmeid.crm.repository.rowmapper.OrganizationRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Contact entity.
 */
@SuppressWarnings("unused")
class ContactRepositoryInternalImpl extends SimpleR2dbcRepository<Contact, Long> implements ContactRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OrganizationRowMapper organizationMapper;
    private final ContactRowMapper contactMapper;

    private static final Table entityTable = Table.aliased("contact", EntityManager.ENTITY_ALIAS);
    private static final Table orgTable = Table.aliased("organization", "org");

    public ContactRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OrganizationRowMapper organizationMapper,
        ContactRowMapper contactMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Contact.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.organizationMapper = organizationMapper;
        this.contactMapper = contactMapper;
    }

    @Override
    public Flux<Contact> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Contact> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ContactSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OrganizationSqlHelper.getColumns(orgTable, "org"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orgTable)
            .on(Column.create("org_id", entityTable))
            .equals(Column.create("id", orgTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Contact.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Contact> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Contact> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Contact> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Contact> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Contact> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Contact process(Row row, RowMetadata metadata) {
        Contact entity = contactMapper.apply(row, "e");
        entity.setOrg(organizationMapper.apply(row, "org"));
        return entity;
    }

    @Override
    public <S extends Contact> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
