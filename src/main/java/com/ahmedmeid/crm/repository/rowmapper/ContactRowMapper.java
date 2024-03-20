package com.ahmedmeid.crm.repository.rowmapper;

import com.ahmedmeid.crm.domain.Contact;
import com.ahmedmeid.crm.domain.enumeration.ContactStatus;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Contact}, with proper type conversions.
 */
@Service
public class ContactRowMapper implements BiFunction<Row, String, Contact> {

    private final ColumnConverter converter;

    public ContactRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Contact} stored in the database.
     */
    @Override
    public Contact apply(Row row, String prefix) {
        Contact entity = new Contact();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setContactName(converter.fromRow(row, prefix + "_contact_name", String.class));
        entity.setJobTitle(converter.fromRow(row, prefix + "_job_title", String.class));
        entity.setEmailAddress(converter.fromRow(row, prefix + "_email_address", String.class));
        entity.setPhoneNo(converter.fromRow(row, prefix + "_phone_no", String.class));
        entity.setAddressNumber(converter.fromRow(row, prefix + "_address_number", Integer.class));
        entity.setAddressStreet(converter.fromRow(row, prefix + "_address_street", String.class));
        entity.setAddressCity(converter.fromRow(row, prefix + "_address_city", String.class));
        entity.setLeadSource(converter.fromRow(row, prefix + "_lead_source", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ContactStatus.class));
        entity.setOrgId(converter.fromRow(row, prefix + "_org_id", Long.class));
        return entity;
    }
}
