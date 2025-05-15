package vn.truongngo.lib.dynamicquery.metadata.jpa;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.truongngo.lib.dynamicquery.metadata.db.TableMetadata;
import vn.truongngo.lib.dynamicquery.metadata.entity.EntityMetadata;
import vn.truongngo.lib.dynamicquery.metadata.entity.FieldMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents metadata for a JPA entity, including its class, table, and field mappings.
 * <p>
 * This class serves as a high-level structure to describe how an entity is mapped to the database,
 * including basic fields, ID fields, and join columns (both simple and composite).
 * </p>
 *
 * <p>
 * It plays a central role in the dynamic query system by providing necessary metadata to interpret
 * how entities are related and how to construct SQL fragments from entity relationships.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
@Builder
public class JpaEntityMetadata implements EntityMetadata {

    /**
     * The Java class representing the JPA entity.
     */
    private Class<?> entityClass;

    /**
     * Metadata describing the corresponding database table
     * that this entity is mapped to.
     */
    private TableMetadata tableMetadata;

    /**
     * List of all fields in the entity, including:
     * <ul>
     *   <li>Basic fields (e.g., name, age)</li>
     *   <li>Identifiers (e.g., annotated with {@code @Id})</li>
     *   <li>Join columns (e.g., annotated with {@code @JoinColumn})</li>
     * </ul>
     */
    private List<FieldMetadata> fields;

    /**
     * List of ID fields for the entity.
     * <p>
     * These fields are typically annotated with {@code @Id} or {@code @EmbeddedId}
     * in the JPA entity class.
     * </p>
     */
    private List<FieldMetadata> idFields;

    /**
     * List of join columns representing foreign key relationships.
     * These fields are mapped using {@code @JoinColumn}.
     */
    private List<JoinColumnFieldMetadata> joinFields;

    /**
     * List of composite join columns representing foreign key relationships
     * that are mapped using multiple {@code @JoinColumn} annotations.
     */
    private List<CompositeJoinColumnFieldMetadata> compositeJoinFields;

    /**
     * List of inverse join columns used for bidirectional relationships
     * where the current side is the inverse side (i.e., uses {@code mappedBy}).
     */
    private List<JoinColumnFieldMetadata> inverseJoinFields;

    /**
     * List of inverse composite join columns for bidirectional relationships
     * with composite foreign keys and {@code mappedBy} references.
     */
    private List<CompositeJoinColumnFieldMetadata> inverseCompositeJoinFields;

    /**
     * Adds a regular field to the list of entity fields.
     *
     * @param field the field metadata to add
     */
    public void addField(FieldMetadata field) {
        if (fields == null) fields = new ArrayList<>();
        fields.add(field);
    }

    /**
     * Adds an identifier field to the list of ID fields.
     *
     * @param field the ID field metadata to add
     */
    public void addIdField(FieldMetadata field) {
        if (idFields == null) idFields = new ArrayList<>();
        idFields.add(field);
    }

    /**
     * Adds a foreign key join field to the entity metadata.
     *
     * @param field the join column field metadata to add
     */
    public void addJoinField(JoinColumnFieldMetadata field) {
        if (joinFields == null) joinFields = new ArrayList<>();
        joinFields.add(field);
    }

    /**
     * Adds a composite foreign key join field to the entity metadata.
     *
     * @param field the composite join column field metadata to add
     */
    public void addCompositeJoinField(CompositeJoinColumnFieldMetadata field) {
        if (compositeJoinFields == null) compositeJoinFields = new ArrayList<>();
        compositeJoinFields.add(field);
    }

    /**
     * Adds an inverse join column field, used when the current entity
     * is the inverse side of a relationship.
     *
     * @param field the inverse join column field metadata to add
     */
    public void addInverseJoinField(JoinColumnFieldMetadata field) {
        if (inverseJoinFields == null) inverseJoinFields = new ArrayList<>();
        inverseJoinFields.add(field);
    }

    /**
     * Adds an inverse composite join column field, used for
     * bidirectional relationships with composite foreign keys.
     *
     * @param field the inverse composite join column field metadata to add
     */
    public void addInverseCompositeJoinField(CompositeJoinColumnFieldMetadata field) {
        if (inverseCompositeJoinFields == null) inverseCompositeJoinFields = new ArrayList<>();
        inverseCompositeJoinFields.add(field);
    }
}
