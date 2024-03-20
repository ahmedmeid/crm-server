package com.ahmedmeid.crm.repository.rowmapper;

import com.ahmedmeid.crm.domain.Organization;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Organization}, with proper type conversions.
 */
@Service
public class OrganizationRowMapper implements BiFunction<Row, String, Organization> {

    private final ColumnConverter converter;

    public OrganizationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Organization} stored in the database.
     */
    @Override
    public Organization apply(Row row, String prefix) {
        Organization entity = new Organization();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setOrganizationName(converter.fromRow(row, prefix + "_organization_name", String.class));
        entity.setIndustry(converter.fromRow(row, prefix + "_industry", String.class));
        entity.setWebsite(converter.fromRow(row, prefix + "_website", String.class));
        entity.setPhoneNumber(converter.fromRow(row, prefix + "_phone_number", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        return entity;
    }
}
