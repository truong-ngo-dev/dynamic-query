package vn.truongngo.lib.dynamicquery.projection.descriptor;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base class for {@link SelectDescriptor} used in query projections.
 * <p>
 * Provides common fields such as {@code alias} and {@code index} for all select descriptor implementations.
 * </p>
 *
 * <p>
 * Subclasses should extend this class to inherit these properties and may add additional metadata as needed.
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
     */
    private String alias;

    /**
     * The zero-based index of the selection in the projection class declaration.
     */
    private Integer index;

}
