package vn.truongngo.lib.dynamicquery.metadata.db;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Default implementation of {@link TableMetadata} representing metadata
 * of a relational database table for query construction.
 *
 * <p>This class includes the table's name, optional schema name,
 * and a list of its column metadata. It is primarily used by the dynamic query engine
 * to understand the structure of a table for generating SQL queries.</p>
 *
 * @see TableMetadata
 * @see ColumnMetadata
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
public class DefaultTableMetadata implements TableMetadata {

    /**
     * The name of the table in the database.
     */
    private String tableName;

    /**
     * The name of the schema that contains the table (nullable if default schema is used).
     */
    private String schemaName;

    /**
     * A list of metadata describing the table's columns.
     */
    private List<ColumnMetadata> columns;

}
