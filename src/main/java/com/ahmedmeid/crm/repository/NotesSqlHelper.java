package com.ahmedmeid.crm.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class NotesSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("note_timestamp", table, columnPrefix + "_note_timestamp"));
        columns.add(Column.aliased("note", table, columnPrefix + "_note"));

        columns.add(Column.aliased("contact_id", table, columnPrefix + "_contact_id"));
        return columns;
    }
}
