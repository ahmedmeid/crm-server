package com.ahmedmeid.crm.repository;

import com.ahmedmeid.crm.domain.Interaction;
import com.ahmedmeid.crm.repository.rowmapper.ContactRowMapper;
import com.ahmedmeid.crm.repository.rowmapper.InteractionRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Interaction entity.
 */
@SuppressWarnings("unused")
class InteractionRepositoryInternalImpl extends SimpleR2dbcRepository<Interaction, Long> implements InteractionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ContactRowMapper contactMapper;
    private final InteractionRowMapper interactionMapper;

    private static final Table entityTable = Table.aliased("interaction", EntityManager.ENTITY_ALIAS);
    private static final Table contactTable = Table.aliased("contact", "contact");

    public InteractionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ContactRowMapper contactMapper,
        InteractionRowMapper interactionMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Interaction.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.contactMapper = contactMapper;
        this.interactionMapper = interactionMapper;
    }

    @Override
    public Flux<Interaction> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Interaction> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = InteractionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ContactSqlHelper.getColumns(contactTable, "contact"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(contactTable)
            .on(Column.create("contact_id", entityTable))
            .equals(Column.create("id", contactTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Interaction.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Interaction> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Interaction> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Interaction> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Interaction> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Interaction> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Interaction process(Row row, RowMetadata metadata) {
        Interaction entity = interactionMapper.apply(row, "e");
        entity.setContact(contactMapper.apply(row, "contact"));
        return entity;
    }

    @Override
    public <S extends Interaction> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
