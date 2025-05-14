package vn.truongngo.lib.dynamicquery.metadata.entity;

import vn.truongngo.lib.dynamicquery.metadata.db.ColumnMetadata;

import java.sql.JDBCType;

/**
 * Represents metadata of a Java field used in query construction.
 * This interface bridges the gap between an object's field and the corresponding database column.
 *
 * <p>It provides information such as the Java field type, JDBC type, field name, and the associated column metadata
 * from the database schema. This abstraction is useful in dynamic query systems that map between object structures
 * and relational models.</p>
 *
 * <p>Unlike {@link ColumnMetadata}, which reflects database structure,
 * this interface reflects application-layer fields that are mapped to database columns.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface FieldMetadata {

    /**
     * Returns the Java type of the field.
     *
     * @return The Java class representing the field type.
     */
    Class<?> getFieldType();

    /**
     * Returns the corresponding {@link JDBCType} of the field,
     * used for query parameter binding and SQL type compatibility.
     *
     * @return The JDBC type of the field.
     */
    JDBCType getJdbcType();

    /**
     * Returns the name of the field in the Java class.
     *
     * @return The name of the Java field.
     */
    String getFieldName();

    /**
     * The index of the field in its declaration context, such as its order in a list or query projection.
     *
     * @return The field index
     */
    int getIndex();

    /**
     * Returns the metadata of the database column that this field maps to.
     *
     * @return The associated {@link ColumnMetadata}.
     */
    ColumnMetadata getColumnMetadata();

}
