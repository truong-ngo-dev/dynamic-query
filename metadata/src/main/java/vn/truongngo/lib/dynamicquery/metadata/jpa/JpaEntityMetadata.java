package vn.truongngo.lib.dynamicquery.metadata.jpa;

import lombok.Getter;
import lombok.Setter;
import vn.truongngo.lib.dynamicquery.metadata.db.TableMetadata;
import vn.truongngo.lib.dynamicquery.metadata.entity.DefaultEntityMetadata;
import vn.truongngo.lib.dynamicquery.metadata.entity.EntityMetadata;
import vn.truongngo.lib.dynamicquery.metadata.entity.FieldMetadata;

import java.util.List;

/**
 * Represents metadata for a JPA entity, including its class, table, and field mappings.
 * <p>
 * This class serves as a high-level structure to describe how an entity is mapped to the database,
 * including basic fields, ID fields, and join columns (both simple and composite).
 * </p>
 *
 * @author Truong
 * @version 1.0
 */
@Getter
@Setter
public class JpaEntityMetadata implements EntityMetadata {

    /**
     * The Java class representing the JPA entity.
     */
    private Class<?> entityClass;

    /**
     * Metadata describing the corresponding database table.
     */
    private TableMetadata tableMetadata;

    /**
     * List of all fields in the entity, including regular, ID, and join fields.
     */
    private List<FieldMetadata> fields;

    /**
     * List of ID fields for the entity.
     * Typically mapped using {@code @Id} or {@code @EmbeddedId}.
     */
    private List<DefaultEntityMetadata> idFields;

    /**
     * List of join columns representing foreign key relationships using {@code @JoinColumn}.
     */
    private List<JoinColumnFieldMetadata> joinFields;

    /**
     * List of composite join columns representing composite foreign key relationships
     * using multiple {@code @JoinColumn} annotations.
     */
    private List<CompositeJoinColumnFieldMetadata> compositeJoinFields;

    /**
     * List of inverse join columns for relationships mapped by another side (e.g., {@code mappedBy}).
     */
    private List<JoinColumnFieldMetadata> inverseJoinFields;

    /**
     * List of inverse composite join columns for relationships mapped by another side
     * with composite foreign keys.
     */
    private List<CompositeJoinColumnFieldMetadata> inverseCompositeJoinFields;

}
