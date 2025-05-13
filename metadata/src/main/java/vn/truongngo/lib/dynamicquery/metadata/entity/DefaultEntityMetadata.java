package vn.truongngo.lib.dynamicquery.metadata.entity;

import lombok.Getter;
import lombok.Setter;
import vn.truongngo.lib.dynamicquery.metadata.db.TableMetadata;

import java.util.List;

/**
 * Default implementation of {@link EntityMetadata}, representing metadata
 * of a Java entity class for dynamic query construction.
 *
 * <p>This class contains the Java type of the entity, its associated fields,
 * and the corresponding database table metadata. It serves as the central
 * mapping layer between object structures and relational database schemas.</p>
 *
 * <p>This metadata is used exclusively for dynamic query generation, not for ORM
 * or runtime persistence operations.</p>
 *
 * @see EntityMetadata
 * @see FieldMetadata
 * @see TableMetadata
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
public class DefaultEntityMetadata implements EntityMetadata {

    /**
     * The Java class representing the entity.
     */
    private Class<?> entityClass;

    /**
     * The list of field metadata associated with the entity.
     * Each field maps to a database column.
     */
    private List<FieldMetadata> fields;

    /**
     * The metadata representing the database table mapped to the entity.
     */
    private TableMetadata tableMetadata;

}
