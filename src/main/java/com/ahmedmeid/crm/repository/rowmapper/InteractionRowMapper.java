package com.ahmedmeid.crm.repository.rowmapper;

import com.ahmedmeid.crm.domain.Interaction;
import com.ahmedmeid.crm.domain.enumeration.InteractionType;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Interaction}, with proper type conversions.
 */
@Service
public class InteractionRowMapper implements BiFunction<Row, String, Interaction> {

    private final ColumnConverter converter;

    public InteractionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Interaction} stored in the database.
     */
    @Override
    public Interaction apply(Row row, String prefix) {
        Interaction entity = new Interaction();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setInteractionTimestamp(converter.fromRow(row, prefix + "_interaction_timestamp", Instant.class));
        entity.setType(converter.fromRow(row, prefix + "_type", InteractionType.class));
        entity.setSummary(converter.fromRow(row, prefix + "_summary", String.class));
        entity.setContactId(converter.fromRow(row, prefix + "_contact_id", Long.class));
        return entity;
    }
}
