package com.ahmedmeid.crm.repository;

import com.ahmedmeid.crm.domain.Notes;
import com.ahmedmeid.crm.repository.rowmapper.ContactRowMapper;
import com.ahmedmeid.crm.repository.rowmapper.NotesRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Notes entity.
 */
@SuppressWarnings("unused")
class NotesRepositoryInternalImpl extends SimpleR2dbcRepository<Notes, Long> implements NotesRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ContactRowMapper contactMapper;
    private final NotesRowMapper notesMapper;

    private static final Table entityTable = Table.aliased("notes", EntityManager.ENTITY_ALIAS);
    private static final Table contactTable = Table.aliased("contact", "contact");

    public NotesRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ContactRowMapper contactMapper,
        NotesRowMapper notesMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Notes.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.contactMapper = contactMapper;
        this.notesMapper = notesMapper;
    }

    @Override
    public Flux<Notes> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Notes> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = NotesSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ContactSqlHelper.getColumns(contactTable, "contact"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(contactTable)
            .on(Column.create("contact_id", entityTable))
            .equals(Column.create("id", contactTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Notes.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Notes> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Notes> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Notes> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Notes> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Notes> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Notes process(Row row, RowMetadata metadata) {
        Notes entity = notesMapper.apply(row, "e");
        entity.setContact(contactMapper.apply(row, "contact"));
        return entity;
    }

    @Override
    public <S extends Notes> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
