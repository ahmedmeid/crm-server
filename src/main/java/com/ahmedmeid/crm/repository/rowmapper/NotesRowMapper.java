package com.ahmedmeid.crm.repository.rowmapper;

import com.ahmedmeid.crm.domain.Notes;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Notes}, with proper type conversions.
 */
@Service
public class NotesRowMapper implements BiFunction<Row, String, Notes> {

    private final ColumnConverter converter;

    public NotesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Notes} stored in the database.
     */
    @Override
    public Notes apply(Row row, String prefix) {
        Notes entity = new Notes();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNoteTimestamp(converter.fromRow(row, prefix + "_note_timestamp", Instant.class));
        entity.setNote(converter.fromRow(row, prefix + "_note", String.class));
        entity.setContactId(converter.fromRow(row, prefix + "_contact_id", Long.class));
        return entity;
    }
}
