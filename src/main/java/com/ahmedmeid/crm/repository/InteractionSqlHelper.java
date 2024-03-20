package com.ahmedmeid.crm.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class InteractionSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("interaction_timestamp", table, columnPrefix + "_interaction_timestamp"));
        columns.add(Column.aliased("type", table, columnPrefix + "_type"));
        columns.add(Column.aliased("summary", table, columnPrefix + "_summary"));

        columns.add(Column.aliased("contact_id", table, columnPrefix + "_contact_id"));
        return columns;
    }
}
