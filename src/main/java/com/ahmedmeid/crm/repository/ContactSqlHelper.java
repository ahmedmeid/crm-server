package com.ahmedmeid.crm.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ContactSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("contact_name", table, columnPrefix + "_contact_name"));
        columns.add(Column.aliased("job_title", table, columnPrefix + "_job_title"));
        columns.add(Column.aliased("email_address", table, columnPrefix + "_email_address"));
        columns.add(Column.aliased("phone_no", table, columnPrefix + "_phone_no"));
        columns.add(Column.aliased("address_number", table, columnPrefix + "_address_number"));
        columns.add(Column.aliased("address_street", table, columnPrefix + "_address_street"));
        columns.add(Column.aliased("address_city", table, columnPrefix + "_address_city"));
        columns.add(Column.aliased("lead_source", table, columnPrefix + "_lead_source"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));

        columns.add(Column.aliased("org_id", table, columnPrefix + "_org_id"));
        return columns;
    }
}
