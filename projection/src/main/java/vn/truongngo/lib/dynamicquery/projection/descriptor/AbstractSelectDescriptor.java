package vn.truongngo.lib.dynamicquery.projection.descriptor;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Field;

/**
 * Abstract base class for {@link SelectDescriptor} used in query projections.
 * <p>
 * Provides common fields such as {@code alias} and {@code index} for all select descriptor implementations.
 * It also holds the reflected {@link Field} object corresponding to the projection field, allowing
 * access to field metadata and value extraction through reflection.
 * </p>
 *
 * <p>
 * Subclasses should extend this class to inherit these common properties and may add
 * additional metadata or behavior specific to their selection type (e.g., column, aggregate, expression).
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
@SuperBuilder
public class AbstractSelectDescriptor implements SelectDescriptor {

    /**
     * The alias used to reference the selected column or expression in the query.
     * This typically corresponds to the name of the projection field or a custom alias.
     */
    private String alias;

    /**
     * The zero-based index of the selection in the projection class declaration.
     * This preserves the order of fields as declared in the projection class.
     */
    private Integer index;

    /**
     * The reflection {@link Field} instance representing the projection field associated with this selection.
     * <p>
     * This allows access to field metadata, annotations, and enables retrieval of field values via reflection.
     * May be {@code null} if the selection does not map directly to a Java field (e.g., for computed expressions).
     * </p>
     */
    private Field field;

}
