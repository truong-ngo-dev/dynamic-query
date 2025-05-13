package vn.truongngo.lib.dynamicquery.metadata.db;

/**
 * Interface describing the metadata of a column in a database table, used for building and executing queries.
 * The methods in this interface provide information about the column's name, definition, nullability, primary key status,
 * and column index to assist in constructing SQL queries dynamically.
 *
 * <p>This is useful for extracting column information and generating query components in dynamic query systems.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface ColumnMetadata {

    /**
     * Retrieves the name of the column
     *
     * @return The name of the column.
     */
    String getColumnName();

    /**
     * Retrieves the definition of the column's data type
     *
     * @return The detailed definition of the column.
     */
    String getColumnDefinition();

    /**
     * Checks whether the column allows NULL values, which impacts the construction of conditions in a query.
     *
     * @return {@code true} if the column can contain NULL values, {@code false} otherwise.
     */
    boolean isNullable();

    /**
     * Checks whether the column is a primary key
     *
     * @return {@code true} if the column is a primary key, {@code false} otherwise.
     */
    boolean isPrimaryKey();

    /**
     * Retrieves the index of the column within the table, useful for identifying the column's position when constructing a SELECT query.
     *
     * @return The index of the column within the table.
     */
    int getColumnIndex();

}
