package com.ahmedmeid.crm.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class OrganizationSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("organization_name", table, columnPrefix + "_organization_name"));
        columns.add(Column.aliased("industry", table, columnPrefix + "_industry"));
        columns.add(Column.aliased("website", table, columnPrefix + "_website"));
        columns.add(Column.aliased("phone_number", table, columnPrefix + "_phone_number"));
        columns.add(Column.aliased("address", table, columnPrefix + "_address"));

        return columns;
    }
}
