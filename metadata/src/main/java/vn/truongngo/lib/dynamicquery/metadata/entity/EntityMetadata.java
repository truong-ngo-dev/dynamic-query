package vn.truongngo.lib.dynamicquery.metadata.entity;

import vn.truongngo.lib.dynamicquery.metadata.db.TableMetadata;

import java.util.List;

/**
 * Represents metadata of an entity class used for dynamic query construction.
 * This interface encapsulates the relationship between a Java entity class,
 * its fields, and the corresponding database table structure.
 *
 * <p>It provides access to the entity's Java class, its field-level metadata,
 * and the underlying database table metadata, enabling object-relational mapping
 * for query generation purposes only.</p>
 *
 * <p>This abstraction is especially useful in dynamic query systems where queries
 * are built based on entity models without relying on ORM frameworks at runtime.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface EntityMetadata {

    /**
     * Returns the Java class representing the entity.
     *
     * @return The entity class.
     */
    Class<?> getEntityClass();

    /**
     * Returns the list of field metadata representing fields in the entity class.
     * Each field is typically mapped to a database column.
     *
     * @return A list of {@link FieldMetadata} for the entity's fields.
     */
    List<FieldMetadata> getFields();

    /**
     * Returns the table metadata that the entity is mapped to.
     *
     * @return The associated {@link TableMetadata}.
     */
    TableMetadata getTableMetadata();


}
