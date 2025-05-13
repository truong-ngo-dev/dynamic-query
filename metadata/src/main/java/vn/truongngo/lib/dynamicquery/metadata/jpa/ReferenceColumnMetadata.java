package vn.truongngo.lib.dynamicquery.metadata.jpa;

/**
 * Represents metadata for a referenced column in a JPA entity relationship.
 *
 * <p>This interface is typically used to capture the target column name
 * in JPA association mappings (e.g., {@code @ManyToOne}, {@code @OneToOne}) for
 * dynamic query generation purposes.</p>
 *
 * <p>It is not intended to model full relational constraints like foreign keys,
 * but only to provide reference information needed to build joins or relationship-based queries.</p>
 *
 * <p>This interface is specific to the JPA metadata extraction module.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface ReferenceColumnMetadata {

    /**
     * Returns the name of the column being referenced in the relationship.
     *
     * <p>This typically corresponds to the {@code referencedColumnName}
     * attribute in JPA annotations like {@code @JoinColumn}.</p>
     *
     * @return the name of the referenced column in the target entity's table
     */
    String getReferenceColumnName();
}
