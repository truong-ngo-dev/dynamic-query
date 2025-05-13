package vn.truongngo.lib.dynamicquery.metadata.db;

import lombok.Getter;
import lombok.Setter;

/**
 * Default implementation of {@link ColumnMetadata} representing metadata
 * of a database column for dynamic query generation.
 *
 * <p>This class includes information such as the column name, SQL definition,
 * whether the column allows null values, if it's part of the primary key,
 * and its position in the table schema.</p>
 *
 * <p>It is used internally by the dynamic query system to translate
 * object structures into relational SQL components.</p>
 *
 * @see ColumnMetadata
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
public class DefaultColumnMetadata implements ColumnMetadata {

    /**
     * The name of the column in the database.
     */
    private String columnName;

    /**
     * The SQL definition of the column, such as type or constraints.
     */
    private String columnDefinition;

    /**
     * Whether this column allows NULL values.
     */
    private boolean nullable;

    /**
     * Whether this column is part of the table's primary key.
     */
    private boolean primaryKey;

    /**
     * The zero-based index of the column in the table schema.
     */
    private int columnIndex;

}
