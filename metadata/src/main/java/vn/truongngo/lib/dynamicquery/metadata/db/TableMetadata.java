package vn.truongngo.lib.dynamicquery.metadata.db;

import java.util.List;

/**
 * Represents the metadata of a database table used in dynamic SQL query construction.
 * This interface provides information about the table's name, schema, and its columns.
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface TableMetadata {

    /**
     * Returns the name of the table to be used in SQL queries.
     *
     * @return The name of the table.
     */
    String getTableName();

    /**
     * Returns the schema name of the table, if applicable.
     * This is used when the SQL dialect requires fully-qualified table names.
     *
     * @return The schema name, or {@code null} if none is specified.
     */
    String getSchemaName();

    /**
     * Returns the list of column metadata for this table.
     * This is used to access details about columns when constructing query components such as SELECT, WHERE, and ORDER BY.
     *
     * @return A list of {@link ColumnMetadata} representing the table's columns.
     */
    List<ColumnMetadata> getColumns();

}
